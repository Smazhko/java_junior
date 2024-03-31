package sem4_hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "students")
public class Student {

    @Id
    @Column
    private Integer id;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private Integer age;

    public Student() {
    }

    public Student(Integer id, String name, String surname, Integer age) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        String years = switch (age % 10) {
            case 1 -> "год";
            case 2, 3, 4 -> "года";
            default -> "лет";
        };
        return "Студент #" + id + " " +
                name + " " + surname +
                " (" + age + " " + years + ')';
    }
}
