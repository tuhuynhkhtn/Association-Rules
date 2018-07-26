package service;

import model.Attribute;

import java.util.*;

public class AprioriService {

    private List<Attribute> attributes;
    private double minSupp;
    private Map<List<Attribute>, Double> transactionMap;
    private Map<Integer, List<Attribute>> rootDBTransaction;
    private boolean isEnd = false;
    private int size = 0;

    public AprioriService(List<Attribute> attributes, double minSupp) {
        this.attributes = attributes;
        this.minSupp = minSupp;
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
        System.out.println("Luật kết hợp:");
        for (Map<List<Attribute>, Double> f : resultF) {
            Set<List<Attribute>> fSet = f.keySet();
            for (List<Attribute> attributeList : fSet) {
                for (Attribute attribute : attributeList) {
                    System.out.print(attribute.getmName() + "\t");
                }
                System.out.println();
            }
            System.out.println();
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
        Set<List<Attribute>> keySet = root.keySet();
        Map<List<Attribute>, Double> newMap = new HashMap<>();

        for (List<Attribute> keyList : keySet) {
            for (Map.Entry<Integer, List<Attribute>> entry : rootDBTransaction.entrySet()) {
                List<Attribute> entryValue = entry.getValue();
                int count = 0;
                if (entryValue.containsAll(keyList)) {
                    count++;
                }
                double minSuppTrans = count / (size * 1.0);
                if (minSuppTrans >= minSupp) {
                    newMap.put(keyList, minSuppTrans);
                }
            }
        }
        if (newMap.isEmpty()) {
            isEnd = true;
            return null;
        }
        return newMap;
    }

    private Map<List<Attribute>, Double> candidateGenerator(int step) {
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

}
