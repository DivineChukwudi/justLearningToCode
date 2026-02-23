package praccticeSessions.notes;

public class printf {
    public static void main(String[] args) {
        // printf() =  is a method used to format output

        //%[flags][width][.precision][specifier-character]
        /*String name = "Divine";
        char firstLetter = 's';
        int age = 30;
        double height = 60.5;
        boolean isEmployed = true;


        //specifier-character
        System.out.printf("Hello %s\n", name);
        System.out.printf("Hello %c\n", firstLetter);
        System.out.printf("Hello, you are %d years old\n", age);
        System.out.printf("Hello, you are %f tall\n", height);
        System.out.printf("Employed: %b\n", isEmployed);
        System.out.printf("%s %c %d %f %b", name, firstLetter, age, height, isEmployed);
        */


        /* 
        double price1 = 9.99;
        double price2 = 100.15;
        double price3 = -54.01;

        //precision and flags
        //displaying the amount of digits after the . sign
        System.out.printf("%+.1f\n", price1);
        System.out.printf("%.3f\n", price2);
        System.out.printf("% .2f", price3);
        */


        //flags
        //  + = output a plus
        //  , = coma grouping separator
        //  ( = negative numbers are enclosed in ()
        //  space = display a minus if negative, space if positive



        //width
        // 0 = zero padding
        // number = right justiied padding
        // negative number = left justified padding

        int id1 = 1;
        int id2 = 23;
        int id3 = 456;
        int id4 = 7890;
        System.out.printf("%-4d\n", id1);
        System.out.printf("%-4d\n", id2);
        System.out.printf("%-4d\n", id3);
        System.out.printf("%-4d", id4);


    }
}
