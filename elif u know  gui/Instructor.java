import java.util.ArrayList;
import java.util.Scanner;
 
public class Instructor extends User {
    private final String department;
    private final ArrayList<Student> students = new ArrayList<>();
 
    public Instructor(String userId, String name, String username, String password,
                      String instructorId, String department) {
        super(userId, name, username, password, "Instructor");
        this.department = department;
    }
 
    public void addStudent(Student student) {
        students.add(student);
    }
 
    public ArrayList<Student> getStudents() {
        return students;
    }
 
    public String getDepartment() {
        return department;
    }
 
    public void viewStudentResults(DataSave data) {
        System.out.println("\n--- Student Results ---");
        for (Student s : students) {
            System.out.println("\n" + s.getName() + " (" + s.getStudentId() + ")");
            boolean hasResult = false;
            for (Result r : data.getResults()) {
                if (r.getStudentId().equals(s.getStudentId())) {
                    System.out.println("  " + r.getTopic() + " (" + r.getDifficulty() + ") | Attempt #"
                            + r.getAttemptNumber() + " | " + r.getScore() + "/" + r.getTotalItems()
                            + " (" + String.format("%.0f", r.getPercentage()) + "%)");
                    hasResult = true;
                }
            }
            if (!hasResult) {
                System.out.println("  No attempts yet.");
            }
        }
    }
 
    public void viewClassPerformance(ArrayList<Quiz> quizzes, DataSave data) {
        System.out.println("\n--- Class Performance ---");
        double overallTotal = 0;
        int overallCount = 0;
 
        for (Quiz q : quizzes) {
            double total = 0;
            int count = 0;
            for (Result r : data.getResults()) {
                if (r.getQuizId() == q.getQuizId()) {
                    total += r.getPercentage();
                    count++;
                }
            }
            if (count > 0) {
                System.out.println(q.getTopic() + " (" + q.getDifficulty() + "): average "
                        + String.format("%.0f", total / count) + "% from " + count + " attempt(s)");
                overallTotal += total;
                overallCount += count;
            }
        }
 
        if (overallCount == 0) {
            System.out.println("No quiz attempts yet.");
        } else {
            System.out.println("Overall class average: "
                    + String.format("%.0f", overallTotal / overallCount) + "%");
        }
    }
 
    public void identifyCommonDifficulties(ArrayList<Quiz> quizzes, DataSave data) {
        System.out.println("\n--- Common Difficulties (average below 60%) ---");
        boolean found = false;
        for (Quiz q : quizzes) {
            double total = 0;
            int count = 0;
            for (Result r : data.getResults()) {
                if (r.getQuizId() == q.getQuizId()) {
                    total += r.getPercentage();
                    count++;
                }
            }
            if (count > 0 && (total / count) < 60) {
                System.out.println("- " + q.getTopic() + " (" + q.getDifficulty() + "): "
                        + String.format("%.0f", total / count) + "%");
                found = true;
            }
        }
        if (!found) {
            System.out.println("No topics with a low class average. Good job, class!");
        }
    }
 
    // Instructor creates a new quiz with questions, difficulty, and time limit
    public void createQuiz(Scanner input, ArrayList<Quiz> quizzes) {
        System.out.println("\n--- Create New Quiz ---");
        System.out.print("Topic: ");
        String topic = input.nextLine();
 
        System.out.println("Difficulty: 1. Basic  2. Average  3. Advanced");
        int d = readNumber(input, "Enter choice: ", 1, 3);
        String level = "Basic";
        if (d == 2) {
            level = "Average";
        } else if (d == 3) {
            level = "Advanced";
        }
 
        int timeLimit = readNumber(input, "Time limit in seconds (10-3600): ", 10, 3600);
        int numQuestions = readNumber(input, "Number of questions (1-10): ", 1, 10);
 
        Quiz quiz = new Quiz(quizzes.size() + 1, topic, level, timeLimit);
 
        for (int i = 1; i <= numQuestions; i++) {
            System.out.println("\nQuestion " + i);
            System.out.print("Question text: ");
            String text = input.nextLine();
 
            String[] choices = new String[4];
            for (int c = 0; c < 4; c++) {
                System.out.print("Choice " + (c + 1) + ": ");
                choices[c] = input.nextLine();
            }
            int correct = readNumber(input, "Correct choice number (1-4): ", 1, 4);
            quiz.addQuestion(new Question(text, choices, correct));
        }
 
        quizzes.add(quiz);
        System.out.println("\nQuiz \"" + topic + "\" was created successfully!");
    }
 
    /** (non-Javadoc)
     * @see User#displayMenu(java.util.Scanner, java.util.ArrayList, DataSave)
     */
    @Override
    public void displayMenu(Scanner input, ArrayList<Quiz> quizzes, DataSave data) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== INSTRUCTOR MENU =====");
            System.out.println("Name: " + getName() + " | Department: " + department);
            System.out.println("1. View students' scores");
            System.out.println("2. View class performance");
            System.out.println("3. Identify common difficulties");
            System.out.println("4. Create a new quiz");
            System.out.println("5. Logout");
            int choice = readNumber(input, "Enter choice: ", 1, 5);
 
            switch (choice) {
                case 1 -> viewStudentResults(data);
                case 2 -> viewClassPerformance(quizzes, data);
                case 3 -> identifyCommonDifficulties(quizzes, data);
                case 4 -> createQuiz(input, quizzes);
                default -> running = false;
            }
        }
    }
}
