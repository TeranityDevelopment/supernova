package net.teranity.supernova.test;

import net.teranity.supernova.sql.table.ColumnConstraint;
import net.teranity.supernova.sql.table.ColumnType;
import net.teranity.supernova.sql.table.TableRepository;
import net.teranity.supernova.sql.table.annotations.ColumnEntity;
import net.teranity.supernova.sql.table.annotations.TableEntity;

@TableEntity(tableName = "users")
public class UserExample extends TableRepository {

    @ColumnEntity(name = "name", type = ColumnType.VARCHAR, length = 255, constraints = {
            ColumnConstraint.UNIQUE,
            ColumnConstraint.NOT_NULL,
            ColumnConstraint.PRIMARY_KEY
    })
    private String name;

    @ColumnEntity(type = ColumnType.INT)
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
