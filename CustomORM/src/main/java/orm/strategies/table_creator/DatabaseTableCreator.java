package orm.strategies.table_creator;

import annotations.Column;
import annotations.Id;
import orm.strategies.table_utils.TableUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTableCreator implements TableCreator {
    private Connection connection;
    private String dataSource;

    public DatabaseTableCreator(Connection connection, String dataSource) {
        this.connection = connection;
        this.dataSource = dataSource;
    }

    @Override
    public boolean doCreate(Class entity) throws SQLException {
        String query = "CREATE TABLE " + this.dataSource + "." + TableUtils.getTableName(entity) + "( ";

        Field[] fields = entity.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field currentField = fields[i];
            currentField.setAccessible(true);

            query += " `" + currentField.getAnnotation(Column.class).name() + "` " + TableUtils.getDbType(currentField);

            if (currentField.isAnnotationPresent(Id.class)) {
                query += " PRIMARY KEY AUTO_INCREMENT";
            }

            if (i < fields.length - 1) {
                query += ",";
            }
        }

        query += ")";

        return this.connection.prepareStatement(query).execute();
    }
}
