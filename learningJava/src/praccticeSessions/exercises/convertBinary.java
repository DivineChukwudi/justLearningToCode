package praccticeSessions.exercises;
import java.util.*;

public class convertBinary{
    public static void main(String[] args){
        // Convert Interger to Binary

        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter number to cenvert to binary: ");
        int userInput = sc.nextInt();

        String binaryNumber = "";

        while (userInput > 0) {
            int calc = userInput % 2;  
            binaryNumber = Integer.toString(calc) + binaryNumber;
            userInput = userInput / 2;
            }

        System.out.println(binaryNumber);
        }
        

}