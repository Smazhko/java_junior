package ru.gb.lesson2.tests;

public class TestRunnerDemo {

  // private никому не видно
  // default (package-private) внутри пакета
  // protected внутри пакета + наследники
  // public всем

  public static void main(String[] args) {
    TestRunner.run(TestRunnerDemo.class);
  }

//  @Test(order = 3)
  private void test1() {
    System.out.println("test1");
  }

//  @Test(order = 1)
  void test2() {
    System.out.println("test2");
  }

//  @Test
  void test3() {
    System.out.println("test3");
  }

}
