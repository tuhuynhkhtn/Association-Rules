package model;

import java.util.ArrayList;
import java.util.List;

public class Attribute {

    private int mIndex;

    private String mName;

    private List<Integer> mData;

    public Attribute() {
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public List<Integer> getmData() {
        return mData;
    }

    public void setmData(List<Integer> mData) {
        this.mData = mData;
    }

    public int getmIndex() {
        return mIndex;
    }

    public void setmIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    public String toString() {
        String str = mIndex + ". " + mName + ": ";
        for (Integer value : mData) {
            str += " " + value;
        }
        return str;
    }
}
