package jongwonapp.goodmorningpushups;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-08-23.
 */

public class workOut {
    String name;
    int set,count,rest;
    int[] partsInfo;
    public workOut(String name)
    {
        this.name =name;
        set=0;
        count=0;
        rest=0;
        partsInfo= new int[]{0};
    }
    public void setBaseInfo(int set, int count, int rest)
    {
        this.set=set;
        this.count=count;
        this.rest=rest;
    }
    public void setPartsInfo(int[] partsInfo)
    {
        this.partsInfo=partsInfo;
    }
    public List<Integer> getTimeInfo()
    {
        List<Integer> timeInfo = new ArrayList<Integer>();
        timeInfo.add(set);
        timeInfo.add(count);
        timeInfo.add(rest);
        for(int i=0;i<partsInfo.length;i++) timeInfo.add(partsInfo[i]);
        return timeInfo;
    }
    public int getTotalTime()
    {
        int aSet=0;
        for(int i=0;i<partsInfo.length;i++) aSet+=partsInfo[i];
        return (aSet * count+rest)*set-rest;

    }


}
