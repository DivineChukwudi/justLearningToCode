package praccticeSessions.notes.oop;

public class inheritance {
    public static void main(String[] args) {
        Student s = new Student();
        s.setName("Suzie");
        s.setAge(18);

        System.out.println(s.getAge());
        System.out.println(s.getName());

    }
}





/**
 * INHERITANCE (BASIC NOTES)
 *
 * - Inheritance is an OOP concept where one class (child/subclass)
 *   gets properties and methods from another class (parent/superclass).
 *
 * - The keyword used for inheritance in Java is:
 *     extends
 *
 * - The parent class contains common data and behavior.
 * - The child class can use these without rewriting the code.
 *
 * Example from this file:
 * - Student is the parent class.
 * - Inheritance is the child class.
 *
 * - The child class can:
 *   - create objects of the parent class
 *   - call public methods of the parent class
 *   - access fields that are not private
 *
 * Why inheritance is useful:
 * - Reduces code duplication
 * - Makes programs easier to maintain
 * - Helps organize related classes logically
 *
 * Important rules:
 * - A child class uses 'extends' to inherit from a parent class.
 * - Java supports single inheritance (one parent only).
 * - Private members of a parent class cannot be accessed directly.
 *
 * Note:
 * - In this example, the Student class provides data (name, age)
 *   and methods (setName), while the Inheritance class demonstrates
 *   how an object of Student can be used.
 */
