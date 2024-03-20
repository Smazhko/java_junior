package ru.gb.lesson2.tests;

import java.lang.reflect.AccessFlag;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestRunner {

  public static void run(Class<?> testClass) {
    final Object testObj = initTestObj(testClass);
    for (Method testMethod : testClass.getDeclaredMethods()) {
      if (testMethod.accessFlags().contains(AccessFlag.PRIVATE)) {
        continue;
      }

      if (testMethod.getAnnotation(Test.class) != null) {
        try {
          testMethod.invoke(testObj);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private static Object initTestObj(Class<?> testClass) {
    try {
      Constructor<?> noArgsConstructor = testClass.getConstructor();
      return noArgsConstructor.newInstance();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Нет конструктора по умолчанию");
    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
      throw new RuntimeException("Не удалось создать объект тест класса");
    }
  }

}
