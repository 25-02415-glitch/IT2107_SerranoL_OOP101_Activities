import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
 
// Saves and loads the quiz results using a text file
public class DataSave {
    private final String fileName;
    private final ArrayList<Result> results = new ArrayList<>();
 
    public DataSave(String fileName) {
        this.fileName = fileName;
    }
 
    public ArrayList<Result> getResults() {
        return results;
    }
 
    public void addResult(Result result) {
        results.add(result);
    }
 
    // Writes all results to the file
    public void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Result r : results) {
                writer.println(r.toFileLine());
            }
        } catch (IOException e) {
            System.out.println("Error: could not save data.");
        }
    }
 
    // Reads all results from the file (if the file exists)
    public void loadData() {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    results.add(Result.fromFileLine(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error: could not load data.");
        } catch (RuntimeException e) {
            System.out.println("Error: the data file has a wrong format.");
        }
    }
 
    // Replaces a result that has the same resultId, then saves
    public void updateData(Result newResult) {
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getResultId() == newResult.getResultId()) {
                results.set(i, newResult);
            }
        }
        saveData();
    }
 
    // Removes a result by its resultId, then saves
    public void deleteData(int resultId) {
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getResultId() == resultId) {
                results.remove(i);
                break;
            }
        }
        saveData();
    }

    public String getFileName() {
        return fileName;
    }
}
