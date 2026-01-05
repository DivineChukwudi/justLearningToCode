package exercises;

import java.util.*;

public class areaOfRec{
    public static void main(String[] args){
        
        //Calculate area of rectangle
        double width = 0;
        double height = 0;
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter width: ");
        width = sc.nextDouble();

        System.out.print("Enter height: ");
        height = sc.nextDouble();
        double area = width * height;
        System.out.println("Area of rectangle is: " + area + " cm².");

        //superscript for cm^2 is numlock on + alt + 0178 = cm²
        sc.close();
    }
}