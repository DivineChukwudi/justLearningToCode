package praccticeSessions.exercises.oop;

public class BankTest {
      public static void main(String[] args) {
        BankAccount account = new BankAccount();
        
        account.deposit(500);   // Deposited: 500. New balance: 500
        account.withdraw(200);  // Withdrew: 200. New balance: 300
        account.withdraw(400);  // Insufficient funds. Current balance: 300
    }
}
