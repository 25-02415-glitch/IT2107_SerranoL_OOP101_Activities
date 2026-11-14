import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


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
 
    
    public void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Result r : results) {
                writer.println(r.toFileLine());
            }
        } catch (IOException e) {
            System.out.println("Error: could not save data.");
        }
    }
 
   
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
 
    
    public void updateData(Result newResult) {
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getResultId() == newResult.getResultId()) {
                results.set(i, newResult);
            }
        }
        saveData();
    }
 
    
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
