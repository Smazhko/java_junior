package sem3_jdbc.db_saver;

public class DBSaverDemo {
    public static void main(String[] args) throws Exception{
        Student newSt1 = new Student("Den", "Astapovsky", 19);
        Student newSt2 = new Student("Alex", "Koshovoy", 20);

        DBSaver.dropTable(newSt1.getClass());
        DBSaver.save(newSt1);
        DBSaver.save(newSt2);
        DBSaver.readTable(newSt1.getClass());

    }
}

/*
Таблицы Student не существует. Удалять нечего.

Анализ объекта класса Student...
В классе Student существует несколько полей с аннотациями @Id. В таблице Student будет создан составной ключ.

таблица: Student
поля id: [id, name]
поля column: {id=1, name=Den, surname=Astapovsky, age=19}

ЗАПРОС НА СОЗДАНИЕ ТАБЛИЦЫ:
CREATE TABLE Student (
id INT,
name VARCHAR (100),
surname VARCHAR (100),
age INT);

ЗАПРОС НА ДОБАВЛЕНИЕ ПЕРВИЧНОГО КЛЮЧА:
ALTER TABLE Student
ADD CONSTRAINT id_name_key PRIMARY KEY (id,name);

ЗАПРОС НА ДОБАВЛЕНИЕ ЗАПИСИ:
INSERT INTO Student (id,name,surname,age)
VALUES
('1','Den','Astapovsky','19');

ДОБАВЛЕНО ЗАПИСЕЙ В БАЗУ: 1

Анализ объекта класса Student...
В классе Student существует несколько полей с аннотациями @Id. В таблице Student будет создан составной ключ.

таблица: Student
поля id: [id, name]
поля column: {id=2, name=Alex, surname=Koshovoy, age=20}

ЗАПРОС НА ДОБАВЛЕНИЕ ЗАПИСИ:
INSERT INTO Student (id,name,surname,age)
VALUES
('2','Alex','Koshovoy','20');

ДОБАВЛЕНО ЗАПИСЕЙ В БАЗУ: 1

Чтение БД ...
id: 1
name: Den
surname: Astapovsky
age: 19
------------------------------
id: 2
name: Alex
surname: Koshovoy
age: 20
------------------------------
 */

/*
Таблица Student удалена.

Анализ объекта класса Student...
В классе Student нет ни одной аннотации @Id и @Column. Создание / редактирование таблицы Student невозможно.

Анализ объекта класса Student...
В классе Student нет ни одной аннотации @Id и @Column. Создание / редактирование таблицы Student невозможно.

Чтение БД ...
Чтение не удалось - таблицы Student не существует.
 */

/*
Таблицы Student не существует. Удалять нечего.

Анализ объекта класса Student...
У указанного класса отсутствует пометка для создания таблицы (аннотация @Table).

Анализ объекта класса Student...
У указанного класса отсутствует пометка для создания таблицы (аннотация @Table).

Чтение БД ...
Чтение не удалось - таблицы Student не существует.

 */

/*
Таблица Student удалена.

Анализ объекта класса Student...
В классе Student нет ни одного поля с аннотацией @Id. Таблица Student будет создана без первичного ключа.

таблица: Student
поля id: []
поля column: {surname=Astapovsky}

ЗАПРОС НА СОЗДАНИЕ ТАБЛИЦЫ:
CREATE TABLE Student (
surname VARCHAR (100));

ЗАПРОС НА ДОБАВЛЕНИЕ ЗАПИСИ:
INSERT INTO Student (surname)
VALUES
('Astapovsky');

ДОБАВЛЕНО ЗАПИСЕЙ В БАЗУ: 1

Анализ объекта класса Student...
В классе Student нет ни одного поля с аннотацией @Id. Таблица Student будет создана без первичного ключа.

таблица: Student
поля id: []
поля column: {surname=Koshovoy}

ЗАПРОС НА ДОБАВЛЕНИЕ ЗАПИСИ:
INSERT INTO Student (surname)
VALUES
('Koshovoy');

ДОБАВЛЕНО ЗАПИСЕЙ В БАЗУ: 1

Чтение БД ...
surname: Astapovsky
------------------------------
surname: Koshovoy
------------------------------
 */