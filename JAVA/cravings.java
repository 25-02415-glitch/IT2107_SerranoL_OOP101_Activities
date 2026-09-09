import java.util.Scanner;

public class cravings {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        String[] menuItems = {
            "Takoyaki",
            "Coke Float",
            "Sundae",
            "Carbonara",
            "French Fries"
        };

        double[] prices = {
            70.00,
            60.00,
            50.00,
            90.00,
            99.00
        };

        int totalQuantity = 0;
        double totalAmount = 0.00;
        boolean isStudent = false;
        boolean statusEntered = false;

        System.out.println("====== CRAVINGS CAFE ======");

        for (int i = 0; i < menuItems.length; i++) {
            System.out.printf("%d. %-20s PHP %.2f%n",
                    i + 1, menuItems[i], prices[i]);
        }

        String orderAgain = "Yes";

        while (orderAgain.equalsIgnoreCase("Yes")) {

            System.out.print("\nEnter item number (1-5): ");
            int itemNumber = input.nextInt();

            System.out.print("Enter quantity (1-10): ");
            int quantity = input.nextInt();

            System.out.print("Are you a student? (Yes or No): ");
            String studentStatus = input.next();

            if (itemNumber < 1 || itemNumber > 5) {

                System.out.println("\nInvalid item number!");
                System.out.println("Order was not included.");
                System.out.println("Please try another order.");

                System.out.print("\nDo you want to order again? (Yes or No): ");
                orderAgain = input.next();

                continue;
            }

            if (quantity < 1 || quantity > 10) {

                System.out.println("\nInvalid quantity!");
                System.out.println("Quantity must be between 1 and 10.");
                System.out.println("Order was not included.");

                System.out.print("\nDo you want to order again? (Yes or No): ");
                orderAgain = input.next();

                continue;
            }

            if (!studentStatus.equalsIgnoreCase("Yes")
                    && !studentStatus.equalsIgnoreCase("No")) {

                System.out.println("\nInvalid student status!");
                System.out.println("Please enter Yes or No.");
                System.out.println("Order was not included.");

                System.out.print("\nDo you want to order again? (Yes or No): ");
                orderAgain = input.next();

                continue;
            }

            if (!statusEntered) {
                isStudent = studentStatus.equalsIgnoreCase("Yes");
                statusEntered = true;
            }

            double orderAmount = prices[itemNumber - 1] * quantity;

            totalQuantity += quantity;
            totalAmount += orderAmount;

            System.out.println("\n ORDER DETAILS");
            System.out.println("Item: " + menuItems[itemNumber - 1]);
            System.out.println("Quantity: " + quantity);
            System.out.printf("Order amount: PHP %.2f%n", orderAmount);
           
            System.out.print("\nDo you want to order again? (Yes or No): ");
            orderAgain = input.next();
        }

      
        double discountRate;

        if (isStudent && totalAmount >= 500) {
            discountRate = 0.15;

        } else if (totalAmount >= 500) {
            discountRate = 0.05;

        } else if (isStudent) {
            discountRate = 0.10;

        } else {
            discountRate = 0.00;
        }

        double totalDiscount = totalAmount * discountRate;
        double finalAmount = totalAmount - totalDiscount;


        System.out.println("Total quantity purchased: " + totalQuantity);

        System.out.printf("Total amount before deductions: PHP %.2f%n",
                totalAmount);

        System.out.printf("Total deduction: PHP %.2f%n",
                totalDiscount);

        System.out.printf("Final amount to pay: PHP %.2f%n",
                finalAmount);

        System.out.println("Thank you for ordering!");

        input.close();
    }
}