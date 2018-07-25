package main;

import model.Attribute;
import service.ReadFileService;

import java.io.IOException;
import java.util.List;

public class Application {

    public static void main(String[] args) {
        //TODO lấy các input từ tham số dòng lệnh ở đây để làm câu 2 và câu 3
        ReadFileService fileService = new ReadFileService();
        try {
            List<Attribute> attributes = fileService.readFile("example.csv");

            for (Attribute attribute : attributes) {
                System.out.println(attribute.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
