package sem3_jdbc;

import java.sql.*;

/**
 * 0. Разобрать код с семинара
 * 1. Повторить код с семинара без подглядываний на таблице Student с полями:
 * 1.1 id - int
 * 1.2 firstName - string
 * 1.3 secondName - string
 * 1.4 age - int
 * 2.* Попробовать подключиться к другой БД
 * 3.** Придумать, как подружить запросы и reflection:
 * 3.1 Создать аннотации Table, Id, Column
 * 3.2 Создать класс, у которого есть методы:
 * 3.2.1 save(Object obj) сохраняет объект в БД
 * 3.2.2 update(Object obj) обновляет объект в БД
 * 3.2.3 Попробовать объединить save и update (сначала select, потом update или insert)
 */

// КАК ПОДКЛЮЧИТЬ mySQL к JAVA проекту:
// https://ru.hexlet.io/qna/java/questions/kak-podklyuchit-mysql-k-java
// И
// https://habr.com/ru/sandbox/146588/

public class Homework {

    /*
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.3.0</version>
    </dependency>
     */

    public static void main(String[] args) {
        // в URL указываем адрес расположения базы - //localhost:3306/ и далее название СХЕМЫ lesson_3
        // если будем создавать новую схему, тогда ничего не указываем,
        // а в методе CREATE сделаем дополнительные запросы Statement для создания СХЕМЫ, а затем и ТАБЛИЦЫ
        String url = "jdbc:mysql://localhost:3306/";
        String user = "root";
        String password = "12345";
        try {
            Connection mySQLConnection = DriverManager.getConnection(url, user, password);
            createTable(mySQLConnection);
            fillTable(mySQLConnection);
            readTable(mySQLConnection);
            updateRecords(mySQLConnection);
            readTable(mySQLConnection);
            updateRecordById(mySQLConnection);
            readTable(mySQLConnection);
            deleteRecords(mySQLConnection);
            readTable(mySQLConnection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static void createTable(Connection mySQLConnection) {
        // ВАЖНО!!!!!!!
        // Statement принимает только 1 запрос за раз,
        // то есть нельзя в 1 выражении сразу и дропнуть таблицу, и создать новую. Это надо РАЗДЕЛЯТЬ!!!

        try (Statement stmnt = mySQLConnection.createStatement()) {
            stmnt.execute("""
                    DROP DATABASE IF EXISTS jdbc_training;
                    """);
            stmnt.execute("""
                    CREATE DATABASE jdbc_training;
                    """);
            stmnt.execute("""
                    USE jdbc_training;
                    """);
            stmnt.execute("""
                    DROP TABLE IF EXISTS staff;
                        """);
            stmnt.execute("""
                            CREATE TABLE staff (
                            id INT AUTO_INCREMENT PRIMARY KEY,
                            firstname VARCHAR(45),
                            lastname VARCHAR(45),
                            post VARCHAR(100),
                            seniority INT,
                            salary INT,
                            age INT);
                    """);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static void fillTable(Connection mySQLConnection) {
        String query = """                                        
                INSERT INTO staff (firstname, lastname, post, seniority, salary, age)
                VALUES
                ('Вася', 'Петров', 'Начальник', '40', 100000, 60),
                ('Петр', 'Власов', 'Начальник', '8', 70000, 30),
                ('Катя', 'Катина', 'Инженер', '2', 70000, 25),
                ('Саша', 'Сасин', 'Инженер', '12', 50000, 35),
                ('Иван', 'Иванов', 'Рабочий', '40', 30000, 59),
                ('Петр', 'Петров', 'Рабочий', '20', 25000, 40),
                ('Сидор', 'Сидоров', 'Рабочий', '10', 20000, 35)
                """;
        try (Statement statement = mySQLConnection.createStatement()) {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void readTable(Connection mySQLConnection) {
        String schemaName = "jdbc_training";
        String tableName = "staff";

        String query = "SELECT * FROM " + schemaName + "." + tableName;

        try (Statement statement = mySQLConnection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            System.out.printf("%-3s %-10s %-10s %-15s %-6s %-6s %-2s %n", "id", "firstname", "lastname", "post", "senior", "salary", "age");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                String post = resultSet.getString("post");
                int seniority = resultSet.getInt("seniority");
                int salary = resultSet.getInt("salary");
                int age = resultSet.getInt("age");

                System.out.printf("%-3s %-10s %-10s %-15s %-6s %-6s %-2s %n", id, firstname, lastname, post, seniority, salary, age);
            }
            System.out.println("-".repeat(30));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateRecordById (Connection mySQLConnection){
        String newName = "QQQQQQ";
        String newLastname = "WWWWWW";
        int id = 5;
        try(PreparedStatement prepStmnt = mySQLConnection.prepareStatement(
                "UPDATE staff SET firstname = ?, lastname = ? WHERE id = ?;")){
            prepStmnt.setString(1, newName);
            prepStmnt.setString(2, newLastname);
            prepStmnt.setInt(3, id);
            prepStmnt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void updateRecords(Connection mySQLConnection){
        try (Statement stmnt = mySQLConnection.createStatement()){
            stmnt.executeUpdate("""
                    UPDATE staff SET post = "Новый начальник" WHERE id IN (3,5,7);
                    """);
            stmnt.executeUpdate("""
                    UPDATE staff SET firstname = "КУЗЯ" WHERE lastname="Власов";
                    """);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRecords(Connection mySQLConnection){
        try (Statement statement = mySQLConnection.createStatement()){
            statement.executeUpdate("DELETE FROM staff WHERE age > 50;");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}

  /*
  @Table(name = "person")
  static class Person {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
  */

