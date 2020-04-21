package jongwonapp.goodmorningpushups;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-08-27.
 */

public class Routine {
    String name;
    List<workOut> workout;
    int position;

    public Routine(String name) {
        workout = new ArrayList<workOut>();
        this.name = name;
        position = 0;
    }

    public void reset() {
        position = 0;
    }

    public boolean isEnd() {
        if (workout.size() > position + 1)
            return false;
        else {
            return true;
        }
    }
    public workOut getNext()
    {
        if(!isEnd())
        {
            return workout.get(position+1);
        }else
        {
            return workout.get(0);
        }
    }
    public void next()
    {
        if (workout.size() > position + 1)
            position++;
        else {
            position=0;
        }
    }
    public workOut getNow()
    {
        return workout.get(position);
    }

    public void setWorkout(List<workOut> workout){
        this.workout=workout;
    }
}
