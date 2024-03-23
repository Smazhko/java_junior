package lesson2.random;

import java.lang.reflect.Field;
import java.util.Random;

public class RandomIntegerInitializer {

  private final static Random random = new Random();

  public static void init(Object obj) {
    // получаем класс передаваемого объекта
    Class<?> objClass = obj.getClass();

    // получаем список полей в полученном объекте и прогоняем их через цикл
    for (Field field : objClass.getDeclaredFields()) {
      // у каждого поля получаем аннотацию
      RandomInteger anno = field.getAnnotation(RandomInteger.class);

      // если аннотация не пустая, то проверяем принадлежность поля к классу int.
      // если класс int, то создаём переменные min max и заполняем их информацией из аннотации к этому полю
      // добыть значение параметров аннотации можно, обратившись к методам .min() .max()
      if (anno != null) {
        if (int.class.equals(field.getType())) {
          int min = anno.min();
          int max = anno.max();

          // генерируем рандомное значение в указанных пределах
          int randomValue = random.nextInt(min, max);

          // пытаемся получить доступ к полю, если вдруг оно приватное
          // и записываем в это полде полученное рандомное значение
          try {
            field.setAccessible(true);
            field.set(obj, randomValue);
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
  }

}
