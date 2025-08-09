package net.teranity.supernova.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.teranity.supernova.dependecyinjection.DependencyInjectionContainer;
import net.teranity.supernova.dependecyinjection.annotations.AutoInject;
import net.teranity.supernova.sql.query.QueryExecutor;
import net.teranity.supernova.sql.table.ColumnConstraint;
import net.teranity.supernova.sql.table.annotations.ColumnEntity;
import net.teranity.supernova.sql.table.annotations.TableEntity;
import net.teranity.supernova.util.ReflectionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

public class SQLConnection {

    private final ExecutorService executorService;
    private final Logger logger;

    private final String url;

    private final String username;
    private final String password;

    private final Map<String, Object> properties;

    private final Connection connection;

    @AutoInject
    private ReflectionsUtil reflectionsUtil;

    private final CompletableFuture<Void> initFuture;
    private final Map<Class<?>, Object> tables;

    private SQLConnection(String url, String username, String password, Map<String, Object> properties) {
        this.executorService = Executors.newCachedThreadPool();
        this.logger = LoggerFactory.getLogger(SQLConnection.class);

        this.url = url;
        this.username = username;
        this.password = password;
        this.properties = properties;

        final HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            hikariConfig.addDataSourceProperty(entry.getKey(), entry.getValue());
        }

        try (HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig)) {
            this.connection = hikariDataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        DependencyInjectionContainer.initialize(SQLConnection.class, this);

        this.tables = new ConcurrentHashMap<>();
        this.initFuture = CompletableFuture.runAsync(this::initializeTables, executorService);
    }

    public void awaitTablesReady() throws ExecutionException, InterruptedException {
        initFuture.get();
    }

    private void initializeTables() {
        final Set<Class<?>> tableEntities = reflectionsUtil.reflections.getTypesAnnotatedWith(TableEntity.class);

        for (Class<?> entity : tableEntities) {
            TableEntity tableEntityAnnotation = entity.getAnnotation(TableEntity.class);

            try {
                String tableName = tableEntityAnnotation.tableName();
                DatabaseMetaData databaseMetaData = connection.getMetaData();

                try (ResultSet tables = databaseMetaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
                    if (!tables.next()) {
                        StringBuilder columnsBuilder = new StringBuilder();
                        ColumnEntity[] columnEntities = entity.getAnnotationsByType(ColumnEntity.class);
                        String columnName;

                        for (int i = 0; i < columnEntities.length; i++) {
                            ColumnEntity col = columnEntities[i];
                            if (col.name().isEmpty()) {
                                columnName = Arrays.stream(entity.getDeclaredFields())
                                        .filter(f -> f.isAnnotationPresent(ColumnEntity.class) && f.getAnnotation(ColumnEntity.class) == col)
                                        .findFirst()
                                        .map(Field::getName)
                                        .orElseThrow(() -> new RuntimeException("No field found for unnamed column in " + entity.getName()));
                            } else {
                                columnName = col.name();
                            }

                            columnsBuilder.append(columnName).append(" ").append(col.type());

                            if (col.length() > 0) {
                                columnsBuilder.append("(").append(col.length()).append(")");
                            }

                            for (ColumnConstraint constraint : col.constraints()) {
                                columnsBuilder.append(" ").append(constraint.getValue());
                            }

                            if (!col.defaultVal().isEmpty()) {
                                columnsBuilder.append(" DEFAULT '").append(col.defaultVal()).append("'");
                            }

                            if (!col.comment().isEmpty()) {
                                columnsBuilder.append(" COMMENT '").append(col.comment()).append("'");
                            }

                            if (i < columnEntities.length - 1) {
                                columnsBuilder.append(", ");
                            }
                        }

                        String query = "CREATE TABLE " + tableName + " (" + columnsBuilder + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

                        QueryExecutor queryExecutor = QueryExecutor.build()
                                .query(query)
                                .build();

                        try {
                            queryExecutor.start().get();
                            this.tables.put(entity, entity.getDeclaredConstructor().newInstance());
                        } catch (InterruptedException | ExecutionException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException |
                                 InstantiationException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Optional<Object> getProperty(String propertyName) {
        return Optional.ofNullable(getProperties().get(propertyName));
    }

    public Connection getConnection() {
        return connection;
    }

    public Map<Class<?>, Object> getTables() {
        return tables;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String url;

        private String username;
        private String password;

        private final Map<String, Object> properties = new HashMap<>();

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withCredential(String username, String password) {
            this.username = username;
            this.password = password;

            return this;
        }

        public Builder withCredential(String username) {
            this.username = username;
            return this;
        }

        public Builder addProperty(String name, Object value) {
            properties.put(name, value);
            return this;
        }

        public SQLConnection build() {
            return new SQLConnection(url, username, password, properties);
        }
    }
}
