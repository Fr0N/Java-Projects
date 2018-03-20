package orm;

import orm.persistence.EntityManagerBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector {
    private EntityManagerBuilder entityManagerBuilder;
    private String adapter;
    private String driver;
    private String host;
    private String port;
    private String user;
    private String password;

    public Connector(EntityManagerBuilder entityManagerBuilder) {
        this.entityManagerBuilder = entityManagerBuilder;
    }

    public EntityManagerBuilder createConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", this.user);
        properties.setProperty("password", this.password);

        Connection connection = DriverManager.getConnection(String.format("%s:%s://%s:%s",
                this.driver, this.adapter, this.host, this.port), properties);

        this.entityManagerBuilder.setConnection(connection);

        return this.entityManagerBuilder;
    }

    public Connector setAdapter(String adapter) {
        this.adapter = adapter;

        return this;
    }

    public Connector setDriver(String driver) {
        this.driver = driver;

        return this;
    }

    public Connector setHost(String host) {
        this.host = host;

        return this;
    }

    public Connector setPort(String port) {
        this.port = port;

        return this;
    }

    public Connector setUser(String user) {
        this.user = user;

        return this;
    }

    public Connector setPassword(String password) {
        this.password = password;

        return this;
    }
}
