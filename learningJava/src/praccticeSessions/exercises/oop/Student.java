package praccticeSessions.exercises.oop;

public class Student {
    private String name;
    private int age;


    // setters
    public void setName(String newName, int newAge){
        name = newName;
        age = newAge;
        

        if (age == 0) {
            age = newAge;
        }else{
             System.out.println("Age has already been set and cannot be changed.");
        }
    }
}
