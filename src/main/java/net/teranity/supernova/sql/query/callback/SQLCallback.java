package net.teranity.supernova.sql.query.callback;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface SQLCallback {

    void call();

    <T> T call(ResultSet resultSet) throws SQLException;
}
