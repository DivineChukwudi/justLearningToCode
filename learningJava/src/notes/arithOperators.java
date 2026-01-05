package notes;

public class arithOperators {
    public static void main (String[] args) {
        //Aritmetic Operators

        //int x = 10;
        //int y = 2;
        //int z;

        //z = x + y;
        //z = x - y;
        //z = x * y;
        //z = x / y;
        //z = x % y; //modulus operator gives remainder
        //System.out.println("Addition: " + z); //12

        //Augmetned Assignment Operators

        //int a = 5;
        //a += 3; //a = a + 3
        //a -= 2; //a = a - 2
        //a *= 4; //a = a * 4
        //a /= 2; //a = a / 2
        //a %= 3; //a = a % 3


        //INCREMENT AND DECREMENT OPERATORS
        int x = 1;
        x++; //increment operator adds 1 (post increment)
        x--; //decrement operator subtracts 1 (post decrement)
        ++x; //pre increment
        --x; //pre decrement


        //Order of operations (PEMDAS)
        double result;
        result = (5 + 3) * 2; //parentheses first
        result = 5 + 3 * 2; //multiplication before addition
        System.out.println("Result: " + result); //16.0
        
    }
}
