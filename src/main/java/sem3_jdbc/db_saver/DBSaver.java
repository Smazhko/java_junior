package sem3_jdbc.db_saver;

import sem3_jdbc.db_saver.annotations.Column;
import sem3_jdbc.db_saver.annotations.Id;
import sem3_jdbc.db_saver.annotations.Table;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 3.** Придумать, как подружить запросы и reflection:
 * 3.1 Создать аннотации Table, Id, Column
 * 3.2 Создать класс, у которого есть методы:
 * 3.2.1 save(Object obj) сохраняет объект в БД
 * 3.2.2 update(Object obj) обновляет объект в БД
 * 3.2.3 Попробовать объединить save и update (сначала select, потом update или insert)
 */

public class DBSaver {
    private static final String USER = "root";
    private static final String PASSWORD = "12345";
    private static final String SCHEMA_NAME = "jdbc_training";
    private static final String URL = "jdbc:mysql://localhost:3306/jdbc_training";

    public static void save(Object tableObj) throws IllegalAccessException {
        Class<?> tableClass = tableObj.getClass();
        String tableName;
        ArrayList<String> idList = new ArrayList<>();
        LinkedHashMap<String, Object> columnMap = new LinkedHashMap<>();

        System.out.println("Анализ объекта класса " + tableClass.getSimpleName() + "...");
        if (checkClass(tableClass)) {
            tableName = tableClass.getSimpleName();
            for (Field fld : tableClass.getDeclaredFields()) {
                fld.setAccessible(true);
                if (fld.getAnnotation(Id.class) != null) {
                    idList.add(fld.getName());
                    columnMap.put(fld.getName(), fld.get(tableObj));
                }
                if (fld.getAnnotation(Column.class) != null) {
                    columnMap.put(fld.getName(), fld.get(tableObj));
                }
            }
            System.out.println("таблица: " + tableName);
            System.out.println("поля id: " + idList);
            System.out.println("поля column: " + columnMap + "\n");


            try (Connection mySQLConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                createTable(mySQLConnection, tableName, idList, columnMap);
                insertRecords(mySQLConnection, tableName, columnMap);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static boolean checkClass(Class<?> tableClass) {
        int idAnnoCount = 0;
        int columnAnnoCount = 0;
        // Если в классе есть аннотация TABLE, то проверяем - есть ли поле с аннотацией ID. Варианты:
        // - Если полей с ID и COLUMN нет - отказываем в создании таблицы.
        // - Если полей ID нет, но есть COLUMN - создаём таблицу без первичного ключа.
        // - Если полей ID несколько - создаём таблицу с составным ключом.
        if (tableClass.getAnnotation(Table.class) != null) {
            for (Field fld : tableClass.getDeclaredFields()) {
                if (fld.getAnnotation(Id.class) != null) {
                    idAnnoCount += 1;
                }
                if (fld.getAnnotation(Column.class) != null) {
                    columnAnnoCount += 1;
                }
            }
            if (idAnnoCount == 0 && columnAnnoCount == 0) {
                System.out.println("В классе " + tableClass.getSimpleName()
                        + " нет ни одной аннотации @Id и @Column. Создание / редактирование таблицы "
                        + tableClass.getSimpleName() + " невозможно.\n");
                return false;
            }
            if (idAnnoCount == 0 && columnAnnoCount > 0)
                System.out.println("В классе " + tableClass.getSimpleName()
                        + " нет ни одного поля с аннотацией @Id. Таблица "
                        + tableClass.getSimpleName() + " будет создана без первичного ключа.\n");
            if (idAnnoCount > 1)
                System.out.println("В классе " + tableClass.getSimpleName()
                        + " существует несколько полей с аннотациями @Id. В таблице "
                        + tableClass.getSimpleName() + " будет создан составной ключ. \n");
            return true;
        }
        System.out.println("У указанного класса отсутствует пометка для создания таблицы (аннотация @Table).\n");
        return false;
    }


    private static void createTable(Connection mySQLConnection, String tableName,
                                    ArrayList<String> idList, LinkedHashMap<String, Object> columnMap) {
        if (!tableExists(mySQLConnection, tableName)) {
            try (Statement stmnt = mySQLConnection.createStatement()) {
                StringBuilder createQuery = new StringBuilder();
                createQuery.append("CREATE TABLE ")
                        .append(tableName)
                        .append(" (\n");
                for (var record : columnMap.entrySet()) {
                    createQuery.append(record.getKey())
                            .append(" ")
                            .append(convertToSQLType(record.getValue().getClass()))
                            .append(", \n");
                }
                createQuery.delete(createQuery.length() - 3, createQuery.length())
                        .append(");");
                System.out.println("ЗАПРОС НА СОЗДАНИЕ ТАБЛИЦЫ:\n" + createQuery + "\n");
                stmnt.execute(createQuery.toString());

                StringBuilder addPrimKeyQuery = new StringBuilder();
                if (idList.size() == 1) {
                    addPrimKeyQuery.append("ALTER TABLE ")
                            .append(tableName)
                            .append("\n")
                            .append("ADD PRIMARY KEY (")
                            .append(idList.get(0))
                            .append(");");
                }
                if (idList.size() > 1) {
                    addPrimKeyQuery.append("ALTER TABLE ")
                            .append(tableName)
                            .append("\n");
                    StringBuilder compositeKeyName = new StringBuilder();
                    StringBuilder fieldsForCompKey = new StringBuilder();
                    for (String id : idList) {
                        compositeKeyName.append(id).append("_");
                        fieldsForCompKey.append(id).append(",");
                    }
                    compositeKeyName.replace(compositeKeyName.length() - 1, compositeKeyName.length(), "_key");
                    fieldsForCompKey.replace(fieldsForCompKey.length() - 1, compositeKeyName.length(), "");
                    addPrimKeyQuery.append("ADD CONSTRAINT ")
                            .append(compositeKeyName)
                            .append(" PRIMARY KEY (")
                            .append(fieldsForCompKey)
                            .append(");");
                }
                if (idList.size() > 0) {
                    System.out.println("ЗАПРОС НА ДОБАВЛЕНИЕ ПЕРВИЧНОГО КЛЮЧА: \n" + addPrimKeyQuery + "\n");
                    stmnt.execute(addPrimKeyQuery.toString());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static void insertRecords(Connection mySQLConnection, String tableName,
                                      LinkedHashMap<String, Object> columnMap) {
        try (Statement stmnt = mySQLConnection.createStatement()) {
            StringBuilder insertQuery = new StringBuilder();
            insertQuery.append("INSERT INTO ")
                    .append(tableName)
                    .append(" (");
            for (var field : columnMap.keySet()) {
                insertQuery.append(field)
                        .append(",");
            }
            insertQuery.delete(insertQuery.length() - 1, insertQuery.length())
                    .append(")\n")
                    .append("VALUES\n")
                    .append("(");
            for (var value : columnMap.values()) {
                insertQuery.append("'")
                        .append(value.toString())
                        .append("',");
            }
            insertQuery.delete(insertQuery.length() - 1, insertQuery.length())
                    .append(");");

            System.out.println("ЗАПРОС НА ДОБАВЛЕНИЕ ЗАПИСИ:\n" + insertQuery + "\n");

            System.out.println("ДОБАВЛЕНО ЗАПИСЕЙ В БАЗУ: " + stmnt.executeUpdate(insertQuery.toString()) + "\n");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void readTable(Class<?> tableClass) {
        // пытаемся коннектиться к БД
        try (Connection mySQLConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String tableName = tableClass.getSimpleName();

            // если таблица в БД с указанным именем существует, то ...
            System.out.println("Чтение БД ...");
            if (tableExists(mySQLConnection, tableName)) {
                String query = "SELECT * FROM " + SCHEMA_NAME + "." + tableName;

                try (Statement statement = mySQLConnection.createStatement();
                     ResultSet resultSet = statement.executeQuery(query)) {
                    // получаем метаданные о таблице, чтобы понять, сколько в ней колонок
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    // читаем все записи из БД в соответствии с мета-данными о таблице
                    while (resultSet.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            Object value = resultSet.getObject(i);
                            System.out.println(columnName + ": " + value);
                        }
                        System.out.println("-".repeat(30)); // для разделения записей
                    }
                }
            } else
                System.out.println("Чтение не удалось - таблицы "+ tableName + " не существует.\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static boolean tableExists(Connection mySQLConnection, String tableName) {
        try {
            DatabaseMetaData metaData = mySQLConnection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, tableName, null);
            boolean result = resultSet.next();
            // System.out.println("Таблица " + tableName + " существует? " + result + "\n");
            return result;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    private static String convertToSQLType(Class<?> curClass) {
        if (curClass.getSimpleName().equals("String"))
            return "VARCHAR (100)";
        if (curClass.getSimpleName().equals("Integer"))
            return "INT";
        return "";
    }

    public static void dropTable(Class<?> tableClass){
        try (Connection mySQLConnection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String tableName = tableClass.getSimpleName();

            try (Statement stmnt = mySQLConnection.createStatement()) {
            if (tableExists(mySQLConnection, tableName)) {
                stmnt.execute("DROP TABLE " + tableName);
                System.out.println("Таблица " + tableName + " удалена.\n");
            }
            else
                System.out.println("Таблицы " + tableName + " не существует. Удалять нечего.\n");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
