public class TimeQuiz {
    private final int timeLimit; 
    private int remainingTime;   
    private long startTime;      
    private boolean timeUp;
    private boolean timerRunning;
 
    public TimeQuiz(int timeLimit) {
        this.timeLimit = timeLimit;
        this.remainingTime = timeLimit;
        this.timeUp = false;
    }
 
    public void startTimer() {
        startTime = System.currentTimeMillis();
        timerRunning = true;
        timeUp = false;
    }
 
    public void stopTimer() {
        remainingTime = getRemainingTime();
        timerRunning = false;
    }
 
    public int getRemainingTime() {
        if (!timerRunning) {
            return remainingTime;
        }
        long secondsPassed = (System.currentTimeMillis() - startTime) / 1000;
        int left = remainingTime - (int) secondsPassed;
        if (left < 0) {
            left = 0;
        }
        return left;
    }
 
    
    public boolean checkTimeLimit() {
        if (getRemainingTime() <= 0) {
            timeUp = true;
        }
        return timeUp;
    }
 
    public void resetTimer() {
        remainingTime = timeLimit;
        timerRunning = false;
        timeUp = false;
    }
}
