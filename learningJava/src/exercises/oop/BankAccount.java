package exercises.oop;

public class BankAccount {
    private double balance;

    // Constructor to start with 0 balance
    public BankAccount() {
        this.balance = 0;
    }

    // Deposit method
    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: " + amount + ". New balance: " + balance);
        } else {
            System.out.println("Deposit must be positive.");
        }
    }

    // Withdraw method
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Withdrawal must be positive.");
        } else if (amount > balance) {
            System.out.println("Insufficient funds. Current balance: " + balance);
        } else {
            balance -= amount;
            System.out.println("Withdrew: " + amount + ". New balance: " + balance);
        }
    }

    // Getter for balance
    public double getBalance() {
        return balance;
    }
}


