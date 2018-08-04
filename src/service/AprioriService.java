package service;

import main.Rule;
import model.Attribute;

import java.util.*;

public class AprioriService {

    private List<Attribute> attributes;
    private String fileNameOutFI;
    private String fileNameOutAR;
    private double minSupp;
    private double minConf;
    private Map<List<Attribute>, Double> transactionMap;
    private Map<Integer, List<Attribute>> rootDBTransaction;
    private boolean isEnd = false;
    private int size = 0;

    public AprioriService(List<Attribute> attributes, String fileNameOutFI, String fileNameOutAR, String minSupp, String minConf) {
        this.attributes = attributes;
        this.fileNameOutFI = fileNameOutFI;
        this.fileNameOutAR = fileNameOutAR;
        this.minSupp = Double.parseDouble(minSupp);
        this.minConf = Double.parseDouble(minConf);
        buildRootDBTransaction();

    }

    public void execute() {
        initCandidate();
        int step = 2;
        List<Map<List<Attribute>, Double>> resultF = new ArrayList<>();
        while (!isEnd && transactionMap != null) {
            transactionMap = candidateGenerator(step);
            transactionMap = calculateMinSupp(transactionMap);
            if (transactionMap != null && !transactionMap.isEmpty())
                resultF.add(transactionMap);
            step++;
        }
        System.out.println("Hoàn thành tạo các mẫu phổ biến");
        System.out.println("================================");

        Set<Rule> associationRules = new HashSet<>();
        for (Map<List<Attribute>, Double> f : resultF) {

            for (Map.Entry<List<Attribute>, Double> entry : f.entrySet()) {
                if (entry.getKey().size() == 2) continue;
                List<Rule> result = createAssociationRule(entry);
                if (!result.isEmpty()) {
                    associationRules.addAll(result);
                }
            }
        }
        System.out.println("Hoàn thành quá trình tạo luật phù hợp");
        WriteFileService fileService = new WriteFileService();
        System.out.println("Dang ghi file FI.txt...");
        fileService.writeItemSetToFile(resultF, fileNameOutFI);
        waitMillisecond(600);
        System.out.println("Hoàn tất ghi file " + fileNameOutFI);
        System.out.println("Dang ghi file AR.txt...");
        fileService.writeRulesToFile(associationRules,fileNameOutAR);
        waitMillisecond(600);
        System.out.println("Hoàn tất ghi file " + fileNameOutAR);
    }

    public void waitMillisecond(long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void buildRootDBTransaction() {
        int itemCount = attributes.get(0).getmData().size();
        rootDBTransaction = new HashMap<>();
        for (int i = 0; i < itemCount; i++) {
            List<Attribute> rowTrans = new ArrayList<>();
            for (int j = 0; j < attributes.size(); j++) {
                if (attributes.get(j).getmData().get(i) == 1) {
                    rowTrans.add(attributes.get(j));
                }
            }
            if (!rowTrans.isEmpty()) {
                rootDBTransaction.put(i, rowTrans);
            }
        }
    }

    private void initCandidate() {
        System.out.println("Khởi tạo cơ sở dữ liệu bảng");
        transactionMap = new HashMap<>();
        for (Attribute attribute : attributes) {
            int sum = 0;
            List<Integer> data = attribute.getmData();
            for (int i = 0; i < data.size(); i++) {
                sum += data.get(i);
            }
            double minSuppAtrribute =  (sum / (data.size() * 1.0));
            List<Attribute> attributeList = new ArrayList<>();
            attributeList.add(attribute);
            transactionMap.put(attributeList, minSuppAtrribute);
        }

        transactionMap.entrySet().removeIf(e -> e.getValue() < minSupp);
        size = transactionMap.size();
    }

    private List<Attribute> mergeTrans(List<Attribute> firstList, List<Attribute> secondList, int step) {
        Set<Attribute> result = new HashSet<>();
        result.addAll(firstList);
        result.addAll(secondList);
        List<Attribute> mergedList = new ArrayList<>(result);
        Collections.sort(mergedList, new Comparator<Attribute>() {
            @Override
            public int compare(Attribute o1, Attribute o2) {
                return o1.getmIndex() > o2.getmIndex() ? 1 : -1;
            }
        });
        return new ArrayList<>(mergedList);
    }

    private Map<List<Attribute>, Double> calculateMinSupp(Map<List<Attribute>, Double> root) {
        System.out.println("Loai bo ung vien khong phu hop voi minSupp");
        Set<List<Attribute>> keySet = root.keySet();
        Map<List<Attribute>, Double> newMap = new HashMap<>();

        for (List<Attribute> keyList : keySet) {
            int count = 0;
            for (Map.Entry<Integer, List<Attribute>> entry : rootDBTransaction.entrySet()) {

                List<Attribute> entryValue = entry.getValue();

                if (entryValue.containsAll(keyList)) {
                    count++;
                }
            }
            double minSuppTrans = count / (size * 1.0);
            if (minSuppTrans >= minSupp) {
                newMap.put(keyList, minSuppTrans);
            }
        }
        if (newMap.isEmpty()) {
            isEnd = true;
            return null;
        }
        return newMap;
    }

    private Map<List<Attribute>, Double> candidateGenerator(int step) {
        System.out.println("Khởi tạo các mẫu ứng viên C" + step);
        Map<List<Attribute>, Double> resultList = new HashMap<>();
        List<List<Attribute>> keyList = new ArrayList<>(transactionMap.keySet());
        for (int i = 0; i < keyList.size() - 1; i++) {
            for (int j = i + 1; j < keyList.size(); j++) {
                List<Attribute> keyFirstTrans = keyList.get(i);
                List<Attribute> keySecondTrans = keyList.get(j);
                List<Attribute> newTrans;

                boolean isSimilar = true;
                if (step != 2) {
                    for (int k = 0; k < step - 2; k++) {
                        if (!keyFirstTrans.get(k).getmName().equals(keySecondTrans.get(k).getmName())) {
                            isSimilar = false;
                            break;
                        }
                    }
                }
                if (!isSimilar) {
                    continue;
                }
                newTrans = mergeTrans(keyFirstTrans, keySecondTrans, step);
                /* không cần phải check item bị duplicate do cơ chế hashmap, key không thể giống nhau,
                    nên nếu có 2 key bị trùng thì sẽ xem như 1 */
                resultList.put(newTrans, 0.0);
            }
        }
        return resultList;
    }

    private List<Rule> createAssociationRule(Map.Entry<List<Attribute>, Double> entry) {
        List<Attribute> key = entry.getKey();
        List<Rule> rules = new ArrayList<>();
        System.out.println("Khởi tạo các luật có thể xảy ra");
        for (int i = 0; i < key.size(); i++) {
            Rule rule = new Rule();
            rule.getResult().add(key.get(i));
            int addedIndexAttribute = key.get(i).getmIndex();
            for (int j = 0; j < key.size(); j++) {
                if (key.get(j).getmIndex() != addedIndexAttribute) {
                    rule.getClause().add(key.get(j));
                }
            }
            rules.add(rule);
        }

        int level = 2;
        List<Rule> previousResult = new ArrayList<>();
        while (true) {
            System.out.println("Tính toán minconf cho luật");
            rules = calculateMinConf(rules);
            System.out.println("Loai bo các luật không phù hợp với minConf");
            rules = compactRules(rules);
            if (level == 2 && !rules.isEmpty()) {
                previousResult.addAll(rules);
            }
            if (!rules.isEmpty()) {
                System.out.println("Phát sinh các luật mới từ các luật phù hợp trước đó");
                rules = createRuleByLevel(rules, 2);
                level++;
            } else {
                break;
            }

            if (!rules.isEmpty()) {
                previousResult.addAll(rules);
            } else {
                break;
            }
        }
        return previousResult;
    }

    private List<Rule> createRuleByLevel(List<Rule> rules, int level) {
        List<Rule> newRules = new ArrayList<>();
        Set<Attribute> keys = new HashSet<>();
        for (Rule r : rules) {
            keys.addAll(r.getAllAttributes());
        }

        for (int i = 0; i < rules.size() - 1; i++) {
            for (int j = i + 1; j < rules.size(); j++) {
                List<Attribute> firstList = rules.get(i).getResult();
                List<Attribute> secondList = rules.get(j).getResult();
                boolean isSimilar = true;
                for (int k = 0; k < level - 1; k++) {
                    if ((firstList.get(k).getmIndex() != secondList.get(k).getmIndex())
                            && firstList.size() > 1 && secondList.size() > 1) {
                        isSimilar = false;
                        break;
                    }
                }
                if (!isSimilar) continue;

                Set<Attribute> attributeSet = new HashSet<>();
                attributeSet.addAll(firstList);
                attributeSet.addAll(secondList);

                Rule rule = new Rule();
                List<Attribute> attrKey = new ArrayList<>(keys);
                List<Attribute> setList = new ArrayList<>(attributeSet);

                rule.getResult().addAll(setList);
                attrKey.removeAll(setList);
                if (attrKey.isEmpty()) continue;
                rule.getClause().addAll(attrKey);
                newRules.add(rule);
            }
        }
        return newRules;
    }

    private List<Rule> buildAssociationRule(Map<List<Attribute>, Double> itemSet) {
        Map<List<Attribute>, Double> ruleList = new HashMap<>();

        // initial rules
        List<Rule> rules = new ArrayList<>();
        for (Map.Entry<List<Attribute>, Double> entryItemSet : itemSet.entrySet()) {
            List<Attribute> key = entryItemSet.getKey();
            List<Attribute> dupKey = key;
            int end = 0;
            while (end < key.size()) {
                Attribute attribute = key.get(end);
                int posKey = key.indexOf(attribute);
                dupKey = key.subList(posKey + 1, key.size());
                Rule rule = new Rule();
                rule.getResult().add(attribute);
                rule.getClause().addAll(dupKey);
                rules.add(rule);
                end += key.size();
            }
        }

        List<Rule> result = new ArrayList<>();

        rules = calculateMinConf(rules);
        rules = compactRules(rules);
        if (!rules.isEmpty()) {
            result.addAll(rules);
        }

        return result;
    }

    private List<Rule> calculateMinConf(List<Rule> root) {
        for (Rule rule : root) {
            int numAllAttributesAppeared = 0;
            int numClauseAppeared = 0;
            for (Map.Entry<Integer, List<Attribute>> entry : rootDBTransaction.entrySet()) {
                List<Attribute> entryValue = entry.getValue();
                if (entryValue.containsAll(rule.getAllAttributes())) {
                    numAllAttributesAppeared++;
                }
                if (entryValue.containsAll(rule.getClause())) {
                    numClauseAppeared++;
                }
            }

            double minConf = (numAllAttributesAppeared * 1.0) / numClauseAppeared;
            rule.setMinConf(minConf);
        }
        return root;
    }

    private List<Rule> compactRules(List<Rule> root) {
        List<Rule> newRules = new ArrayList<>();
        for (Rule rule : root) {
            if (rule.getMinConf() >= minConf) {
                newRules.add(rule);
            }
        }
        return newRules;
    }



}
