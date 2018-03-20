package orm.persistence;

import orm.strategies.SchemaInitializationStrategy;
import orm.strategies.table_creator.DatabaseTableCreator;
import orm.strategies.table_creator.TableCreator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

public class StrategyConfigurer {

    private Connection connection;
    private String dataSource;
    private TableCreator tableCreator;

    public StrategyConfigurer(Connection connection, String dataSource) {
        this.connection = connection;
        this.dataSource = dataSource;
        tableCreator = new DatabaseTableCreator(this.connection, this.dataSource);
    }

    public <T extends SchemaInitializationStrategy> SchemaInitializationStrategy createStrategy(Class<T> strategyClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<SchemaInitializationStrategy> constructor =
                (Constructor<SchemaInitializationStrategy>) strategyClass
                .getDeclaredConstructor(Connection.class, TableCreator.class, String.class);

        constructor.setAccessible(true);

        SchemaInitializationStrategy strategy = constructor.newInstance(this.connection,
                this.tableCreator, this.dataSource);

        return strategy;
    }
}
