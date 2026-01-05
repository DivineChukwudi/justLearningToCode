package exercises;
import java.util.*;

public class phoneNumber {
    public static void main(String[] args) {
        Random rd = new Random();
        //First 3 digits
        int first, second, third, middle, last;

        first = rd.nextInt(7);
        second = rd.nextInt(7);
        third = rd.nextInt(7);
        middle =  rd.nextInt(100, 742);
        last = rd.nextInt(9999);

        System.out.println(first +""+ second +""+ third + "-" + middle + "-" + last);

    }
}
