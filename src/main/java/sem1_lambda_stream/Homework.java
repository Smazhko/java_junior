package sem1_lambda_stream;

import lesson1.Streams;
import org.w3c.dom.ls.LSOutput;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Homework {
    public static void main(String[] args) {
        List<Department> depList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            depList.add( new Department("Отдел #" + i));
        }

        String[] names = {"Виталий", "Петр", "Никита", "Иван", "Сергей", "Алексей", "Антонина", "Ирина", "Лариса", "Ольга",
        "Владислав", "Александр", "Милана", "Анатолий", "Дарья", "Вероника", "Алёна", "Михаил", "Елена", "Анна",
        "Олег", "Тимофей", "Григорий", "Маргарита", "Вячеслав", "Родион"};

        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            persons.add(new Person(names[ThreadLocalRandom.current().nextInt(names.length)],
                    ThreadLocalRandom.current().nextInt(20,66),
                    ThreadLocalRandom.current().nextInt(20,120) * 1000.0,
                    depList.get(ThreadLocalRandom.current().nextInt(depList.size())))
            );
        }
        printNamesOrdered(persons);
        printMappedAgesByDeprt(persons);
        Map<Department, Person> oldestPersons = printDepartmentOldestPerson(persons);
        findFirstPersons(persons);
        findTopDepartment(persons);

    }

    /**
     * Вывести на консоль отсортированные (по алфавиту) имена персонов
     */
    public static void printNamesOrdered(List<Person> persons) {
        System.out.println("\n>>>>>>>>> СПИСОК УНИКАЛЬНЫХ ИМЁН ПО АЛФАВИТУ");
        persons.stream()                                    // Stream<Person>
                .map(Person::getName)                       // преобразуем в поток, состоящий из имён. лямбда (x) -> x.getName()
                .distinct()                                 // оставляем только уникальные значения
                .sorted(String::compareToIgnoreCase)        // сортируем по возрастанию, игнорируя регистр.
                                                            // в качестве компаратора - уже готовый метод сравнения из класса STRING
                .forEach(System.out::println);              // терминальный оператор - каждое значение выводим в консоль
                                                            // лямбда (x) -> System.out.println(x);

    }

    // распечатать MAP: в каждом отделе список возрастов, упорядоченный по убыванию
    public static void printMappedAgesByDeprt(List<Person> persons){
        Map<Department, List<Integer>> departmentAgeMap = persons.stream()
                .collect(                                                   // собрать коллекцию
                        Collectors.groupingBy(                              // создание MAP путём группировки по какому-то признаку:
                                Person::getDepartment,                      // сам признак - ключ для MAP, value для MAP - список => используем функцию сборки списка
                                Collectors.mapping(                         // список собираем из значений, которые достаём из объектов => MAP-ирование объектов
                                        Person::getAge,                     // список составляют возраста - получаем их лямбдой (x) -> x.getAge()
                                        Collectors.collectingAndThen(       // чтобы упорядочить список по убыванию, сразу используем .collectingAndThen
                                            Collectors.toList(),            // поток, над которым надо выполнить действие
                                            (List<Integer> ages) -> {       // и метод(лямбда), который надо применить для получения результата
                                                ages.sort(Comparator.reverseOrder());
                                                return ages;
                                            }
                                        )
                                )
                        )
                );

        /*
        Collectors.collectingAndThen()
        Адаптирует коллектор для выполнения дополнительного завершающего преобразования. Например, можно было бы адаптировать
        коллектор ToList(), чтобы он всегда создавал неизменяемый список с:
           List<String> list = people.stream().collect(
                collectingAndThen(toList(),
                Collections::unmodifiableList));
        Параметры:
          downstream – коллектор
          finisher – функция, которая будет применена к конечному результату коллектора
        Возвращается:
          Collector, который является результатом выполнения функции finisher над потоком downstream
         */
        System.out.println("\n>>>>>>>>> ОТДЕЛЫ (упорядоченные по алфавиту) СО СПИСКОМ ВОЗРАСТОВ по убыванию");
        departmentAgeMap.entrySet()
                .stream().sorted(                           // сортируем поток
                        Map.Entry.comparingByKey(           // сортируем map.Entry по ключу, применяя компаратор
                                Comparator.comparing(Department::toString)      // - сравнение отделов по имени
                        )
                )
                .forEach(System.out::println);
    }


    /**
     * В каждом департаменте найти самого взрослого сотрудника.
     * Вывести на консоль мапипнг department -> personName
     * Map<Department, Person>
     */
    public static Map<Department, Person> printDepartmentOldestPerson(List<Person> persons) {
        Comparator<Person> ageComparator = Comparator.comparing(Person::getAge);

        // сначала создаём МАР, которую собираем так: key - отдел, value - значение, которое останется после сравнения всех сотрудников по возрастам
        Map<Department, Person> oldestPersonByDeprt = persons.stream()
                .collect(Collectors.toMap(
                        Person::getDepartment,          // получаем ключ
                        Function.identity(),            // получаем значение
                        (first, second) -> {            // путём выполнения ЛЯМБДЫ,
                            if (ageComparator.compare(first, second) > 0)       // которая использует другую ЛЯМБДУ ageComparator
                                return first;                                   // для вычисления максимального значения возраста
                            return second;
                        }
                        )
                );

        // то есть в конструкции Function.identity(), (first, second) -> {  ...  }
        // Function.identity() будет выступать в качестве "накопителя" результата,
        // а ЛЯМБДА (first, second) -> { ... } будет записывать в Function.identity() результат сравнения

        // далее полученную МАР мы просто сортируем по VALUE в ОБРАТНОМ ПОРЯДКЕ (.reversed())
        System.out.println("\n>>>>>>>>> ОТДЕЛЫ С САМЫМ СТАРШИМ сотрудником (сортировка по убыванию от самого старшего)");
        oldestPersonByDeprt.entrySet()
                .stream().sorted(
                        Map.Entry.comparingByValue(                     // компаратор,
                                Comparator.comparing(Person::getAge)    // который принимает ещё один компаратор,
                                        .reversed()                     // который можно инвертировать
                        )
                )
                .forEach(System.out::println);

        return oldestPersonByDeprt;
    }


    /**
     * Найти 10 первых сотрудников, младше 30 лет, у которых зарплата выше 50_000
     */
    public static List<Person> findFirstPersons(List<Person> persons) {
        List<Person> youngestRichest = persons.stream()
                .filter(person -> person.getAge() < 30)
                .filter(person -> person.getSalary() > 50_000)
                .limit(10)
                .sorted(Comparator
                        .comparing(Person::getSalary)
                        .reversed()
                )
                .toList();

        System.out.println("\n>>>>>>>>> СОТРУДНИКИ младше 30 с ЗП больше 50 000 (сортировка по ЗП)");

        youngestRichest.forEach(System.out::println);

        return youngestRichest;
    }


    /**
     * Найти департамент, чья суммарная зарплата всех сотрудников максимальна
     */
    public static Optional<Department> findTopDepartment(List<Person> persons) {
        System.out.println("\n>>>>>>>>> САМЫЙ ФИНАНСИРУЕМЫЙ ОТДЕЛ");

        // формируем МАР путём группировки списка по отделам.
        Map<Department, Double> mapDepartment = persons.stream()
                .collect(
                        Collectors.groupingBy(
                                Person::getDepartment,                      // key - отдел
                                Collectors.summingDouble(Person::getSalary))// value - сумма всех зарплат в отделе
                        );

        // выведем список всех отделов, отсортированный по убыванию суммы финансирования
        mapDepartment.entrySet()
                .stream().sorted(
                        Map.Entry.comparingByValue(
                                Comparator.reverseOrder()
                        )
                )
                .forEach(System.out::println);

        // получаем отдел с максимальной суммой зарплат в виде Optional.
        // вычисляем максимум по value - .comparingByValue()
        // а потом из полученного ENTRY вынимаем ключ - название отдела
        Optional<Department> richestDepartment = mapDepartment.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);

        System.out.println(richestDepartment);

        return richestDepartment;
    }

}

/*
>>>>>>>>> СПИСОК УНИКАЛЬНЫХ ИМЁН ПО АЛФАВИТУ
Александр
Алексей
Алёна
Анатолий
Анна
Антонина
Вероника
Владислав
Вячеслав
Григорий
Дарья
Елена
Иван
Ирина
Лариса
Маргарита
Никита
Ольга
Петр
Сергей
Тимофей

>>>>>>>>> ОТДЕЛЫ СО СПИСКОМ ВОЗРАСТОВ по убыванию
Отдел #1=[61, 50, 50, 43, 36, 35, 28, 20]
Отдел #2=[58, 49, 45, 35, 26, 24]
Отдел #3=[64, 63, 62, 41, 34, 28, 28]
Отдел #4=[49, 34, 30, 27]
Отдел #5=[62, 51, 40]
Отдел #6=[49, 45, 22]
Отдел #7=[63, 54, 47, 30, 29, 21, 21]
Отдел #8=[44, 43, 26, 23, 21]
Отдел #9=[65, 61, 57, 38, 33, 27, 22]

>>>>>>>>> ОТДЕЛЫ С САМЫМ СТАРШИМ сотрудником (сортировка по убыванию от самого старшего)
Отдел #9=Алёна     (65) ЗП  84 000,00 ₽ Отдел #9
Отдел #3=Иван      (64) ЗП 109 000,00 ₽ Отдел #3
Отдел #7=Дарья     (63) ЗП 112 000,00 ₽ Отдел #7
Отдел #5=Ирина     (62) ЗП  31 000,00 ₽ Отдел #5
Отдел #1=Анатолий  (61) ЗП  70 000,00 ₽ Отдел #1
Отдел #2=Григорий  (58) ЗП  58 000,00 ₽ Отдел #2
Отдел #4=Тимофей   (49) ЗП 103 000,00 ₽ Отдел #4
Отдел #6=Вероника  (49) ЗП  34 000,00 ₽ Отдел #6
Отдел #8=Дарья     (44) ЗП  64 000,00 ₽ Отдел #8

>>>>>>>>> СОТРУДНИКИ младше 30 с ЗП больше 50 000 (сортировка по ЗП)
Тимофей   (28) ЗП 103 000,00 ₽ Отдел #3
Елена     (26) ЗП  99 000,00 ₽ Отдел #2
Петр      (27) ЗП  88 000,00 ₽ Отдел #9
Петр      (21) ЗП  79 000,00 ₽ Отдел #8
Алёна     (23) ЗП  75 000,00 ₽ Отдел #8
Маргарита (21) ЗП  75 000,00 ₽ Отдел #7
Елена     (28) ЗП  65 000,00 ₽ Отдел #1
Лариса    (29) ЗП  53 000,00 ₽ Отдел #7

>>>>>>>>> САМЫЙ ФИНАНСИРУЕМЫЙ ОТДЕЛ
Отдел #3=572000.0
Отдел #9=475000.0
Отдел #1=468000.0
Отдел #7=420000.0
Отдел #2=336000.0
Отдел #8=315000.0
Отдел #4=305000.0
Отдел #5=204000.0
Отдел #6=98000.0
Optional[Отдел #3]
 */
