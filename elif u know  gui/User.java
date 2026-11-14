import java.util.ArrayList;
import java.util.Scanner;
 

public abstract class User {
    
    private final String userId;
    private final String name;
    private final String username;
    private final String password;
    private final String role;
 
    public User(String userId, String name, String username, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }
 
    
    public boolean login(String inputUsername, String inputPassword) {
        return username.equals(inputUsername) && password.equals(inputPassword);
    }
 
    public void logout() {
        System.out.println("Goodbye, " + name + "! You have been logged out.");
    }
 
    public String getRole() {
        return role;
    }
 
    public String getUserId() {
        return userId;
    }
 
    public String getName() {
        return name;
    }
 
    public String getUsername() {
        return username;
    }
 
    
    public abstract void displayMenu(Scanner input, ArrayList<Quiz> quizzes, DataSave data);
 
    
    public static int readNumber(Scanner input, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int number = Integer.parseInt(input.nextLine().trim());
                if (number >= min && number <= max) {
                    return number;
                }
                System.out.println("Please enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter numbers only.");
            }
        }
    }
}