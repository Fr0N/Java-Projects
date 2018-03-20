package orm.strategies;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface SchemaInitializationStrategy {
    void execute() throws SQLException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InstantiationException, InvocationTargetException;
}
