import java.util.ArrayList;
 
public class Quiz {
    private final int quizId;
    private final String topic;
    private final String difficultyLevel;   
    private final int timeLimit;            
    private final ArrayList<Question> questions = new ArrayList<>();
 
    public Quiz(int quizId, String topic, String difficultyLevel, int timeLimit) {
        this.quizId = quizId;
        this.topic = topic;
        this.difficultyLevel = difficultyLevel;
        this.timeLimit = timeLimit;
    }
 
    public void addQuestion(Question question) {
        questions.add(question);
    }
 
    public void displayQuestion(int index) {
        System.out.println("\nQuestion " + (index + 1) + " of " + questions.size());
        questions.get(index).display();
    }
 
    public boolean checkAnswer(int index, int answer) {
        return questions.get(index).isCorrect(answer);
    }
 
    
    public int computeScore(int[] answers) {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (checkAnswer(i, answers[i])) {
                score++;
            }
        }
        return score;
    }
 
    public String getCorrectAnswerText(int index) {
        return questions.get(index).getCorrectAnswerText();
    }
 
    public int getTotalQuestions() {
        return questions.size();
    }
 
    public int getQuizId() {
        return quizId;
    }
 
    public String getTopic() {
        return topic;
    }
 
    public String getDifficultyLevel() {
        return difficultyLevel;
    }
 
    public int getTimeLimit() {
        return timeLimit;
    }
 
    public String getDifficulty() {
        return difficultyLevel;
    }
 
    public Question getQuestion(int index) {
        return questions.get(index);
    }
}
