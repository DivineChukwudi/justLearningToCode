package praccticeSessions.notes.oop;

public class Student {
    private String name;
    private int age;

    public void setName(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public void setAge(int newAge){
        newAge = age;
    }

    public int getAge() {
        return age;
    }

}

// Notes on setting:
// since we set and not returning anything to user we use 'VOID'
//to bypass the setting of a parameter name being the same as the instance variable e.g:
/*
public class Student {
    private String name;
    private int age;

    public void setName (String name){
    this.name = name;
    }
 */
// An instance is a specific object created from a class.
// Think of a class as a blueprint, and an instance as the actual thing built from that blueprint.