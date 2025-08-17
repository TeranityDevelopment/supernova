package supernova.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import supernova.event.EventManager;
import supernova.event.defaults.ConnectionIniEvent;
import supernova.util.MultiValue;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SQLConnection {

    private final Logger logger;

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;

    private String jdbcUrl;
    private MultiValue<String, String> credential;
    private final Map<String, Object> dataSourceProperties;

    private HikariDataSource hikariDataSource;
    private Connection connection;

    public SQLConnection(String jdbcUrl, MultiValue<String, String> credential, Map<String, Object> dataSourceProperties) {
        this.logger = LoggerFactory.getLogger(SQLConnection.class);

        this.executorService = Executors.newFixedThreadPool(4);
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);

        this.jdbcUrl = jdbcUrl;
        this.credential = credential;
        this.dataSourceProperties = dataSourceProperties;

        ConnectionIniEvent connectionIniEvent = new ConnectionIniEvent(this);
        EventManager.fire(connectionIniEvent);

        if (connectionIniEvent.isCompleted()) {
            if (!connectionIniEvent.isCancelled()) {
                try {
                    reconnect();
                    logger.info(connectionIniEvent.getConnectedMessage());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                logger.error("Failed to connect to SQL due to cancellation from an event.");
            }
        }
    }


    public Connection reconnect() throws SQLException {
        if (isAlive()) {
            shutdown();
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(credential.first());
        hikariConfig.setPassword(credential.second());

        dataSourceProperties.forEach(hikariConfig::addDataSourceProperty);

        this.hikariDataSource = new HikariDataSource(hikariConfig);
        this.connection = hikariDataSource.getConnection();

        return connection;
    }

    public void shutdown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (hikariDataSource != null) {
            hikariDataSource.close();
        }
    }

    public boolean isAlive() throws SQLException {
        return connection != null && connection.isValid(10);
    }

    public Logger getLogger() {
        return logger;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public MultiValue<String, String> getCredential() {
        return credential;
    }

    public void setCredential(MultiValue<String, String> credential) {
        this.credential = credential;
    }

    public Map<String, Object> getDataSourceProperties() {
        return dataSourceProperties;
    }

    public Connection getConnection() {
        return connection;
    }
}
