package sem1_lambda_stream;

import lesson1.Streams;
import org.w3c.dom.ls.LSOutput;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        persons.stream()
                .map(Person::getName)
                .distinct()
                .sorted(String::compareToIgnoreCase)
                .forEach(System.out::println);
    }

    // распечатать MAP: в каждом отделе список вохрастов, упорядоченный по убыванию
    public static void printMappedAgesByDeprt(List<Person> persons){
        Map<Department, List<Integer>> departmentAgeMap = persons.stream()
                .collect(
                        Collectors.groupingBy(
                                Person::getDepartment,
                                Collectors.mapping(
                                        Person::getAge,
                                        Collectors.collectingAndThen(
                                            Collectors.toList(),
                                            (List<Integer> ages) -> {
                                                ages.sort(Comparator.reverseOrder());
                                                return ages;
                                            }
                                        )
                                )
                        )
                );
        System.out.println("\n>>>>>>>>> ОТДЕЛЫ СО СПИСКОМ ВОЗРАСТОВ по убыванию");
        departmentAgeMap.entrySet()
                .stream().sorted(Map.Entry.comparingByKey(Comparator.comparing(Department::toString)))
                .forEach(System.out::println);

    }


    /**
     * В каждом департаменте найти самого взрослого сотрудника.
     * Вывести на консоль мапипнг department -> personName
     * Map<Department, Person>
     */
    public static Map<Department, Person> printDepartmentOldestPerson(List<Person> persons) {
        Comparator<Person> ageComparator = Comparator.comparing(Person::getAge);

        Map<Department, Person> oldestPersonByDeprt = persons.stream()
                .collect(Collectors.toMap(
                        Person::getDepartment,
                        Function.identity(),
                        (first, second) -> {
                            if (ageComparator.compare(first, second) > 0)
                                return first;
                            return second;
                        }));

        System.out.println("\n>>>>>>>>> ОТДЕЛЫ С САМЫМ СТАРШИМ сотрудником (сортировка по убыванию от самого старшего)");
        oldestPersonByDeprt.entrySet()
                .stream().sorted(Map.Entry.comparingByValue(Comparator.comparing(Person::getAge).reversed()))
                        .forEach(System.out::println);

        return oldestPersonByDeprt;
    }


    /**
     * Найти 10 первых сотрудников, младше 30 лет, у которых зарплата выше 50_000
     */
    public static List<Person> findFirstPersons(List<Person> persons) {
        List<Person> youngestRichest = persons.stream()
                .filter(person -> person.getAge() < 30)
                .filter(person -> person.getSalary() > 50000)
                .limit(10)
                .sorted(Comparator.comparing(Person::getSalary).reversed())
                .toList();

        System.out.println("\n>>>>>>>>> СОТРУДНИКИ младше 30 с ЗП больше 50 000 (сортировка по ЗП)");

        youngestRichest.forEach(System.out::println);

        return youngestRichest;
    }


    /**
     * Найти депаратмент, чья суммарная зарплата всех сотрудников максимальна
     */
    public static Optional<Department> findTopDepartment(List<Person> persons) {
        System.out.println("\n>>>>>>>>> САМЫЙ ФИНАНСИРУЕМЫЙ ОТДЕЛ");

        Map<Department, Double> mapDepartment = persons.stream()
                .collect(
                        Collectors.groupingBy(
                                Person::getDepartment,
                                Collectors.summingDouble(Person::getSalary))
                        );

        // выведем список всех отделов, отсортированный по убыванию суммы финансирования
        mapDepartment.entrySet()
                .stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(System.out::println);

        Optional<Department> richestDepartment = mapDepartment.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);

        System.out.println(richestDepartment);

        return richestDepartment;
    }

}
