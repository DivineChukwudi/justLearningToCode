package praccticeSessions.exercises;

import java.util.*;
public class madLibsGame {
    public static void main(String[] args) {
        //Mad Libs Game
        Scanner sc = new Scanner(System.in);
        String adjective1;
        String noun1;
        String adjective2;
        String verb1;
        String adjective3;

        System.out.print("Enter 1st adjective(descriptive): ");
        adjective1 = sc.nextLine();
        System.out.print("Enter 1st noun(animal): ");
        noun1 = sc.nextLine();
        System.out.print("Enter 2nd adjective(descriptive): ");
        adjective2 = sc.nextLine();
        System.out.print("Enter 1st verb(ending with \"-ing\"): ");
        verb1 = sc.nextLine();
        System.out.print("Enter 3rd adjective(descriptive): ");
        adjective3 = sc.nextLine();

        System.out.println("Today I went to a " + adjective1 + " zoo.");
        System.out.println("In an exhibit i saw a " + noun1 + ".");
        System.out.println(noun1 + " was very " + adjective2 + " and " + verb1 + "!");
        System.out.println("I was " + adjective3 + "!");

        sc.close();

    }
}
