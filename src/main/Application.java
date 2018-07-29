package main;

import model.Attribute;
import service.Process;
import service.AprioriService;
import service.ReadFileService;
import service.WriteFileService;

import java.io.IOException;
import java.util.*;

public class Application {

    @SuppressWarnings("resource")
	public static void main(String[] args) {
        ReadFileService fileService = new ReadFileService();
        try {
            List<Attribute> attributes = fileService.readFile("example.csv");
			System.out.println("Input minsup: ");
			Float minsup = new Scanner(System.in).nextFloat();
			
			List<String> list_exam = Process.FrequentItemSets(attributes, minsup);
			
			if(WriteFileService.FI(list_exam, "FI.txt"))
				System.out.println("Output file text successfully.");
			else System.out.println("Output to file failed.");

            AprioriService aprioriService = new AprioriService(attributes, 0.4, 0.9);
            aprioriService.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
