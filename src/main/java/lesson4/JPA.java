package lesson4;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class JPA {

  // JPA Java (Jakarta) Persistence API - набор соглашений (правила и аннотации) для работы с объектами.
  // Hibernate - реализация спецификации JPA (одна из реализаций JPA)
  // EclipseLink - иная реализация JPA

  // для конфигурации Hibernate нужен конфигурационный файл: src / resources / hibernate.cfg.xml

//  <property name="hibernate.hbm2ddl.auto">create-drop</property>
//     hibernate.hbm2ddl.auto - что нужно сделать при старте приложения:
//        NONE        - ничего не далать
//        CREATE      - создать новые таблицы по всем сущностям, которые есть в проекте
//        CREATE-DROP - при старте приложения схему создать, при завершении приложения - схему удалить
//        UPDATE      - если схемы в БД нет, она создастся, если схема есть, но она не совпадает с классами в проекте,
//                      то БД обновится, но могут возникать конфликты
//        VALIDATE    - проверяет соответствие схемы с БД и в классах приложения. Если не соответствует - приложение падает
//
//  <property name="show_sql">true</property>
//      при включенной настройке show_sql HIBERNATE будет в логах показывать SQL запросы, который сам формирует
//
// Необходимо прямо указать на классы, которые будут переводиться в БД.
//<!--        <mapping package="ru.gb.lesson4"/>-->
//        <mapping class="ru.gb.lesson4.Person"/>
//        <mapping class="ru.gb.lesson4.Department"/>

  public static void main(String[] args) throws SQLException {

    // подключаемся к Hibernate:  
    Configuration configuration = new Configuration().configure();
    try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
      insertPersons(sessionFactory);
//
//      try (Session session = sessionFactory.openSession()) {
//        // SQL Structure Query Language
//        // JQP Java Query Language
//
//        Query<Department> query = session.createQuery("select p from Department p", Department.class);
//        System.out.println(query.getSingleResult());
//      }

      try (Session session = sessionFactory.openSession()) {  // создаём сессию
        Transaction tx = session.beginTransaction();          // начинаем транзакцию
        Person person = session.find(Person.class, 1L);    // формируем новый объект ИЗ БД
        System.out.println(person);

        person.setName("NEW NAME");     // меняем созданный объект
        session.merge(person);          // сливаем его с БД

        tx.commit();                    // закрываем транзакцию
      }

      // Создаём список, куда надо поместить объекты, соответствующие результату поиска по условию
      List<Person> persons = new ArrayList<>();
      try (Session session = sessionFactory.openSession()) {    // создаём сессию
        // SQL Structure Query Language
        // JQP Java Query Language

        // для поиска (SELECT) в создании транзакции нет необходимости
        // создаём объект типа QUERY для получения результатов выполнения запроса
        // .createQuery(ТЕКСТ ЗАПРОСА с параметрами, КЛАСС объектов, которые собираем по запросу)
        // параметры прописываем с помощью .setParameter(ИМЯ параметра, ЗНАЧЕНИЕ)
        Query<Person> query = session.createQuery("select p from Person p where id > :id", Person.class);
        query.setParameter("id", 5);
        List<Person> resultList = query.getResultList();

        // ИЗМЕНЯЕМ всех персон, которые попали в запрос выше
        // для обновления данных в БД по этим объектам -> session.MERGE(объект, который надо обновить)
        Transaction tx = session.beginTransaction();
        for (Person person : resultList) {
          person.setName("UPDATED");
          session.merge(person);
        }
        tx.commit(); // завершаем транзакцию обновлением данных в БД

        // сохраним список с результатами обработки
        persons.addAll(session.createQuery("from Person p", Person.class).getResultList());
        System.out.println(persons);
      }


      // УДАЛЕНИЕ записей из БД -> .remove(ОБЪЕКТ)
      try (Session session = sessionFactory.openSession()) {
        Person person = session.find(Person.class, 1L);
        System.out.println(person);

        Transaction tx = session.beginTransaction();
        session.remove(person);
        tx.commit();
      }

      try (Session session = sessionFactory.openSession()) {
        Person person = session.find(Person.class, 1L);
        System.out.println(person);
      }
    }
  }

  private static void insertPersons(SessionFactory sessionFactory) {
    try (Session session = sessionFactory.openSession()) {
      Transaction tx = session.beginTransaction();

      Department department = new Department();
      department.setId(555L);
      department.setName("DEPARTMENT NAME");
      session.persist(department);

      // генерируем персон для БД
      for (long i = 1; i <= 10; i++) {
        Person person = new Person();
        person.setId(i);
        person.setName("Person #" + i);
        person.setDepartment(department);

        session.persist(person);
      }

      tx.commit();
    }
  }

}
