import entities.User;
import orm.Connector;
import orm.persistence.EntityManager;
import orm.persistence.EntityManagerBuilder;
import orm.strategies.UpdateStrategy;

import java.util.Date;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
//        String username = scanner.nextLine().trim();
//        String password = scanner.nextLine().trim();
//        String dbName = scanner.nextLine().trim();

//        List<Class> allEntities = getAllEntities(System.getProperty("user.dir"));

        Connector connector = new Connector(new EntityManagerBuilder());

        EntityManager entityManager = new EntityManagerBuilder()
                .configureConnectionString()
                    .setDriver("jdbc")
                    .setAdapter("mysql")
                    .setHost("localhost")
                    .setPort("3306")
                    .setUser("root")
                    .setPassword("1234")
                    .createConnection()
                .setDatabaseStrategyType(UpdateStrategy.class)
                .setDataSource("asd")
                .build();



//        Connector connector = new Connector();
//        connector.createConnection("root", "1234", "orm_db");
//        EntityManager em = new EntityManager(connector.getConnection());

//        ExampleEntity exampleEntity = new ExampleEntity("Pesho Peshev");
//        em.persist(exampleEntity);
//        em.doDelete(ExampleEntity.class, "full_name LIKE 'Pesho Peshev'");

        User user = new User("pesho", 27, new Date());
//        user.setId(1);
//        em.persist(user);

//        User found = em.findFirst(User.class, "age > 15");
//        User found = em.findFirst(User.class);
//        List<User> found = (List<User>) em.find(User.class);
//        System.out.println(found);
    }
}
