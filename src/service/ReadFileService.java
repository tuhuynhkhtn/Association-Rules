package service;

import model.Attribute;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReadFileService {

    public List<Attribute> readFile(String fileName) throws IOException {
        List<Attribute> attributes = new ArrayList<>();
        File file = new File(fileName);
        if (!file.exists()) {
            return attributes;
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line  = reader.readLine();
        while (line != null) {
            if (attributes.isEmpty()) {
                String[] atrributeNames = line.split(",");
                for (int i = 0; i < atrributeNames.length; i++) {
                    Attribute attribute = new Attribute();
                    attribute.setmIndex(i);
                    attribute.setmName(atrributeNames[i]);
                    attribute.setmData(new ArrayList<>());
                    attributes.add(attribute);
                }
            } else {
                String[] data = line.split(",");
                for (int i = 0; i < data.length; i++) {
                    String dataCell = data[i];
                    int valueCell = dataCell.equals("y") ? 1 : 0;
                    Attribute attribute = attributes.get(i);
                    attribute.getmData().add(valueCell);
                }
            }

            line = reader.readLine();
        }
        return attributes;
    }

}
