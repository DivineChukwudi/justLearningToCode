package praccticeSessions.exercises;
import java.util.*;

public class sphere {
    public static void main(String[] args) {

        // given the radius of a circle: 
        // circumference = 2 * Math.PI * radius;
        // area = Math.PI * Math.pow(radius, 2);
        // volume = (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);

        Scanner sc = new Scanner(System.in);

        double radius, circum, area, volume;

        System.out.print("Enetr radius of sphere: ");
        radius = sc.nextDouble();

        circum = 2 * Math.PI * radius;
        area = Math.PI * Math.pow(radius, 2);
        volume = (4.0 / 3.0) * Math.PI * Math.pow(radius, 3);

        System.out.print("The Circumference of the radius " + radius + " is: " + circum + ", its Area is: " + area + " & its Volume is: " + volume);
        
        // To print a certain amount of decimals we use the "f" function
        System.out.println();
        //  This is wrong ill get back to it. 
        //System.out.printf("The Circumference of the radius " + radius + " is: %.1f" , circum + ", its Area is: %.1f", area + " & its Volume is: %.1f", volume);

    }
}
