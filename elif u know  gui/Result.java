// Saves the score of one quiz attempt
public class Result {
    private final int resultId;
    private final String studentId;
    private final int quizId;
    private final String topic;
    private final String difficulty;
    private final int score;
    private final int totalItems;
    private final int attemptNumber;
    private final String dateTaken;
 
    public Result(int resultId, String studentId, int quizId, String topic, String difficulty,
                  int score, int totalItems, int attemptNumber, String dateTaken) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.quizId = quizId;
        this.topic = topic;
        this.difficulty = difficulty;
        this.score = score;
        this.totalItems = totalItems;
        this.attemptNumber = attemptNumber;
        this.dateTaken = dateTaken;
    }
 
    public int getResultId() { return resultId; }
    public String getStudentId() { return studentId; }
    public int getQuizId() { return quizId; }
    public String getTopic() { return topic; }
    public String getDifficulty() { return difficulty; }
    public int getScore() { return score; }
    public int getTotalItems() { return totalItems; }
    public int getAttemptNumber() { return attemptNumber; }
    public String getDateTaken() { return dateTaken; }
 
    public double getPercentage() {
        if (totalItems == 0) {
            return 0;
        }
        return (score * 100.0) / totalItems;
    }
 
    // A topic is "weak" if the percentage is below 60
    public String getWeakTopics() {
        if (getPercentage() < 60) {
            return topic;
        }
        return "None";
    }
 
    // Compares this attempt with an older attempt
    public String compareAttempts(Result older) {
        double difference = getPercentage() - older.getPercentage();
        if (difference > 0) {
            return "Improved by " + String.format("%.0f", difference) + "%";
        } else if (difference < 0) {
            return "Dropped by " + String.format("%.0f", -difference) + "%";
        }
        return "Same score as before";
    }
 
    // Converts the result to one line of text for saving in a file
    public String toFileLine() {
        return resultId + "|" + studentId + "|" + quizId + "|" + topic + "|" + difficulty + "|"
                + score + "|" + totalItems + "|" + attemptNumber + "|" + dateTaken;
    }
 
    // Converts one line of text from the file back to a Result object
    public static Result fromFileLine(String line) {
        String[] p = line.split("\\|");
        return new Result(Integer.parseInt(p[0]), p[1], Integer.parseInt(p[2]), p[3], p[4],
                Integer.parseInt(p[5]), Integer.parseInt(p[6]), Integer.parseInt(p[7]), p[8]);
    }
}
