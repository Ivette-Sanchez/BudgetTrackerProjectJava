import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// represnt a financial transaction income/expense
public class Transaction {
    private String description;
    private double amount;
    private Category category;
    private LocalDate date;

    // constructor --> accepts description, amount, category, date as string --> parses date to LocalDate
    public Transaction(String description, double amount, Category category, String date) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = LocalDate.parse(date); // converts date string to LocalDate object
    }

    // Getters for all 
    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    // returns a string representation of the transaction for display
    @Override
    public String toString() {
        return description + " | " + category.getName() + " | " + date.toString() + " : $" + String.format("%.2f", amount);
    }
    
}
