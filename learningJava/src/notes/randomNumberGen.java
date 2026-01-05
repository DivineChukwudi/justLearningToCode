package notes;
import java.util.*;

public class randomNumberGen {
    public static void main(String[] args){
        Random rd = new Random();

        int num;

        num = rd.nextInt(55, 59);
        System.out.println("Random Integer: " + num);

    }
}
