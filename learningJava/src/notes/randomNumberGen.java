package notes;
import java.util.*;

public class randomNumberGen {
    public static void main(String[] args){
        Random rd = new Random();

        /*int num;

        num = rd.nextInt(55, 59);
        System.out.println("Random Integer: " + num);*/


        /*double number;
        number = rd.nextDouble(1, 3);
        System.out.print(number);8*/

        boolean isHeads;
        isHeads = rd.nextBoolean();
        //System.out.print(isHeads);

        if(isHeads) {
            System.out.print("HEADS!");
        }
        else {
            System.out.print("TAILS!");
        }

    }
}
