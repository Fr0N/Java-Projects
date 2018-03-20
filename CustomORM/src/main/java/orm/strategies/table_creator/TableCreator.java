package orm.strategies.table_creator;

import java.sql.SQLException;

public interface TableCreator {

    boolean doCreate(Class entity) throws SQLException;

}
