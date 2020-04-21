package jongwonapp.goodmorningpushups;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018-08-26.
 */

public class workoutAdapter extends RecyclerView.Adapter<exerciseViewHolder> implements exerciseViewHolder.OnListItemClickListener,ItemTouchHelperCallback.OnItemMoveListener {

    List<workOut> workout;
    List<Boolean> clicked= new ArrayList<>();
    ApplicationVariables av;
    int editnum;
    @Override
    public exerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_exercise,parent,false);
        final exerciseViewHolder holder = new exerciseViewHolder(v);
        holder.setOnListItemClickListener(this);
        holder.butDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEventCompat.getActionMasked(event)==MotionEvent.ACTION_DOWN)
                    startDragListener.onWorkoutStartDrag(holder);
                return false;
            }
        });
        holder.textName.setTypeface(Typeface.DEFAULT);
        return holder;
    }
/*
    public void setList(List<workOut> wo)
    {
        workout = wo;
        clicked.clear();
        for(int i=0;i<wo.size();i++) clicked.add(false);
        notifyDataSetChanged();
    }*/


    public void add(workOut wo){
        workout.add(wo);
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
                workout.remove(i);
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
        holder.click(clicked.get(position));
        workOut wo=workout.get(position);
        holder.textName.setText(wo.name);
    }

    @Override
    public int getItemCount() {
        return workout.size();
    }

    @Override
    public void onListItemClick(int position) {
        boolean temp=clicked.get(position);
        for(int i=0;i<clicked.size();i++) clicked.set(i,false);
        clicked.set(position,!temp);
        notifyDataSetChanged();
    }

    @Override
    public void onEditButtonClick(int position) {
        ((settingActivity)con).workoutPageUp(position);
    }

    public void setWorkout(int position)
    {
        workout=av.routines.get(position).workout;
        clicked.clear();
        for(int i=0;i<workout.size();i++) clicked.add(false);
        notifyDataSetChanged();
        editnum=position;
    }

    @Override
    public boolean OnItemMove(int fromPosition, int toPosition) {
        Collections.swap(workout,fromPosition,toPosition);
        Collections.swap(clicked,fromPosition,toPosition);
        notifyItemMoved(fromPosition,toPosition);
        return false;
    }
    public interface OnStartDragListener{
        void onWorkoutStartDrag(exerciseViewHolder holder);
    }

    private final Context con;
    private final OnStartDragListener startDragListener;

    public workoutAdapter(Context con, OnStartDragListener startDragListener, ApplicationVariables av) {
        this.con = con;
        this.startDragListener = startDragListener;
        this.av=av;
        workout=new ArrayList<workOut>();
    }
}
