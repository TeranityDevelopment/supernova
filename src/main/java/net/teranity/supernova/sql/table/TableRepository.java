package net.teranity.supernova.sql.table;

import net.teranity.supernova.dependecyinjection.DependencyInjectionContainer;
import net.teranity.supernova.dependecyinjection.annotations.AutoInject;
import net.teranity.supernova.sql.query.QueryExecutor;
import net.teranity.supernova.sql.table.annotations.ColumnEntity;
import net.teranity.supernova.sql.table.annotations.TableEntity;
import net.teranity.supernova.util.ReflectionsUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ExecutionException;

public abstract class TableRepository {

    @AutoInject
    private ReflectionsUtil reflectionsUtil;

    private String tableName;

    public TableRepository() {
        DependencyInjectionContainer.injectDependencies(this);

        final Set<Class<?>> tableEntities = reflectionsUtil.reflections.getTypesAnnotatedWith(TableEntity.class);
        for (Class<?> clazz : tableEntities) {
            if (clazz == getClass()) {
                TableEntity table = clazz.getAnnotation(TableEntity.class);
                this.tableName = table.tableName();
            }
        }
    }

    public void save() {
        Map<String, Object> records = getStringObjectMap();

        StringBuilder columns = new StringBuilder();
        for (String columnName : records.keySet()) {
            columns.append(columns).append(", ");
        }
        columns.deleteCharAt(columns.length() - 1);

        StringBuilder recordsValues = new StringBuilder();
        recordsValues.append("?, ".repeat(records.size()));
        recordsValues.deleteCharAt(recordsValues.length() - 1);

        String query = "insert into " + tableName + " (" + columns + ") values (" + recordsValues + ")";
        QueryExecutor queryExecutor = QueryExecutor.build()
                .query(query)
                .records(records.values().stream().toList())
                .build();

        try {
            queryExecutor.start().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void update() {

    }

    public void delete() {

    }

    public void sync() {

    }

    private Map<String, Object> getStringObjectMap() {
        Map<String, Object> records = new HashMap<>();

        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ColumnEntity.class)) {
                ColumnEntity columnEntity = field.getAnnotation(ColumnEntity.class);

                try {
                    String columnName = columnEntity.name();
                    Object record = field.get(this);

                    records.put(columnName, record);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return records;
    }
}
