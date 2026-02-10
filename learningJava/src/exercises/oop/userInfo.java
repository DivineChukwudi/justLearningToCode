package exercises.oop;
import java.util.*;

public class userInfo {
    public static void main(String[] args){
        Student s = new Student();
        Scanner sc = new Scanner(System.in);

        String userName;
        int userAge = 0;

        System.out.print("Enter your name: ");
        userName = sc.nextLine();
        while (userAge < 1) {
        System.out.print("Enter your age: ");
        userAge = sc.nextInt();
        }

        s.setName(userName, userAge);
        
        sc.close();
    }
}
