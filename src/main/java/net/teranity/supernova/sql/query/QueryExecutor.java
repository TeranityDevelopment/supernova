package net.teranity.supernova.sql.query;

import net.teranity.supernova.dependecyinjection.DependencyInjectionContainer;
import net.teranity.supernova.dependecyinjection.annotations.AutoInject;
import net.teranity.supernova.sql.SQLConnection;
import net.teranity.supernova.sql.query.callback.SQLCallback;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class QueryExecutor {

    @AutoInject
    private SQLConnection sqlConnection;

    private final String query;
    private final SQLCallback callback;
    private final List<Object> records;

    private volatile boolean next;

    private QueryExecutor(String query, SQLCallback callback, List<Object> records) {
        DependencyInjectionContainer.injectDependencies(this);

        this.query = query;
        this.callback = callback;
        this.records = records;
    }

    public boolean next() {
        return next;
    }

    public CompletableFuture<Void> start() {
        return CompletableFuture.runAsync(() -> {
            try (PreparedStatement preparedStatement = sqlConnection.getConnection().prepareStatement(query)) {
                if (records != null) {
                    for (int i = 0; i < records.size(); i++) {
                        preparedStatement.setObject((i + 1), records.get(i));
                    }
                }

                preparedStatement.executeUpdate();
                if (callback != null) {
                    callback.call();
                }

                next = true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, sqlConnection.getExecutorService());
    }

    public static Builder build() {
        return new Builder();
    }

    public static class Builder {

        private String query;
        private SQLCallback callback;
        private List<Object> records;

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder callback(SQLCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder records(List<Object> records) {
            this.records = records;
            return this;
        }

        public QueryExecutor build() {
            return new QueryExecutor(query, callback, records);
        }
    }
}
