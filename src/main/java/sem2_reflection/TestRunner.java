package sem2_reflection;

import sem2_reflection.annotations.*;

import java.lang.reflect.*;
import java.util.*;

public class TestRunner {

    private static ArrayList<Method> beforeAllList = new ArrayList<>();
    private static ArrayList<Method> beforeEachList = new ArrayList<>();
    private static TreeMap<Integer, Method> testList = new TreeMap<>();
    private static ArrayList<Method> afterEachList = new ArrayList<>();
    private static ArrayList<Method> afterAllList = new ArrayList<>();

    public static void run(Class<?> testClass) {
        // Для того чтобы вызывать тест-методы, нам нужен объект, на котором мы их будем вызывать.
        // Поэтому нам надо сначала создать объект класса, который передаётся в TestRunner.
        // Создадим для этого отдельный метод initTestObj
        final Object testObj = initTestObj(testClass);

        // получаем список всех методов тестирующего класса testClass
        for (Method testMethod : testClass.getDeclaredMethods()) {

            // если метод приватный, мы его пропускаем
            if (testMethod.accessFlags().contains(AccessFlag.PRIVATE)) {
                continue;
            }
            // System.out.print("Method " + testMethod.getName() + ", annotation: ");
            // Arrays.stream(testMethod.getAnnotations()).forEach(System.out::print);
            // System.out.println();

            // Далее работаем с оставшимися после проверки публичными методами.
            // Получаем аннотацию и проверяем её класс.
            // Если класс полученной аннотации = @Test, то запускаем этот метод.
            if (testMethod.getAnnotation(BeforeAll.class) != null) {
                beforeAllList.add(testMethod);
            }
            if (testMethod.getAnnotation(BeforeEach.class) != null) {
                beforeEachList.add(testMethod);
            }
            if (testMethod.getAnnotation(Test.class) != null) {
                int key = testMethod.getAnnotation(Test.class).order();
                testList.put(key, testMethod);
            }
            if (testMethod.getAnnotation(AfterEach.class) != null) {
                afterEachList.add(testMethod);
            }
            if (testMethod.getAnnotation(AfterAll.class) != null) {
                afterAllList.add(testMethod);
            }
        }

        //testList.entrySet().stream().forEach(entry -> System.out.println(entry.getKey() +" : " + entry.getValue()));

        runMethodList(beforeAllList, testObj);
        for (Method test : testList.values()) {
            runMethodList(beforeEachList, testObj);
            try {
                test.invoke(testObj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            runMethodList(afterEachList, testObj);
            System.out.println();
        }
        runMethodList(afterAllList, testObj);

    }

    private static void runMethodList(ArrayList<Method> methList, Object targetObj){
        if (!methList.isEmpty()) {
            for (Method method : methList) {
                try {
                    method.invoke(targetObj);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static Object initTestObj(Class<?> testClass) {
        try {
            // создаем объект КОНСТРУКТОР передаваемого в TestRunner класса
            // вызываем этот конструктор, чтобы создать объект передаваемого класса
            Constructor<?> noArgsConstructor = testClass.getConstructor();

            // возвращаем объект тестируемого класса
            return noArgsConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            // это исключение может возникнуть при вызове testClass.getConstructor()
            throw new RuntimeException("Нет конструктора по умолчанию");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            // всё ЭТО - исключения, которые бросает noArgsConstructor.newInstance()
            throw new RuntimeException("Не удалось создать объект тестирующего класса");
        }
    }

}
