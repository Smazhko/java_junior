package lesson2;

import lesson1.Streams;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Reflections {

  public static void main(String[] args) throws Exception {
    // .java -> .class (bytecode) -> JVM (bytecode -> $23423#23324

    Class<User> userClass = User.class;
    // Класс Class параметризуем типом нашего класса и получаем объект класса User для дальнейшего раскручивания на винтики.
    // Чтобы в методе, который принимает какой-то объект, начать работать с рефлексией и получить КЛАСС передаваемого объекта,
    // используем метод getClass():
    // void myMethod (Object obj) { Class<?> reflectObject = obj.getClass(); }

    Constructor<User> constructor = userClass.getConstructor(String.class, String.class);
    // Создаём объект типа Constructor, который будет указывать на конструктор нашего класса User,
    // который принимает в качестве параметров две переменные типа String.

    User inchestnov = constructor.newInstance("inchestnov", "pa$$");
    // Создаём новый объект типа User путём запуска объекта Constructor - метод newInstance(параметр1, параметр2...ю).

    Method methodGetLogin = userClass.getMethod("getLogin");
    // Создаём объект класса METHOD. Получаем его из объекта класса Class -
    // getMethod("НАЗВАНИЕ МЕТОДА", список КЛАССОВ переменных) - как конструктор.
    // Поскольку GET ничего не принимает, а только возвращает, то списка переменных у нас нет.
    // поэтому просто .getMethod("getLogin");

    String result = (String) methodGetLogin.invoke(inchestnov); // равносильно вызову inchestnov.getLogin();
    // запуск выполнения метода из объекта METHOD ->
    // invoke(ОБЪЕКТ КЛАССА типа USER, у которого надо вызвать метод, параметр1, параметр2...).

    System.out.println("(String) methodGetLogin.invoke(inchestnov): " + result);
    // РЕЗУЛЬТАТ: inchestnov

    // аналогично с методов setPassword класса User. Помещаем его в объект типа METHOD и вызываем с помощью INVOKE
    // getMethod( "НАЗВАНИЕ МЕТОДА)", список типов переменных, которые принимает метод).
    Method setPassword = userClass.getMethod("setPassword", String.class); // inchestnov.setPassword("newPassword");
    setPassword.invoke(inchestnov, "newPassword");
    System.out.println("inchestnov.getPassword(): " + inchestnov.getPassword());
    // РЕЗУЛЬТАТ: newPassword

    // а вот таким способом мы вызываем СТАТИЧЕСКИЕ методы -> invoke(null),
    // так как объект класса для вызова статического метода создавать не нужно.
    // class User { public static long getCounter() { return counter; } }
    System.out.println("Статическое поле - счётчик: " + userClass.getMethod("getCounter").invoke(null));
    // РЕЗУЛЬТАТ: 1

    // создаём объект класса FILED, который будет содержать ссылку на какое-то конкретное поле объекта класса User
    // .getDeclared..Constructor/Field/Method - обозначает доступ ко ВСЕМ полям, прописанным в этом классе - private, public
    // .get..Constructor/Field/Method - обозначает доступ ко всем полям / методам / констукторам,
    // которые ДОСТУПНЫ - public и наследованные от родителей
    // получить значение поля -> .get(объект класса, поле которого надо получить)
    Field password = userClass.getDeclaredField("password");
    System.out.println("userClass.getDeclaredField(\"password\").get(inchestnov): " + password.get(inchestnov)); // inchestnov.password
    // РЕЗУЛЬТАТ - newPassword


    // получаем доступ к ПРИВАТНОМУ полю login. ОБЯЗАТЕЛЬНО надо установить ДОСТУП .setAccessible(true)
    Field login = userClass.getDeclaredField("login");
    System.out.println("login.get(inchestnov): " + login.get(inchestnov));
    login.setAccessible(true);
    login.set(inchestnov, "newValue");

    System.out.println("объект inchestnov с новыми параметрами: " + inchestnov);

    // declared - все, что объявлено в конкретном классе (без учета наследования) - getDeclaredField, getDeclaredMethod
    // not declared - все доступные (с учетом наследования) - getField, getMethod

    // Получаем аннотацию метода, принадлежащего классу-родителю
    // метод getAnnotation принимает аргумент - искомая аннотация.
    // Если она есть, то метод её возвращает, если нет - то возвращает NULL
    MyAnnotation anno = SuperUser.class.getMethod("setPassword", String.class).getAnnotation(MyAnnotation.class);
    System.out.println("параметр аннтотации из класса-родителя: " + anno.myParameter());
    System.out.println(anno);

    // !! Получить приватные поля класса родителя НАПРЯМУЮ нельзя. в обход - можно.
    // getSuperclass - получить класс родителя

  }

  static class User {

    private static long counter = 0L;

    private final String login;
    private String password;

    public User(String login) {
      this(login, "defaultpassword");
    }

    public User(String login, String password) {
      this.login = login;
      this.password = password;
      counter++;
    }

    public static long getCounter() {
      return counter;
    }

    public String getLogin() {
      return login;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    @Override
    public String toString() {
      return "User{" +
        "login='" + login + '\'' +
        ", password='" + password + '\'' +
        '}';
    }
  }

  static class SuperUser extends User {

    public SuperUser(String login) {
      super(login, "");
    }

    @Override
    @MyAnnotation(myParameter = "text")
    public void setPassword(String password) {
      throw new UnsupportedOperationException();
    }
  }



}
