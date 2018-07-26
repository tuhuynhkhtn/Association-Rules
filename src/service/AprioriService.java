package service;

import model.Attribute;

import java.util.*;

public class AprioriService {

    private List<Attribute> attributes;
    private float minSupp;
    private Map<List<Attribute>, Float> transactionMap;
    private Map<Integer, List<Attribute>> rootDBTransaction;

    public AprioriService(List<Attribute> attributes, float minSupp) {
        this.attributes = attributes;
        this.minSupp = minSupp;
        buildRootDBTransaction();
    }

    public void execute() {
        initCandidate();
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
            rootDBTransaction.put(i, rowTrans);
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
            float minSuppAtrribute = sum / data.size();
            List<Attribute> attributeList = new ArrayList<>();
            attributeList.add(attribute);
            transactionMap.put(attributeList, minSuppAtrribute);
        }

        transactionMap.entrySet().removeIf(e -> e.getValue() < minSupp);
    }

    private List<Attribute> mergeTrans(List<Attribute> firstList, List<Attribute> secondList, int step) {
        List<Attribute> result = new ArrayList<>();
        result.addAll(firstList);
        secondList = secondList.subList(step - 1, secondList.size());
        result.addAll(secondList);
        return result;
    }

    private void calculateMinSupp(Map<List<Attribute>, Float> root) {
        Set<List<Attribute>> keySet = root.keySet();
        Map<List<Attribute>, Float> newMap = new HashMap<>();

        for (List<Attribute> keyList : keySet) {
            for (Map.Entry<Integer, List<Attribute>> entry : rootDBTransaction.entrySet()) {
                List<Attribute> entryValue = entry.getValue();
                int count = 0;
                if (keyList.containsAll(entryValue) || entryValue.containsAll(keyList)) {
                    count++;
                }
                float minSupp = count / rootDBTransaction.size();
                newMap.put(keyList, minSupp);
            }
        }

        root = newMap;
    }

    private Map<List<Attribute>, Float> candidateGenerator(int step) {
        Map<List<Attribute>, Float> resultList = new HashMap<>();
        List<List<Attribute>> keyList = new ArrayList<>(transactionMap.keySet());
        for (int i = 0; i < keyList.size() - 1; i++) {
            for (int j = i + 1; j < keyList.size(); j++) {
                List<Attribute> keyFirstTrans = keyList.get(i);
                List<Attribute> keySecondTrans = keyList.get(j);
                List<Attribute> newTrans;

                boolean isSimilar = true;
                for (int k = 0; k < step - 1; k++) {
                    if (!keyFirstTrans.get(k).getmName().equals(keySecondTrans.get(k).getmName())) {
                        isSimilar = false;
                        break;
                    }
                }
                if (!isSimilar) {
                    continue;
                }
                newTrans = mergeTrans(keyFirstTrans, keySecondTrans, step);
                /* không cần phải check item bị duplicate do cơ chế hashmap, key không thể giống nhau,
                    nên nếu có 2 key bị trùng thì sẽ xem như 1 */
                resultList.put(newTrans, (float) 0.0);
            }
        }
        return resultList;
    }

}
