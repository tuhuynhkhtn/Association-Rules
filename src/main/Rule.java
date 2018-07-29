package main;

import model.Attribute;

import java.util.ArrayList;
import java.util.List;

public class Rule {

    private List<Attribute> clause;

    private List<Attribute> result;

    private double minConf = 0;

    public Rule() {
        minConf = 0;
        clause = new ArrayList<>();
        result = new ArrayList<>();
    }

    public List<Attribute> getClause() {
        return clause;
    }

    public void setClause(List<Attribute> clause) {
        this.clause = clause;
    }

    public List<Attribute> getResult() {
        return result;
    }

    public void setResult(List<Attribute> result) {
        this.result = result;
    }

    public double getMinConf() {
        return minConf;
    }

    public void setMinConf(double minConf) {
        this.minConf = minConf;
    }

    public List<Attribute> getAllAttributes() {
        List<Attribute> attributes = new ArrayList<>();
        attributes.addAll(clause);
        attributes.addAll(result);
        return attributes;
    }

    @Override
    public String toString() {
        String rules = "";
        for (Attribute attribute : clause) {
            rules += attribute.getmName() + " ";
        }
        rules += "==> ";
        for (Attribute attribute : result) {
            rules += attribute.getmName() + " ";
        }

        rules += minConf + "\n";
        return rules;
    }
}
