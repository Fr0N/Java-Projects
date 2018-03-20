package orm.strategies;

import entities.User;
import orm.strategies.table_creator.TableCreator;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DropCreateStrategy extends AbstactStrategy {
    private final String DROP_DATABASE_QUERY = "DROP DATABASE IF EXISTS `%s`;";
    private final String CREATE_DATABASE_QUERY = "CREATE DATABASE `%s`;";

    protected DropCreateStrategy(Connection connection,
                                 TableCreator tableCreator,
                                 String dataSource) {
        super(connection, tableCreator, dataSource);
    }

    @Override
    public void execute() throws SQLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        String dropQuery = String.format(DROP_DATABASE_QUERY, dataSource);
        this.connection.prepareStatement(dropQuery).execute();

        String createQuery = String.format(CREATE_DATABASE_QUERY, dataSource);
        this.connection.prepareStatement(createQuery).execute();

        List<Class> entities = this.getAllEntities();
        this.createTables(entities);

    }

    private void createTables(List<Class> entities) throws SQLException {
        for (Class<User> entity : entities) {
            this.tableCreator.doCreate(entity);
        }
    }
}
