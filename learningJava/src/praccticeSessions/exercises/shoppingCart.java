package praccticeSessions.exercises;

import java.util.*;

public class shoppingCart {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        Double totalPrice;
        System.out.print("What item would you like to buy?: ");
        String item = sc.nextLine();

        StringBuilder sb = new StringBuilder(item);

        System.out.print("What is the price for each?: ");
        Double price =  sc.nextDouble();

        System.out.print("How many would you like?: ");
        int amount = sc.nextInt();

        totalPrice = price * amount;
       
        if (amount > 1) {
            sb.append("s");
            item = sb.toString();
        }
        //output to user
        System.out.println();
        System.out.println("-----Receipt-----");
        System.out.println("You bought " + amount + " " + item);
        System.out.println("Your total is $" + totalPrice);

        sc.close();
    }
}
