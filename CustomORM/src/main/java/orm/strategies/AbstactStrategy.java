package orm.strategies;

import orm.scanner.EntityScanner;
import orm.strategies.table_creator.TableCreator;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.List;

public abstract class AbstactStrategy implements SchemaInitializationStrategy {

    protected Connection connection;
    protected String dataSource;
    protected TableCreator tableCreator;

    protected AbstactStrategy(Connection connection,
                              TableCreator tableCreator,
                              String dataSource) {
        this.connection = connection;
        this.tableCreator = tableCreator;
        this.dataSource = dataSource;
    }

    List<Class> getAllEntities() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return EntityScanner.getAllEntities(System.getProperty("user.dir"));
    }

}
