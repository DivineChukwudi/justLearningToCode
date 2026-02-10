package exercises;

public class factorial {
    public static void main(String[] args) {

    int userInput = 5, i;
    int totalFac = 1;


    for (i = 1; i <= userInput; i++ ){
        totalFac *= i; 
    }


    
    System.out.println(totalFac);
    }
}
