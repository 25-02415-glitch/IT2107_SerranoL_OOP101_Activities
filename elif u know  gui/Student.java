import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
 
public class Student extends User {
    private final String studentId;
    private final String section;
    private final ArrayList<Result> attemptHistory = new ArrayList<>();
 
    public Student(String userId, String name, String username, String password,
                   String studentId, String section) {
        super(userId, name, username, password, "Student");
        this.studentId = studentId;
        this.section = section;
    }
 
    public String getStudentId() {
        return studentId;
    }
 
    public String getSection() {
        return section;
    }
 
    public ArrayList<Result> getAttemptHistory() {
        return attemptHistory;
    }
 
    
    public Result getPerfectResult(int quizId) {
        for (Result r : attemptHistory) {
            if (r.getQuizId() == quizId && r.getTotalItems() > 0 && r.getScore() == r.getTotalItems()) {
                return r;
            }
        }
        return null;
    }
 
    
    public Result recordAttempt(Quiz quiz, int[] answers, DataSave data) {
        int total = quiz.getTotalQuestions();
        int score = quiz.computeScore(answers);
        int attemptNumber = 1;
        for (Result r : attemptHistory) {
            if (r.getQuizId() == quiz.getQuizId()) {
                attemptNumber++;
            }
        }
        int newId = data.getResults().size() + 1;
        Result result = new Result(newId, studentId, quiz.getQuizId(), quiz.getTopic(),
                quiz.getDifficultyLevel(), score, total, attemptNumber, LocalDate.now().toString());
        attemptHistory.add(result);
        data.addResult(result);
        data.saveData();
        return result;
    }
 
    public void addToHistory(Result result) {
        attemptHistory.add(result);
    }
 
    
    public Quiz selectDifficulty(Scanner input, ArrayList<Quiz> quizzes) {
        System.out.println("\nChoose difficulty:");
        System.out.println("  1. Basic");
        System.out.println("  2. Average");
        System.out.println("  3. Advanced");
        int choice = readNumber(input, "Enter choice: ", 1, 3);
 
        String level = "Basic";
        if (choice == 2) {
            level = "Average";
        } else if (choice == 3) {
            level = "Advanced";
        }
 
        
        ArrayList<Quiz> available = new ArrayList<>();
        System.out.println("\nAvailable " + level + " quizzes:");
        for (Quiz q : quizzes) {
            if (q.getDifficultyLevel().equals(level)) {
                available.add(q);
                System.out.println("  " + available.size() + ". " + q.getTopic()
                        + " (" + q.getTotalQuestions() + " items, " + q.getTimeLimit() + " seconds)");
            }
        }
 
        if (available.isEmpty()) {
            System.out.println("  No quizzes available for this level yet.");
            return null;
        }
 
        int pick = readNumber(input, "Choose a topic number: ", 1, available.size());
        return available.get(pick - 1);
    }
 
    public void takeQuiz(Quiz quiz, Scanner input, DataSave data) {
        System.out.println("\n=== " + quiz.getTopic() + " (" + quiz.getDifficultyLevel() + ") ===");
        System.out.println("Time limit: " + quiz.getTimeLimit() + " seconds");
        System.out.println("Type the number of your answer.");
 
        int total = quiz.getTotalQuestions();
        int[] answers = new int[total];   
 
        TimeQuiz timer = new TimeQuiz(quiz.getTimeLimit());
        timer.startTimer();
 
        for (int i = 0; i < total; i++) {
            quiz.displayQuestion(i);
            answers[i] = readNumber(input, "Your answer (1-4): ", 1, 4);
 
            if (quiz.checkAnswer(i, answers[i])) {
                System.out.println("Correct!");
            } else {
                System.out.println("Wrong. The correct answer is: " + quiz.getCorrectAnswerText(i));
            }
 
            if (timer.checkTimeLimit()) {
                System.out.println("\nTime is up! The quiz has ended.");
                break;
            }
        }
        timer.stopTimer();
 
        int score = quiz.computeScore(answers);
 
        
        int attemptNumber = 1;
        for (Result r : attemptHistory) {
            if (r.getQuizId() == quiz.getQuizId()) {
                attemptNumber++;
            }
        }
 
        int newId = data.getResults().size() + 1;
        Result result = new Result(newId, studentId, quiz.getQuizId(), quiz.getTopic(),
                quiz.getDifficultyLevel(), score, total, attemptNumber, LocalDate.now().toString());
 
        attemptHistory.add(result);
        data.addResult(result);
        data.saveData();
 
        System.out.println("\n--- Quiz Finished ---");
        System.out.println("Score: " + score + "/" + total
                + " (" + String.format("%.0f", result.getPercentage()) + "%)");
        if (result.getPercentage() >= 80) {
            System.out.println("Feedback: Excellent work!");
        } else if (result.getPercentage() >= 60) {
            System.out.println("Feedback: Good job, keep practicing.");
        } else {
            System.out.println("Feedback: You need more practice on " + quiz.getTopic() + ".");
        }
 
        
        if (attemptNumber > 1) {
            for (int i = attemptHistory.size() - 2; i >= 0; i--) {
                Result old = attemptHistory.get(i);
                if (old.getQuizId() == quiz.getQuizId()) {
                    System.out.println("Compared to your last attempt: " + result.compareAttempts(old));
                    break;
                }
            }
        }
    }
 
    public void retakeQuiz(Quiz quiz, Scanner input, DataSave data) {
        System.out.println("\nRetaking the quiz...");
        takeQuiz(quiz, input, data);
    }
 
    public void viewResults() {
        if (attemptHistory.isEmpty()) {
            System.out.println("\nYou have no quiz results yet.");
            return;
        }
        Result last = attemptHistory.get(attemptHistory.size() - 1);
        System.out.println("\nYour latest result:");
        System.out.println("  " + last.getTopic() + " (" + last.getDifficulty() + ") - "
                + last.getScore() + "/" + last.getTotalItems()
                + " (" + String.format("%.0f", last.getPercentage()) + "%)");
    }
 
    public void viewAttemptHistory() {
        if (attemptHistory.isEmpty()) {
            System.out.println("\nYou have no attempts yet.");
            return;
        }
        System.out.println("\n--- Attempt History ---");
        for (Result r : attemptHistory) {
            System.out.println(r.getDateTaken() + " | " + r.getTopic() + " (" + r.getDifficulty()
                    + ") | Attempt #" + r.getAttemptNumber() + " | "
                    + r.getScore() + "/" + r.getTotalItems()
                    + " (" + String.format("%.0f", r.getPercentage()) + "%)");
        }
    }
 
    public void getStudyRecommendation() {
        System.out.println("\n--- Study Recommendation ---");
        if (attemptHistory.isEmpty()) {
            System.out.println("Take a quiz first so we can give you suggestions.");
            return;
        }
        boolean foundWeak = false;
        for (Result r : attemptHistory) {
            String weak = r.getWeakTopics();
            if (!weak.equals("None")) {
                System.out.println("- Review \"" + weak + "\" and try the " + r.getDifficulty()
                        + " quiz again.");
                foundWeak = true;
            }
        }
        if (!foundWeak) {
            System.out.println("You are doing great! Try a higher difficulty level.");
        }
    }
 
    @Override
    public void displayMenu(Scanner input, ArrayList<Quiz> quizzes, DataSave data) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== STUDENT MENU =====");
            System.out.println("Name: " + getName() + " | Section: " + section);
            System.out.println("1. Take a quiz");
            System.out.println("2. View latest result");
            System.out.println("3. View attempt history");
            System.out.println("4. Study recommendation");
            System.out.println("5. Logout");
            int choice = readNumber(input, "Enter choice: ", 1, 5);
 
            switch (choice) {
                case 1 -> {
                    Quiz quiz = selectDifficulty(input, quizzes);
                    if (quiz != null) {
                        takeQuiz(quiz, input, data);
                        int again = readNumber(input, "\nRetake this quiz? (1 = Yes, 2 = No): ", 1, 2);
                        while (again == 1) {
                            retakeQuiz(quiz, input, data);
                            again = readNumber(input, "\nRetake this quiz? (1 = Yes, 2 = No): ", 1, 2);
                        }
                    }
                }
                case 2 -> viewResults();
                case 3 -> viewAttemptHistory();
                case 4 -> getStudyRecommendation();
                default ->
                    running = false;
            }
        }
    }
}