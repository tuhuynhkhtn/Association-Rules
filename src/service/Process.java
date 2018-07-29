package service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import model.Attribute;

public class Process {
	static List<Integer> key =new ArrayList<>();
	
	public static List<String> FrequentItemSets(List<Attribute> list, Float minsup) {
		List<String> list_thuc_the = listThucThe(list);
		
		int count = 0;
		boolean check = true;
		List<String> list_match = new ArrayList<>(); 
		for (String string : list_thuc_the) {
			for (int i = 0; i < list.get(0).getmData().size(); i++) {
				for (String str : string.split(" ")) {
					for (int j = 0; j < list.size(); j++)
						if(Integer.valueOf(str) == list.get(j).getmIndex() && list.get(j).getmData().get(i) == 0)
							check = false;
				}
				
				if(check == true) count++;
				else check = true;
			}
			
			if (count > 0 && (float)count/list.get(0).getmData().size() >= minsup) {
				list_match.add((new DecimalFormat("0.00").format((float)count/list.get(0).getmData().size())) + " " + string);
				count = 0;
			}
		}
		
		return list_match;
	}
	
	static List<String> listThucThe(List<Attribute> list){
		List<String> list_key = new ArrayList<>();
		
		for (int i = 1; i < list.size(); i++) {
			for (int j = 0; j < toHop(list.size(), i); j++) {
				key = play(list, i, -1, key,list_key);
				String str = convert(key);
				if(!str.equals(""))
					list_key.add(str);
				else j--;
				key = new ArrayList<Integer>();
			}
		}
		return list_key;
	}
	
	static Integer toHop(Integer n, Integer k) {
		return giaiThua(n, 1) / (giaiThua(n - k, 1) * giaiThua(k, 1));
	}
	
	static Integer giaiThua(Integer n, Integer i) {
		if(i==n) return i;
		return i * giaiThua(n, i+1);
	}
	
	public static List<Integer> play(List<Attribute> arr, Integer lenth,Integer elem_current, List<Integer> temp, List<String> list_key) {
		
		for (int i = elem_current + 1; i < arr.size(); i++) {
			temp.add(arr.get(i).getmIndex());
			if(temp.size() < lenth) {
				temp = play(arr, lenth, i, temp, list_key); 
				if(temp == null) return null;
				else if(temp.size() == lenth) return temp;
				else if(temp.size() < lenth) temp.remove(temp.size() -1);
			}
			else if(temp.size() == lenth && !searchInListTK(list_key,convert(temp), lenth)) return temp;
			else if(temp.size() == lenth) temp.remove(temp.size() -1);
		}
		
		return temp;
	}
	
	static boolean searchInListTK(List<String> list_key, String str,Integer lenth) {
		for (String string : list_key) {
			if(string.equalsIgnoreCase(str) && lenth == string.split(" ").length)
				return true;
		}
		return false;
	}
	
	static String convert(List<Integer> list) {
		String str = "";
		if(list == null) return str;
		if(list.size()==0) return str;
		
		str=list.get(0).toString();
		for (int i = 1; i < list.size(); i++) {
			str += " " + list.get(i).toString();
		}
		
		return str;
	}
}
