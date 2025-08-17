package supernova.event.defaults;

import supernova.event.Cancellable;
import supernova.event.EventBase;
import supernova.sql.SQLConnection;

public class ConnectionIniEvent extends EventBase implements Cancellable {

    private final SQLConnection sqlConnection;
    private boolean cancelled;

    private String connectedMessage;

    public ConnectionIniEvent(SQLConnection sqlConnection) {
        super(true);
        this.sqlConnection = sqlConnection;

        this.connectedMessage = "Connected to SQL for the first time.";
    }

    public SQLConnection getSqlConnection() {
        return sqlConnection;
    }

    public String getConnectedMessage() {
        return connectedMessage;
    }

    public void setConnectedMessage(String connectedMessage) {
        this.connectedMessage = connectedMessage;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
