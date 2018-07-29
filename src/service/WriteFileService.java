package service;

import main.Rule;
import model.Attribute;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WriteFileService {

    public void writeItemSetToFile(List<Map<List<Attribute>, Double>>  itemSet, String fileName) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            for (Map<List<Attribute>, Double> f : itemSet) {
                Set<List<Attribute>> fSet = f.keySet();

                bufferedWriter.write(fSet.size() + "");
                bufferedWriter.newLine();
                for (List<Attribute> attributeList : fSet) {
                    for (Attribute attribute : attributeList) {
                        bufferedWriter.write(attribute.getmName().trim() + "\t");

                    }
                    bufferedWriter.write(f.get(attributeList) + "");
                    bufferedWriter.newLine();
                }
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeRulesToFile(Set<Rule> ruleSet, String fileName) {

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            bufferedWriter.write(ruleSet.size() + "");
            bufferedWriter.newLine();
            for (Rule rule : ruleSet) {
                bufferedWriter.write(rule.toString());
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
