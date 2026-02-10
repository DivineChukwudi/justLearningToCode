package exercises;
import java.util.*;

public class hypotenuse {
    public static void main(String[] args) {
        
        // Find Hypotenuse  c = sqrt(a² + b²)

        Scanner sc =  new Scanner(System.in);

        double a, b, c;

        System.out.print("Enter 1st value(repre x): ");
        a = sc.nextInt();

        System.out.print("Enter 2nd value(repre y): ");
        b =  sc.nextInt();

        c = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));

        System.out.print("The hypotenuse of values " + a + " & " + b + " is: " + c);

        sc.close();

    }
}
