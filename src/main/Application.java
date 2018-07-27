package main;

import model.Attribute;
import service.AprioriService;
import service.ReadFileService;

import java.io.IOException;
import java.util.*;

public class Application {

    public static void main(String[] args) {
        //TODO lấy các input từ tham số dòng lệnh ở đây để làm câu 2 và câu 3
        ReadFileService fileService = new ReadFileService();
        try {
            List<Attribute> attributes = fileService.readFile("example.csv");

            AprioriService aprioriService = new AprioriService(attributes, 0.4);
            aprioriService.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
