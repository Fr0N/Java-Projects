package orm.persistence;

import annotations.Column;
import annotations.Entity;
import annotations.Id;
import orm.strategies.SchemaInitializationStrategy;
import orm.strategies.table_utils.TableUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EntityManager implements DbContext {
    private SchemaInitializationStrategy strategy;
    private String dataSouce;
    private Connection connection;

    public EntityManager(Connection connection, String dataSource, SchemaInitializationStrategy strategy) throws SQLException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException {
        this.connection = connection;
        this.dataSouce = dataSource;
        this.strategy = strategy;

        this.strategy.execute();
    }

    public <E> boolean persist(E entity) throws IllegalAccessException, SQLException {
        Field primary = this.getId(entity.getClass());
        primary.setAccessible(true);
        Object value = primary.get(entity);

        if (value == null || (int) value <= 0) {
            return this.doInsert(entity, primary);
        }

        return this.doUpdate(entity, primary);
    }

    public <E>  Iterable<E> find(Class<E> table) throws SQLException, IllegalAccessException, InstantiationException {
        String query = "SELECT * FROM " + TableUtils.getTableName(table);
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);

        List<E> result = new ArrayList<E>();

        while (rs.next()) {
            E entity = table.newInstance();
            this.fillEntity(table, rs, entity);
            result.add(entity);
        }


        return result;
    }

    public <E> Iterable<E> find(Class<E> table, String where) throws SQLException, IllegalAccessException, InstantiationException {
        String query = "SELECT * FROM " + TableUtils.getTableName(table) + " WHERE " + where;
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);

        List<E> result = new ArrayList<E>();

        while (rs.next()) {
            E entity = table.newInstance();
            this.fillEntity(table, rs, entity);
            result.add(entity);
        }


        return result;
    }

    public <E> E findFirst(Class<E> table) throws SQLException, IllegalAccessException, InstantiationException {
        String query = "SELECT * FROM " + TableUtils.getTableName(table) + " LIMIT 1";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        E entity = table.newInstance();
        this.fillEntity(table, rs, entity);
        return entity;
    }

    public <E> E findFirst(Class<E> table, String where) throws SQLException, IllegalAccessException, InstantiationException {
        if (where == null) {
            return this.findFirst(table);
        }

        String query = "SELECT * FROM " + TableUtils.getTableName(table) + " WHERE " + where + " LIMIT 1";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);
        rs.next();
        E entity = table.newInstance();
        this.fillEntity(table, rs, entity);
        return entity;
    }

    public void doDelete(Class<?> table, String criteria) throws Exception {
        String tableName = table.getAnnotation(Entity.class).name();

        if (!this.checkIfTableExists(tableName)) {
            throw new Exception("Table doen't exist.");
        }

        String query = String.format("DELETE FROM %s WHERE %s", tableName, criteria);
        this.connection.prepareStatement(query).execute();
    }

    private <E> void fillEntity(Class table, ResultSet rs, E entity) throws SQLException, IllegalAccessException {
        Field[] fields = table.getDeclaredFields();
        for (Field currentField : fields) {
            currentField.setAccessible(true);
            this.fillField(currentField, rs, currentField.getAnnotation(Column.class).name(), entity);
        }
    }

    private <E> void fillField(Field currentField, ResultSet rs, String columnName, E entity) throws SQLException, IllegalAccessException {
        if (currentField.getType().equals(Integer.class) || currentField.getType().equals(int.class)) {
            int value = rs.getInt(columnName);
            currentField.set(entity, value);
        } else if (currentField.getType().equals(Long.class) || currentField.getType().equals(long.class)) {
            long value = rs.getLong(columnName);
            currentField.set(entity, value);
        } else if (currentField.getType().equals(Double.class) || currentField.getType().equals(double.class)) {
            double value = rs.getDouble(columnName);
            currentField.set(entity, value);
        } else if (currentField.getType().equals(String.class)) {
            String value = rs.getString(columnName);
            currentField.set(entity, value);
        } else if (currentField.getType().equals(Date.class)) {
            Date value = rs.getDate(columnName);
            currentField.set(entity, value);
        }
    }

    private Field getId(Class entity) {
        return Arrays.stream(entity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Object does not have id"));
    }

    private <E> boolean doInsert(E entity, Field field) throws IllegalAccessException, SQLException {
        String tableName = TableUtils.getTableName(entity.getClass());

        if (!this.checkIfTableExists(tableName)) {
            this.doCreate(entity);
        }

        String query = "INSERT INTO " + tableName + " (";

        Field[] fields = entity.getClass().getDeclaredFields();

        List<String> fieldNames = new ArrayList<>();
        List<String> values = new ArrayList<>();

        for (int i = 0; i < fields.length; i++) {
            Field currentField = fields[i];
            currentField.setAccessible(true);

            Column columnAnnotation = currentField.getAnnotation(Column.class);
            if (!this.checkIfFieldExists(tableName, columnAnnotation.name())) {
                this.doAlter(tableName, currentField);
            }

            if (currentField.isAnnotationPresent(Id.class)) {
                continue;
            }

            if (currentField.get(entity) instanceof Date) {
                values.add("'" + new SimpleDateFormat("yyyy-MM-dd").format(currentField.get(entity)) + "'");
            } else {
                values.add("'" + currentField.get(entity).toString() + "'");
            }

            fieldNames.add("`" + currentField.getAnnotation(Column.class).name() + "`");
        }

        query += String.join(",", fieldNames) + ") VALUES (" + String.join(",", values) + ");";
        System.out.println(query);
        return connection.prepareStatement(query).execute();
    }

    private void doAlter(String tableName, Field field) throws SQLException {
        Column columnAnnotation = field.getAnnotation(Column.class);
        String query = String.format("ALTER TABLE %s ADD COLUMN %s %s", tableName, columnAnnotation.name(), TableUtils.getDbType(field));
        this.connection.prepareStatement(query).execute();
    }

    private <E> boolean doUpdate(E entity, Field primary) throws IllegalAccessException, SQLException {
        String query = "UPDATE " + TableUtils.getTableName(entity.getClass()) + " SET ";

        Field[] fields = entity.getClass().getDeclaredFields();

        String fieldNamesAndValues = "";
        String where = "";

        for (int i = 0; i < fields.length; i++) {
            Field currentField = fields[i];
            currentField.setAccessible(true);

            if (currentField.isAnnotationPresent(Id.class)) {
                where += "WHERE `" + currentField.getAnnotation(Column.class).name() + "` = " + currentField.get(entity);
                continue;
            } else {
                String currentValue = "";
                if (currentField.get(entity) instanceof Date) {
                    currentValue = new SimpleDateFormat("yyyy-MM-dd").format(currentField.get(entity));
                } else {
                    currentValue = currentField.get(entity).toString();
                }

                fieldNamesAndValues += "`" + currentField.getAnnotation(Column.class).name() + "` = '" + currentValue + "'";
            }

            if (i < fields.length - 1) {
                fieldNamesAndValues += ",";
            }
        }

        query += fieldNamesAndValues + where;
        return connection.prepareStatement(query).execute();
    }

    private <E> boolean doCreate(E entity) throws SQLException {
        String query = "CREATE TABLE " + TableUtils.getTableName(entity.getClass()) + "( ";

        Field[] fields = entity.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field currentField = fields[i];
            currentField.setAccessible(true);

            query += " `" + currentField.getAnnotation(Column.class).name() + "` " + TableUtils.getDbType(currentField);

            if (currentField.isAnnotationPresent(Id.class)) {
                query += " PRIMARY KEY AUTO_INCREMENT";
            }

            if (i < fields.length -1) {
                query += ",";
            }
        }

        query += ")";

        return this.connection.prepareStatement(query).execute();
    }

    private boolean checkIfTableExists(String tableName) throws SQLException {
        String query = "SELECT COUNT(*) AS 'count' FROM information_schema.tables WHERE table_schema = 'orm_db' AND table_name = '" + tableName + "';";
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
                "    TABLE_SCHEMA = 'orm_db' \n" +
                "AND TABLE_NAME = '%s' \n" +
                "AND COLUMN_NAME = '%s'", tableName, fieldName);

        ResultSet rs = this.connection.prepareStatement(query).executeQuery();

        rs.next();
        if (rs.getInt("count") == 0) {
            return false;
        }

        return true;
    }

    public Connection getConnection() {
        return this.connection;
    }
}
