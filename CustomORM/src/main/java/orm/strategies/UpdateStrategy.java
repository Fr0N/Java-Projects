package orm.strategies;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import orm.strategies.table_creator.TableCreator;
import orm.strategies.table_utils.TableUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class UpdateStrategy extends AbstactStrategy{

    protected UpdateStrategy(Connection connection,
                                 TableCreator tableCreator,
                                 String dataSource) {
        super(connection, tableCreator, dataSource);
    }

    @Override
    public void execute() throws SQLException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        //Delete tables for non existing entities
        List<Class> allEntities = getAllEntities();

        for (Class entity : allEntities) {
            Field primary = this.getId(entity);
            primary.setAccessible(true);

            this.updateTable(entity, primary);
        }
    }

    private Field getId(Class entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Object does not have id"));
    }

    private boolean updateTable(Class entity, Field field) throws IllegalAccessException, SQLException {
        String tableName = TableUtils.getTableName(entity);

        if (!this.checkIfTableExists(tableName)) {
            this.tableCreator.doCreate(entity);
        }

        Field[] fields = entity.getDeclaredFields();
        //Delete non present fields
        for (int i = 0; i < fields.length; i++) {
            Field currentField = fields[i];
            currentField.setAccessible(true);

            Column columnAnnotation = currentField.getAnnotation(Column.class);
            if (!this.checkIfFieldExists(tableName, columnAnnotation.name())) {
                this.doAlter(tableName, currentField);
            }
        }

        return false;
    }

    private boolean checkIfTableExists(String tableName) throws SQLException {
        String query = String.format("SELECT COUNT(*) AS 'count' FROM information_schema.tables WHERE table_schema = '%s' AND table_name = '%s';", this.dataSource, tableName);
        ResultSet rs = this.connection.prepareStatement(query).executeQuery();

        rs.next();
        if (rs.getInt("count") == 0) {
            return false;
        }

        return true;
    }

    private <E> boolean checkIfFieldExists(String tableName, String fieldName) throws SQLException {
        String query = String.format("SELECT COUNT(*) AS 'count'\n" +
                "FROM information_schema.COLUMNS \n" +
                "WHERE \n" +
                "    TABLE_SCHEMA = '%s' \n" +
                "AND TABLE_NAME = '%s' \n" +
                "AND COLUMN_NAME = '%s'", this.dataSource, tableName, fieldName);


        ResultSet rs = this.connection.prepareStatement(query).executeQuery();

        rs.next();
        if (rs.getInt("count") == 0) {
            return false;
        }

        return true;
    }

    private void doAlter(String tableName, Field field) throws SQLException {
        Column columnAnnotation = field.getAnnotation(Column.class);
        String query = String.format("ALTER TABLE %s.%s ADD COLUMN %s %s", this.dataSource, tableName, columnAnnotation.name(), TableUtils.getDbType(field));
        this.connection.prepareStatement(query).execute();
    }

    public void doDelete(Class<?> table, String criteria) throws Exception {
        String tableName = table.getAnnotation(Entity.class).name();

        if (!this.checkIfTableExists(tableName)) {
            throw new Exception("Table doen't exist.");
        }

        String query = String.format("DELETE FROM %s WHERE %s", tableName, criteria);
        this.connection.prepareStatement(query).execute();
    }
}
