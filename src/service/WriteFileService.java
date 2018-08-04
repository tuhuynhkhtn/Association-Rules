package service;

import main.Rule;
import model.Attribute;

import java.io.BufferedWriter;
import java.io.File;
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
                    bufferedWriter.write(f.get(attributeList) + "\t");
                    for (Attribute attribute : attributeList) {
                        bufferedWriter.write(attribute.getmName().trim() + "\t");

                    }
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
    @SuppressWarnings("unused")
	private static Integer countNumMang(List<String> list, Integer num) {
		Integer i = 0;
		for (String string : list) {
			if(string.split(" ").length == num + 1) i++;
			else if(string.split(" ").length > num + 1) return i;
		}
		return i;
	}
	@SuppressWarnings({ "unused", "resource" })
	public static boolean FI(List<String> list,String str_file_name_out) {
		try {
			FileWriter fw = new FileWriter(new File(str_file_name_out));
			
			for (int i = 1; i < list.get(list.size() - 1).split(" ").length; i++) {
				Integer count = countNumMang(list, i);
					fw.write(Integer.toString(count)+"\n");
					for (String string : list) {
						if(string.split(" ").length == i+1) fw.write(string+"\n");
						else if(string.split(" ").length > i+1) break;
					}
			}
			
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
