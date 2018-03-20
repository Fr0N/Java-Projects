package orm.persistence;

import orm.Connector;
import orm.strategies.SchemaInitializationStrategy;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

public class EntityManagerBuilder {
    private Connection connection;
    private String dataSource; //Name of the database
    private Class strategyClass;
    private SchemaInitializationStrategy strategy;

    public Connector configureConnectionString() {
        return new Connector(this);
    }

    public <T extends SchemaInitializationStrategy> EntityManagerBuilder setDatabaseStrategyType(Class<T> strategyClass) {
        this.strategyClass = strategyClass;

        return this;
    }

    public EntityManager build() throws SQLException,
                                        IllegalAccessException,
                                        ClassNotFoundException,
                                        NoSuchMethodException,
                                        InstantiationException,
                                        InvocationTargetException {
        StrategyConfigurer strategyConfigurer = new StrategyConfigurer(this.connection, this.dataSource);
        this.strategy = strategyConfigurer.createStrategy(strategyClass);

        if (this.connection == null) {
            throw new IllegalStateException("Connection is null");
        }
        if (this.dataSource == null) {
            throw new IllegalStateException("Data source is null");
        }
        if (this.strategy == null) {
            throw new IllegalStateException("Strategy is null");
        }

        return new EntityManager(this.connection, this.dataSource, this.strategy);
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

//
//    public Connection getConnection() {
//        return this.connection;
//    }
//
    public EntityManagerBuilder setDataSource(String dataSource) {
        this.dataSource = dataSource;

        return this;
    }
//
//    public String getDataSource() {
//        return this.dataSource;
//    }
//
//    public void setStrategy(SchemaInitializationStrategy strategy) {
//        this.strategy = strategy;
//    }
}
