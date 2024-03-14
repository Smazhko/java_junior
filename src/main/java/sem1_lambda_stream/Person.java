package sem1_lambda_stream;

public class Person {
    private String name;
    private Integer age;
    private Double salary;
    private Department department;

    public Person(String name, Integer age, Double salary, Department department) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.department = department;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public Double getSalary() {
        return salary;
    }

    public Department getDepartment() {
        return department;
    }

    @Override
    public String toString() {
        return String.format("%-9s (%s) ЗП %10s ₽ %s",name, age, String.format("%,.2f",salary), department);
    }
}
