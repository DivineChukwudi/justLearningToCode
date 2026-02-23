package praccticeSessions.notes;

public class ifStatements {
    public static void main(String[] args) {
        
        // If statement = perorms a block of code if its condition is true
        // positioning of the statments matters!
        int age = 0;
        if (age >= 18) {
            System.out.println("You are an adult!");
        }else if (age < 0){
            System.out.println("You haven't been born yet!");
        }
        else if (age == 0){
            System.out.println("Blud just came out lmao, on some \"Hello, World!\".😂 ");
        }
        else {
            System.out.println("You are not an adult!");
        }
    }
}
