package lesson3;

import java.sql.*;

/*
https://javarush.com/groups/posts/2172-jdbc-ili-s-chego-vsje-nachinaetsja

openjdk / java.sql /  -> Connection, Driver, Statement, ResultSet
 */

public class JDBC {

    public static void main(String[] args) {
        // создаём подключение к БД
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:test")) {
            acceptConnection(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void acceptConnection(Connection connection) throws SQLException {
        createTable(connection);      // наш метод
        insertData(connection);       // наш метод
        deleteRandomRow(connection);  // наш метод

        updateRow(connection, "Igor", "8998090948");

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("select id, name, second_name from person");

            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String secondName = resultSet.getString("second_name");

                System.out.println("id = " + id + ", name = " + name + ", second_name = " + secondName); // id = 1, name = Igor, second_name = Igor
                System.out.println(resultSet); // rs1: org.h2.result.LocalResult@70beb599 columns: 3 rows: 4 pos: 0
            }
        }
    }

    private static void insertData(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // .executeUpdate(ЗАПРОС) - возвращает количество измененных строк в таблице в результате выполнения запроса
            int affectedRows = statement.executeUpdate("""
                    insert into person(id, name) values
                    (1, 'Igor'),
                    (2, 'Person #2'),
                    (3, 'John'),
                    (4, 'Alex'),
                    (5, 'Peter')
                    """);

            System.out.println("INSERT: affected rows: " + affectedRows);
        }
    }

    private static void updateRow(Connection connection, String name, String secondName) throws SQLException {

        // Во избежание SQL-инъекций со стороны злоумышленников необходимо использовать PreparedStatement.
        // Объект PreparedStatement позволяет объединить несколько команд SQL в одну группу и передать их на обработку
        // в базу данных в пакетном режиме.
        // ТО ЕСТЬ:
        // Благодаря этой конструкции запрос разделяется на 2 части - сам запрос и именованные параметры,
        // которые отправляются в БД. Далее сама БД из этих двух частей собирает запрос, который потом выполняется.
        // Параметры запроса указываются с помощью PreparedStatement.setString(ИНДЕКС параметра, ИМЯ параметра);
        // В примере ниже использованы $1 $2 в местах значений параметров. Вероятно, это работает с H2.
        // В mySQL такой фокус не прокатил и там вместо $1 $2 необходимо использовать классический вариант с "?"
        // .prepareStatement("update person set second_name = ? where name = ?")
        // При таком вариант параметры нумеруются с 1 по порядку следования знаков вопроса.
        // РАБОТА С PREPARED STATEMENT на JavaRush:
        // https://javarush.com/quests/lectures/questhibernate.level08.lecture01
        // ТАКЖЕ:
        // https://proselyte.net/prepared-statement-peculiarities/
        try (PreparedStatement stmt = connection.prepareStatement("update person set second_name = $1 where name = $2")) {
            stmt.setString(1, secondName);
            stmt.setString(2, name);

          System.out.println("updateRow -> " + stmt.executeUpdate());
        }

//    try (Statement statement = connection.createStatement()) {
//      statement.executeUpdate("update person set secondName = " + secondName + "where name = " + name);
//    }
    }

    private static void deleteRandomRow(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            System.out.println("DELETED: " + statement.executeUpdate("delete from person where id = 2"));
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // .execute(ЗАПРОС) возвращает true/false по результатам выполнения запроса
            statement.execute("""
                    create table person (
                      id bigint,
                      name varchar(256),
                      second_name varchar(256)
                    )
                    """);
        }
    }

}
