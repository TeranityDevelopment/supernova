package net.teranity.supernova.sql.table;

public enum ColumnConstraint {
    PRIMARY_KEY("PRIMARY KEY"),
    NOT_NULL("NOT NULL"),
    UNIQUE("UNIQUE"),
    AUTO_INCREMENT("AUTO_INCREMENT");

    private final String value;

    ColumnConstraint(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
