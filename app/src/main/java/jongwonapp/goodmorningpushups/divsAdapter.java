package jongwonapp.goodmorningpushups;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018-08-30.
 */

public class divsAdapter extends RecyclerView.Adapter<divsViewHolder> implements divsViewHolder.OnItemButtonClickListener{
    //workOut wo;
    List<Integer> time;
    private final Context con;
    ApplicationVariables av;
    int editnum;

    public divsAdapter(Context con, ApplicationVariables av) {
        this.con = con;
        this.av=av;
        editnum=0;
        time = new ArrayList<Integer>();
    }

    public void setWorkOut(workOut wo){
        time.clear();
        for(int i=0;i<wo.getTimeInfo().size();i++)
            time.add(wo.getTimeInfo().get(i));
        notifyDataSetChanged();
    }
    public List<Integer> getTime(){
        return time;
    }

    @Override
    public divsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_divs,parent,false);
        final divsViewHolder holder = new divsViewHolder(v);
        holder.setOnItemButtonClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(divsViewHolder holder, int position) {
        if(time.size()>0)
        {
            if(position == time.size()-3)
            {
                holder.text_div.setText("휴식");
                holder.setSec(time.get(2));
            }else
            {
                holder.text_div.setText("구분동작 "+(position+1));
                holder.setSec(time.get(position+3));
            }
        }
    }

    public void add()
    {
        time.add(10);
        notifyDataSetChanged();
    }
    public void del()
    {
        time.remove(time.size()-1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return time.size()-2;
    }


    @Override
    public void onItemButtonClick(int position, float sec) {
        if(position==this.getItemCount()-1) time.set(2,Math.round(sec));
        else time.set(position+3,Math.round(sec));
        ((settingActivity)con).setTimeused();
    }
}
