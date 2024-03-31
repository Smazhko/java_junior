package lesson4;

import jakarta.persistence.*;


// @Entity
// Аннотация @Entity в Hibernate используется для пометки класса как сущности (entity). Сущность представляет собой
//  объектно-ориентированное представление данных, которые будут сохранены в базе данных.
//  Помеченный аннотацией @Entity класс является Java-объектом, который будет сопоставлен с таблицей в базе данных.
//
// Вот что делает аннотация @Entity:
//
//  - Указывает Hibernate на то, что класс является сущностью, которую следует сопоставить с таблицей в базе данных.
//  - Связывает класс с метаданными Hibernate, что позволяет Hibernate отслеживать и управлять состоянием этой сущности.
//  - Позволяет Hibernate создавать запросы к базе данных, основанные на этой сущности.
//  - Позволяет Hibernate автоматически создавать таблицу в базе данных на основе структуры этой сущности
//  (если используется подход автогенерации таблиц).

// @Table - указывает, на какую таблицу должна смотреть Entity

// Типы полей в классах, работающих с БД, должны быть ссылочными, так как поля в таблице могут принимать значение NULL

// @Id - указание на поле с первичным ключом

// У сущности обязательно должен быть конструктор без аргументов, а так же GETTERs и SETTERs для всех полей.

// Аннотации COLUMN не обязательны. Hibernate понимает, что надо создать колонки для всех полей в классе.

@Entity
@Table(name = "persons")
public class Person {

  @Id
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @ManyToOne(fetch = FetchType.LAZY) // способ связать две таблицы - персоны и отделы. много персон к одному отделу.
  @JoinColumn(name = "department_id") // по какому полю будет осуществляться связь.
  private Department department;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Department getDepartment() {
    return department;
  }

  public void setDepartment(Department department) {
    this.department = department;
  }

  @Override
  public String toString() {
    return "Person{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", department=" + department +
      '}';
  }
}
