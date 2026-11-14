public class Question {
    private final String questionText;
    private final String[] choices;
    private final int correctAnswer;

    public Question(String questionText, String[] choices, int correctAnswer) {
        this.questionText = questionText;
        this.choices = choices;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getChoices() {
        return choices;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public String getCorrectAnswerText() {
        return choices[correctAnswer - 1];
    }

    public void display() {
        System.out.println(questionText);
        for (int i = 0; i < choices.length; i++) {
            System.out.println("  " + (i + 1) + ". " + choices[i]);
        }
    }

    public boolean isCorrect(int answer) {
        return answer == correctAnswer;
    }
}