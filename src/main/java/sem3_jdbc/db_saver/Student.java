package sem3_jdbc.db_saver;

import sem3_jdbc.db_saver.annotations.Column;
import sem3_jdbc.db_saver.annotations.Table;



@Table
public class Student {

    //@Id
    private final Integer id;

    private static Integer counterForId = 1;

    //@Id
    private final String name;

    @Column
    private final String surname;

    //@Column
    private final Integer age;

    public Student(String name, String surname, Integer age) {
        this.id = counterForId;
        this.name = name;
        this.surname = surname;
        this.age = age;
        counterForId++;
    }
}
