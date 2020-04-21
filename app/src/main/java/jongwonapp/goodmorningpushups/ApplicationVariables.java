package jongwonapp.goodmorningpushups;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-08-27.
 */

public class ApplicationVariables extends Application {
    public List<Routine> routines;
    public int today;

    public ApplicationVariables() {
        routines=new ArrayList<Routine>();
        today=0;
    }

    public void setToday(int today) {
        this.today = today;
    }
}
