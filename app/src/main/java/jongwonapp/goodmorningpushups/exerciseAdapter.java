package jongwonapp.goodmorningpushups;

import android.app.Application;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018-08-26.
 */

public class exerciseAdapter extends RecyclerView.Adapter<exerciseViewHolder> implements exerciseViewHolder.OnListItemClickListener,ItemTouchHelperCallback.OnItemMoveListener {

    List<Routine> routines = new ArrayList<>();
    List<Boolean> clicked= new ArrayList<>();
    ApplicationVariables av;
    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    public exerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_exercise,parent,false);
        final exerciseViewHolder holder = new exerciseViewHolder(v);
        holder.setOnListItemClickListener(this);
        holder.butDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEventCompat.getActionMasked(event)==MotionEvent.ACTION_DOWN)
                    startDragListener.onStartDrag(holder);
                return false;
            }
        });
        return holder;
    }



    public void add(String data){

        routines.add(new Routine(data));
        for(int i=0;i<clicked.size();i++) clicked.set(i,false);
        clicked.add(false);
        notifyDataSetChanged();
    }

    public boolean delete()
    {
        for(int i=0;i<clicked.size();i++)
        {
            if(clicked.get(i))
            {
                routines.remove(i);
                clicked.set(i,false);
                clicked.remove(0);
                notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBindViewHolder(exerciseViewHolder holder, int position) {
        if(routines.size()>0)
        {
            String name = routines.get(position).name;
            holder.click(clicked.get(position));
            holder.textName.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    @Override
    public void onListItemClick(int position) {
        boolean temp=clicked.get(position);
        for(int i=0;i<clicked.size();i++) clicked.set(i,false);
        clicked.set(position,!temp);
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        notifyDataSetChanged();
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime && temp) {
            av.today=position;
            Log.i("!!","!");
            ((settingActivity)con).onBackPressed();
        }else {
            backPressedTime = tempTime;
        }
    }

    @Override
    public void onEditButtonClick(int position) {
        ((settingActivity)con).exercisePageUp(position);
    }

    public void setTodayFocused()
    {
        clicked.set(av.today,true);
        notifyDataSetChanged();
    }


    @Override
    public boolean OnItemMove(int fromPosition, int toPosition) {
        Collections.swap(routines,fromPosition,toPosition);
        Collections.swap(clicked,fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);
        return false;
    }
    public interface OnStartDragListener{
        void onStartDrag(exerciseViewHolder holder);
    }
    private final Context con;
    private final OnStartDragListener startDragListener;

    public exerciseAdapter(Context con, OnStartDragListener startDragListener,ApplicationVariables av) {
        this.con = con;
        this.startDragListener = startDragListener;
        routines = av.routines;
        this.av=av;
        clicked.clear();
        for(int i=0;i<routines.size();i++) clicked.add(false);
    }
}
