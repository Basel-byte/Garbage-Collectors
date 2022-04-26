package model;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FileUtil {

    public List<List<String>> readFromCSVFile(String filePath) {
        List<List<String>> records = new ArrayList<>();
        try(CSVReader csvReader = new CSVReader(new FileReader(filePath))){
            String[] values;
            while ((values = csvReader.readNext()) != null) {
                records.add(Arrays.asList(values));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return records;
    }

    public void writeInCSVFile(LinkedHashMap<Integer, Interval> map, String filePath) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(filePath));
        for (Map.Entry<Integer, Interval> entry : map.entrySet()) {
            writer.writeNext(new String[]{entry.getKey().toString(), String.valueOf(entry.getValue().getStart()), String.valueOf(entry.getValue().getEnd())});
        }
        writer.close();
    }

    public List<String> readFromTextFile(String filePath) {
        List<String> records = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = br.readLine()) != null) {
                records.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }
}