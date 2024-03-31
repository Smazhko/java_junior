package sem4_hibernate;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Random;

/**
   * 1. Создать сущность Student с полями:
   * 1.1 id - int
   * 1.2 firstName - string
   * 1.3 secondName - string
   * 1.4 age - int
   * 2. Подключить hibernate. Реализовать простые запросы: Find(by id), Persist, Merge, Remove
   * 3. Попробовать написать запрос поиска всех студентов старше 20 лет (session.createQuery)
   */

  /*
  СМ. КОММЕНТАРИИ К КОДУ В LESSON4 ВО ВСЕХ!!!! КЛАССАХ

          <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
        </dependency>

        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>6.5.0.CR1</version>
        </dependency>

        <mapping class="sem4_hibernate.Student"/>

   */
public class Homework {

    public static void main(String[] args) {
      Configuration configure = new Configuration().configure();
        try (SessionFactory sesFact = configure.buildSessionFactory()) {
            System.out.println(">>>>>>>>> ДОБАВЯЛЯЕМ СТУДЕНТОВ В БАЗУ >>>>>>>>>>>");
            insertRndRecords(sesFact);


            System.out.println(">>>>>>>>> ПЕЧАТЬ БАЗЫ  >>>>>>>>>>>");
            showAll(sesFact);

            System.out.println(">>>>>>>>> СМОТРИМ ТОЛЬКО 5го >>>>>>>>>>>");
            findById(sesFact, 5L);

            System.out.println(">>>>>>>>> МЕНЯЕМ 5го >>>>>>>>>>>");
            changeById(sesFact, 5L);

            System.out.println(">>>>>>>>> СНОВА СМОТРИМ 5го >>>>>>>>>>>");
            findById(sesFact, 5L);

            System.out.println(">>>>>>>>> УДАЛЯЕМ 5го >>>>>>>>>>>");
            deleteById(sesFact, 5L);

            System.out.println(">>>>>>>>> ПЕЧАТЬ БАЗЫ  >>>>>>>>>>>");
            showAll(sesFact);

            System.out.println(">>>>>>>>> ПОИСК ПО ВОЗРАСТУ МЕЖДУ 20 и 99 >>>>>>>>>>>");
            findByAge(sesFact, 20, 99);
        }

    }


    public static void insertRndRecords(SessionFactory sesFact){
        String[] names = {"Виталий", "Петр", "Никита", "Иван", "Сергей", "Алексей", "Антонина", "Ирина", "Лариса", "Ольга",
                "Владислав", "Александр", "Милана", "Анатолий", "Дарья", "Вероника", "Алёна", "Михаил", "Елена", "Анна",
                "Олег", "Тимофей", "Григорий", "Маргарита", "Вячеслав", "Родион"};

        Random rnd = new Random();

        try(Session ses = sesFact.openSession()){
            Transaction tx = ses.beginTransaction();

            for (int i = 0; i < 10; i++) {
                Student student = new Student();
                student.setId(i);
                student.setName(names[rnd.nextInt(names.length)]);
                student.setSurname((char) (1040 + rnd.nextInt(32)) + ".");
                student.setAge(rnd.nextInt(16, 24));
                ses.persist(student);
            }
            tx.commit();
        }
    }

    public static Student findById(SessionFactory sesFact, Long id){
        try(Session ses = sesFact.openSession()){
            Student newStudent = ses.find(Student.class, id);
            System.out.println(newStudent);
            return newStudent;

        }
    }

    public static List<Student> showAll (SessionFactory sesFact){
        try(Session sess = sesFact.openSession()){
            Query<Student> fromStudentS = sess.createQuery("from Student s", Student.class);
            List<Student> studentList = fromStudentS.getResultList();
            studentList.forEach(System.out::println);
            return studentList;
        }
    }

    public static Student changeById(SessionFactory sesFact, Long id){
        try (Session ses = sesFact.openSession()){
            Transaction tx = ses.beginTransaction();

            Student st = findById(sesFact, id);
            st.setName("CHANGED");
            st.setSurname("Q.");
            st.setAge(99);
            ses.merge(st);

            tx.commit();
            return st;
        }
    }

    public static Student deleteById(SessionFactory sesFact, Long id){
        try(Session ses = sesFact.openSession()){
            Transaction tx = ses.beginTransaction();
            Student st = findById(sesFact, id);
            ses.remove(st);
            tx.commit();
            return st;
        }
    }


    private static List<Student> findByAge(SessionFactory sesFact, int min, int max) {
        try(Session ses = sesFact.openSession()){
            Query<Student> qry = ses.createQuery("SELECT s FROM Student s WHERE age BETWEEN :min AND :max", Student.class);
            qry.setParameter("min", min);
            qry.setParameter("max", max);
            List<Student> stList = qry.getResultList();
            stList.forEach(System.out::println);
            return stList;
        }
    }
}

/*
>>>>>>>>> ДОБАВЯЛЯЕМ СТУДЕНТОВ В БАЗУ >>>>>>>>>>>
Hibernate: insert into students (age,name,surname,id) values (?,?,?,?)
Hibernate: insert into students (age,name,surname,id) values (?,?,?,?)
Hibernate: insert into students (age,name,surname,id) values (?,?,?,?)
Hibernate: insert into students (age,name,surname,id) values (?,?,?,?)
Hibernate: insert into students (age,name,surname,id) values (?,?,?,?)
Hibernate: insert into students (age,name,surname,id) values (?,?,?,?)
Hibernate: insert into students (age,name,surname,id) values (?,?,?,?)
Hibernate: insert into students (age,name,surname,id) values (?,?,?,?)
Hibernate: insert into students (age,name,surname,id) values (?,?,?,?)
Hibernate: insert into students (age,name,surname,id) values (?,?,?,?)
>>>>>>>>> ПЕЧАТЬ БАЗЫ  >>>>>>>>>>>
Hibernate: select s1_0.id,s1_0.age,s1_0.name,s1_0.surname from students s1_0
Студент #0 Никита А. (22 года)
Студент #1 Маргарита Б. (23 года)
Студент #2 Дарья Б. (17 лет)
Студент #3 Елена З. (17 лет)
Студент #4 Михаил Ф. (23 года)
Студент #5 Родион М. (22 года)
Студент #6 Александр Н. (19 лет)
Студент #7 Маргарита С. (20 лет)
Студент #8 Милана Ж. (19 лет)
Студент #9 Анатолий Ц. (17 лет)
>>>>>>>>> СМОТРИМ ТОЛЬКО 5го >>>>>>>>>>>
Hibernate: select s1_0.id,s1_0.age,s1_0.name,s1_0.surname from students s1_0 where s1_0.id=?
Студент #5 Родион М. (22 года)
>>>>>>>>> МЕНЯЕМ 5го >>>>>>>>>>>
Hibernate: select s1_0.id,s1_0.age,s1_0.name,s1_0.surname from students s1_0 where s1_0.id=?
Студент #5 Родион М. (22 года)
Hibernate: select s1_0.id,s1_0.age,s1_0.name,s1_0.surname from students s1_0 where s1_0.id=?
Hibernate: update students set age=?,name=?,surname=? where id=?
>>>>>>>>> СНОВА СМОТРИМ 5го >>>>>>>>>>>
Hibernate: select s1_0.id,s1_0.age,s1_0.name,s1_0.surname from students s1_0 where s1_0.id=?
Студент #5 CHANGED Q. (99 лет)
>>>>>>>>> УДАЛЯЕМ 5го >>>>>>>>>>>
Hibernate: select s1_0.id,s1_0.age,s1_0.name,s1_0.surname from students s1_0 where s1_0.id=?
Студент #5 CHANGED Q. (99 лет)
Hibernate: select null,s1_0.age,s1_0.name,s1_0.surname from students s1_0 where s1_0.id=?
Hibernate: delete from students where id=?
>>>>>>>>> ПЕЧАТЬ БАЗЫ  >>>>>>>>>>>
Hibernate: select s1_0.id,s1_0.age,s1_0.name,s1_0.surname from students s1_0
Студент #0 Никита А. (22 года)
Студент #1 Маргарита Б. (23 года)
Студент #2 Дарья Б. (17 лет)
Студент #3 Елена З. (17 лет)
Студент #4 Михаил Ф. (23 года)
Студент #6 Александр Н. (19 лет)
Студент #7 Маргарита С. (20 лет)
Студент #8 Милана Ж. (19 лет)
Студент #9 Анатолий Ц. (17 лет)
>>>>>>>>> ПОИСК ПО ВОЗРАСТУ МЕЖДУ 20 и 99 >>>>>>>>>>>
Hibernate: select s1_0.id,s1_0.age,s1_0.name,s1_0.surname from students s1_0 where s1_0.age between ? and ?
Студент #0 Никита А. (22 года)
Студент #1 Маргарита Б. (23 года)
Студент #4 Михаил Ф. (23 года)
Студент #7 Маргарита С. (20 лет)
Hibernate: drop table if exists department cascade
Hibernate: drop table if exists persons cascade
Hibernate: drop table if exists students cascade
 */
