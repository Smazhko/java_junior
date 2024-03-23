package sem2_reflection;

import sem2_reflection.annotations.*;

/**
 * Доделать запускатель тестов:
 * <br>1. Создать аннотации BeforeEach, BeforeAll, AfterEach, AfterAll
 * <br>2. Доработать класс TestRunner так, что
 * <br>2.1 Перед всеми тестами запускаеются методы, над которыми стоит BeforeAll
 * <br>2.2 Перед каждым тестом запускаются методы, над которыми стоит BeforeEach
 * <br>2.3 Запускаются все тест-методы (это уже реализовано)
 * <br>2.4 После каждого теста запускаются методы, над которыми стоит AfterEach
 * <br>2.5 После всех тестов запускаются методы, над которыми стоит AfterAll
 * <br>Другими словами, BeforeAll -> BeforeEach -> Test1 -> AfterEach -> BeforeEach -> Test2 -> AfterEach -> AfterAll
 * <br>
 * <br>3.* Доработать аннотацию Test: добавить параметр int order,
 * по которому нужно отсортировать тест-методы (от меньшего к большему) и запустить в нужном порядке.
 * <br>Значение order по умолчанию - 0
 * <br>4.** Создать класс Asserter для проверки результатов внутри теста с методами:
 * <br>4.1 assertEquals(int expected, int actual)
 * <br>
 * <br>Идеи реализации: внутри Asserter'а кидать исключения, которые перехвываются в тесте.
 * <br>Из TestRunner можно возвращать какой-то объект, описывающий результат тестирования.
 */

public class TestRunnerDemo {

  // МОДИФИКАТОРЫ ДОСТУПА:
  // private                    - никому не видно
  // default (package-private)  - внутри пакета - ПО УМОЛЧАНИЮ
  // protected                  - внутри пакета + наследники
  // public                     - всем

  // помещаем текущий класс TestRunnerDemo в статический метод класса TestRunner,
  // где методы класса TestRunnerDemo помеченные соответствующими аннотациями, будут запускаться
  public static void main(String[] args) {
    TestRunner.run(TestRunnerDemo.class);
  }

  @Test(order = 3)
  private void test1() {
    // этот тест не пройдёт, потому что метод - приватный
    System.out.println("    test 1");
  }

  @Test(order = 1)
  void test2() {
    System.out.println("    test 2");
  }

  @Test(order = 500)
  void testA() {
    System.out.println("    test A");
  }

  @Test(order = -50)
  void testX() {
    System.out.println("    test X");
  }

  @Test
  void test3() {
    System.out.println("    test 3");
  }

  @BeforeAll
  void beforeAll(){
    System.out.println("before all tests");
  }

  @BeforeEach
  void beforeEach1(){
    System.out.println("  before each #1 test");
  }

  @BeforeEach
  void beforeEach2(){
    System.out.println("  before each #2 test");
  }

  @AfterEach
  void afterEach1(){
    System.out.println("  after each #1 test");
  }

  @AfterEach
  void afterEach2(){
    System.out.println("  after each #2 test");
  }

  @AfterAll
  void afterAll(){
    System.out.println("after all tests");
  }

}

/*
before all tests
  before each #2 test
  before each #1 test
    test X
  after each #2 test
  after each #1 test

  before each #2 test
  before each #1 test
    test 3
  after each #2 test
  after each #1 test

  before each #2 test
  before each #1 test
    test 2
  after each #2 test
  after each #1 test

  before each #2 test
  before each #1 test
    test A
  after each #2 test
  after each #1 test

after all tests
 */