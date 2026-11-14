import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DataSave data = new DataSave("results.txt");
        data.loadData();

        File userFile = new File("users.txt");
        if (!userFile.exists()) {
            DataSave.saveUser("users.txt",
                    new Student("U1", "Cueto, Kathleen Alexa A.", "cueto", "pass123", "25-00238", "2107"));
            DataSave.saveUser("users.txt",
                    new Student("U2", "Noche, Daniella D.", "noche", "pass123", "25-00958", "2107"));
            DataSave.saveUser("users.txt",
                    new Student("U3", "Serrano, Lovely P.", "serrano", "pass123", "25-02415", "2107"));
            DataSave.saveUser("users.txt", new Instructor("U4", "Ms. Fatima Marie P. Agdon", "Ms.agdon", "admin123",
                    "I-001", "Information Technology"));
        }

        ArrayList<User> users = DataSave.loadUsers("users.txt");
        ArrayList<Student> students = new ArrayList<>();
        Instructor teacher = null;

        for (User u : users) {
            if (u instanceof Student) {
                students.add((Student) u);
            } else if (u instanceof Instructor) {
                teacher = (Instructor) u;
            }
        }

        if (teacher != null) {
            for (Student s : students) {
                teacher.addStudent(s);
            }
        }

        for (Result r : data.getResults()) {
            for (Student s : students) {
                if (s.getStudentId().equals(r.getStudentId())) {
                    s.addToHistory(r);
                }
            }
        }

        ArrayList<Quiz> quizzes = createSampleQuizzes();

        if (args.length > 0 && args[0].equals("--console")) {
            runConsole(users, quizzes, data);
        } else {
            javax.swing.SwingUtilities.invokeLater(() -> new QuizApp(users, quizzes, data).setVisible(true));
        }
    }

    public static void runConsole(ArrayList<User> users, ArrayList<Quiz> quizzes, DataSave data) {
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("=====================================");
            System.out.println("   ELIF U KNOW - Python Quiz System  ");
            System.out.println("=====================================");

            boolean running = true;
            while (running) {
                System.out.println("\n1. Login");
                System.out.println("2. Exit");
                int choice = User.readNumber(input, "Enter choice: ", 1, 2);

                if (choice == 2) {
                    running = false;
                } else {
                    User loggedIn = loginScreen(input, users);
                    if (loggedIn != null) {
                        System.out.println("\nWelcome, " + loggedIn.getName() + " (" + loggedIn.getRole() + ")!");
                        loggedIn.displayMenu(input, quizzes, data);
                        loggedIn.logout();
                    }
                }
            }
            System.out.println("Thank you for using Elif U Know!");
        }
    }

    public static User loginScreen(Scanner input, ArrayList<User> users) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("\nUsername: ");
            String username = input.nextLine().trim();
            System.out.print("Password: ");
            String password = input.nextLine().trim();

            for (User u : users) {
                if (u.login(username, password)) {
                    return u;
                }
            }
            System.out.println("Wrong username or password. Attempts left: " + (3 - attempt));
        }
        System.out.println("Too many failed attempts.");
        return null;
    }

    public static ArrayList<Quiz> createSampleQuizzes() {
        ArrayList<Quiz> quizzes = new ArrayList<>();

        Quiz q1 = new Quiz(1, "Python Basics", "Quiz 1", 3);
        q1.addQuestion(new Question("Which is a valid Python variable name?",
                new String[] { "2name", "my_name", "my-name", "class" }, 2));
        q1.addQuestion(new Question("What is the data type of 5.0 in Python?",
                new String[] { "int", "float", "str", "bool" }, 2));
        q1.addQuestion(new Question("Which symbol starts a single-line comment in Python?",
                new String[] { "//", "#", "/*", "--" }, 2));
        quizzes.add(q1);

        Quiz q2 = new Quiz(2, "Python Control Flow", "Quiz 2", 3);
        q2.addQuestion(new Question("Which keyword means \"else if\" in Python?",
                new String[] { "elseif", "elif", "else if", "elsif" }, 2));
        q2.addQuestion(new Question("Which operator checks if two values are equal?",
                new String[] { "=", "==", "!=", "=>" }, 2));
        q2.addQuestion(new Question("x = 5. What does this print?  if x > 3: print(\"A\") else: print(\"B\")",
                new String[] { "A", "B", "Error", "Nothing" }, 1));
        quizzes.add(q2);

        Quiz q3 = new Quiz(3, "Python Lists", "Quiz 3", 3);
        q3.addQuestion(new Question("fruits = [\"a\", \"b\", \"c\"]. What is fruits[1]?",
                new String[] { "a", "b", "c", "Error" }, 2));
        q3.addQuestion(new Question("Which method adds an item at the end of a list?",
                new String[] { "add()", "append()", "insert()", "push()" }, 2));
        q3.addQuestion(new Question("What is len([1, 2, 3, 4])?",
                new String[] { "3", "4", "5", "Error" }, 2));
        quizzes.add(q3);

        Quiz q4 = new Quiz(4, "Python Functions", "Quiz 4", 3);
        q4.addQuestion(new Question("Which keyword is used to define a function?",
                new String[] { "function", "def", "fun", "define" }, 2));
        q4.addQuestion(new Question("def f(x): return x * 2.  What does print(f(f(3))) show?",
                new String[] { "6", "12", "9", "18" }, 2));
        q4.addQuestion(new Question("What does a function return if it has no return statement?",
                new String[] { "0", "An empty string", "None", "Error" }, 3));
        quizzes.add(q4);

        return quizzes;
    }
}