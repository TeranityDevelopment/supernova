package net.teranity.supernova.sql.query;

import net.teranity.supernova.dependecyinjection.DependencyInjectionContainer;
import net.teranity.supernova.dependecyinjection.annotations.AutoInject;
import net.teranity.supernova.sql.SQLConnection;
import net.teranity.supernova.sql.query.callback.SQLCallback;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class QueryGetter<T> {

    @AutoInject
    private SQLConnection sqlConnection;

    private final String query;
    private final SQLCallback callback;
    private final List<Object> records;

    private T reference;
    private CompletableFuture<Void> future;

    private QueryGetter(String query, SQLCallback callback, List<Object> records) {
        DependencyInjectionContainer.injectDependencies(this);

        this.query = query;
        this.callback = callback;
        this.records = records;
    }

    public CompletableFuture<Void> start() {
        future = CompletableFuture.runAsync(() -> {
            try (PreparedStatement preparedStatement = sqlConnection.getConnection().prepareStatement(query)) {
                if (records != null) {
                    for (int i = 0; i < records.size(); i++) {
                        preparedStatement.setObject((i + 1), records.get(i));
                    }
                }

                this.reference = callback.call(preparedStatement.executeQuery());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, sqlConnection.getExecutorService());
        return future;
    }

    public Optional<T> get() {
        if (future == null) {
            throw new IllegalStateException("You must call start() before get()");
        }
        try {
            future.join();
        } catch (Exception e) {
            throw new RuntimeException("Error while waiting for query to finish", e);
        }

        return Optional.ofNullable(reference);
    }

}
