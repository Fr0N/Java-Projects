package orm.strategies.table_utils;

import annotations.Entity;

import java.lang.reflect.Field;

public class TableUtils {

    public static String getTableName(Class<?> entity) {
        String tableName = "";

        if (entity.isAnnotationPresent(Entity.class)) {
            Entity annotation = (Entity) entity.getAnnotation(Entity.class);
            tableName = annotation.name();
        }

        if (tableName.equals("")) {
            tableName = entity.getClass().getSimpleName();
        }

        return tableName;
    }

    public static String getDbType(Field field) {
        String mySQLType = "";

        switch (field.getType().getSimpleName()) {
            case "int":
            case "Integer":
                mySQLType = "INT";
                break;
            case "String":
                mySQLType = "VARCHAR(50)";
                break;
            case "Date":
                mySQLType = "DATETIME";
                break;
        }

        return mySQLType;
    }
}
