package jongwonapp.goodmorningpushups;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class settingActivity extends AppCompatActivity implements exerciseAdapter.OnStartDragListener,workoutAdapter.OnStartDragListener{

    exerciseAdapter exerciseAdapter;
    workoutAdapter workoutAdapter;
    divsAdapter divsAdapter;
    ConstraintLayout layoutExercise,layoutBase,layoutWorkout;
    RecyclerView listExercise,listWorkout,listEdit;
    RecyclerView.LayoutManager exerciseManager,workoutManager,editManager;
    ItemTouchHelperCallback exerciseCallback,workoutCallback;
    ItemTouchHelper exerciseTouchHelper,workoutTouchHelper;
    Button butAdd,butDel,butwAdd,butwDel,butdAdd,butdDel,butcAdd,butcDel,butsAdd,butsDel,butwOk,butwNo;
    ImageButton butExedit,butWoedit;
    ApplicationVariables av;
    EditText textWorkout,textWorkoutEdit,numDiv,numCount,numSet;
    TextView textTimeusing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        av=(ApplicationVariables)getApplication();
        setBasePage();
        setExercisePage();
        setWorkoutPage();


    }

    private void setBasePage()
    {
        exerciseAdapter = new exerciseAdapter(this,this,av);
        listExercise = (RecyclerView)findViewById(R.id.list_exercise);
        butAdd = (Button)findViewById(R.id.but_new);
        butDel=(Button)findViewById(R.id.but_del);
        layoutBase=(ConstraintLayout)findViewById(R.id.layout_base);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());
        listExercise.addItemDecoration(dividerItemDecoration);

        exerciseManager = new LinearLayoutManager(this);
        exerciseCallback = new ItemTouchHelperCallback(exerciseAdapter);
        exerciseTouchHelper = new ItemTouchHelper(exerciseCallback);
        exerciseTouchHelper.attachToRecyclerView(listExercise);
        //for(int i=0;i<5;i++) exerciseAdapter.add("데이터 "+(i+1));
        listExercise.setLayoutManager(exerciseManager);
        listExercise.setAdapter(exerciseAdapter);
        exerciseAdapter.setTodayFocused();
        listExercise.scrollToPosition(av.today);

        butAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int highest=0;
                for(Routine rt: av.routines)
                {
                    String name=rt.name;
                    if(name.contains("새로운 루틴"))
                    {
                        String temp = name.substring("새로운 루틴".length());
                        try{
                            if(highest<Integer.parseInt(temp)) highest=Integer.parseInt(temp);
                        }catch(NumberFormatException e){
                        }
                    }
                }
                exerciseAdapter.add("새로운 루틴"+(highest+1));
            }
        });
        butDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!exerciseAdapter.delete()) Toast.makeText(settingActivity.this,"선택된 목록이 없습니다.",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void exercisePageUp(int position)
    {
        Animation slide_in_up = AnimationUtils.loadAnimation(settingActivity.this,R.anim.slide_in_up);
        workoutAdapter.setWorkout(position);
        textWorkout.setText(av.routines.get(position).name);
        slide_in_up.setFillAfter(true);
        layoutExercise.setEnabled(true);
        layoutExercise.setVisibility(View.VISIBLE);
        layoutBase.setEnabled(false);
        layoutExercise.startAnimation(slide_in_up);
    }

    public void workoutPageUp( int wpos)
    {
        divsAdapter.editnum=wpos;
        workOut wo = av.routines.get(workoutAdapter.editnum).workout.get(wpos);
/*        workOut wo =new workOut("할룽");
        wo.setBaseInfo(5,20,100);
        wo.setPartsInfo(new int[]{15,10,20});*/
        textWorkoutEdit.setText(wo.name);
        numDiv.setText((wo.getTimeInfo().size()-3)+"");
        divsAdapter.setWorkOut(wo);
        numSet.setText(wo.getTimeInfo().get(0).toString());
        numCount.setText(wo.getTimeInfo().get(1).toString());
        setTimeused();
        Animation slide_in_up = AnimationUtils.loadAnimation(settingActivity.this,R.anim.slide_in_up);
        layoutExercise.setEnabled(false);
        layoutWorkout.setEnabled(true);
        layoutWorkout.setVisibility(View.VISIBLE);
        layoutWorkout.startAnimation(slide_in_up);

    }
    public void setTimeused()
    {
        List<Integer> time= divsAdapter.getTime();
        time.set(0,Integer.parseInt(numSet.getText().toString()));
        time.set(1,Integer.parseInt(numCount.getText().toString()));

        int aSet=0;
        for(int i=3;i<time.size();i++) aSet+=time.get(i);
        aSet=((aSet*time.get(1)+time.get(2))*time.get(0)-time.get(2))/10;
        int m = aSet/60;
        int s = aSet%60;
        textTimeusing.setText("예상 소요시간 : 약 "+m+"분 "+s+"초");
    }

    @Override
    public void onBackPressed() {
        if(layoutExercise.isEnabled())
        {
            Animation slide_out_down = AnimationUtils.loadAnimation(settingActivity.this,R.anim.slide_out_down);
            layoutExercise.setEnabled(false);
            layoutExercise.startAnimation(slide_out_down);
            //layoutBase.
            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutBase.setEnabled(true);
                    layoutExercise.setVisibility(View.INVISIBLE);
                    exerciseAdapter.notifyDataSetChanged();
                }
            },450);
        }else if(layoutWorkout.isEnabled())
        {
            Animation slide_out_down = AnimationUtils.loadAnimation(settingActivity.this,R.anim.slide_out_down);
            layoutWorkout.setEnabled(false);
            layoutWorkout.startAnimation(slide_out_down);
            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    layoutExercise.setEnabled(true);
                    layoutWorkout.setVisibility(View.INVISIBLE);
                    workoutAdapter.notifyDataSetChanged();
                }
            },450);
        }else
        {
            super.onBackPressed();
        }

    }

    private void setExercisePage()
    {
        workoutAdapter=new workoutAdapter(this,this,av);
        textWorkout=(EditText) findViewById(R.id.textWorkout);
        layoutExercise=(ConstraintLayout)findViewById(R.id.layout_exercise);
        listWorkout=(RecyclerView)findViewById(R.id.list_workout);
        butwAdd=(Button)findViewById(R.id.but_wnew);
        butwDel=(Button)findViewById(R.id.but_wdel);
        butExedit=(ImageButton)findViewById(R.id.but_exEdit);
        layoutExercise.setVisibility(View.INVISIBLE);
        layoutExercise.setEnabled(false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());
        listWorkout.addItemDecoration(dividerItemDecoration);

        workoutManager = new LinearLayoutManager(this);
        workoutCallback = new ItemTouchHelperCallback(workoutAdapter);
        workoutTouchHelper = new ItemTouchHelper(workoutCallback);
        workoutTouchHelper.attachToRecyclerView(listWorkout);
        listWorkout.setLayoutManager(workoutManager);
        listWorkout.setAdapter(workoutAdapter);
        butwAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int highest=0;
                for(workOut wo:workoutAdapter.workout)
                {
                    String name=wo.name;
                    if(name.contains("새로운 운동"))
                    {
                        String temp = name.substring("새로운 운동".length());
                        try{
                            if(highest<Integer.parseInt(temp)) highest=Integer.parseInt(temp);
                        }catch(NumberFormatException e){
                        }
                    }
                }

                workoutAdapter.add(new workOut("새로운 운동"+(highest+1)));
            }
        });
        butwDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!workoutAdapter.delete()) Toast.makeText(settingActivity.this,"선택된 목록이 없습니다.",Toast.LENGTH_LONG).show();
            }
        });

        butExedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textWorkout.setEnabled(true);
                textWorkout.requestFocus();
                textWorkout.setSelection(textWorkout.length());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
        textWorkout.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                textWorkout.setEnabled(false);
                textWorkout.clearFocus();
                textWorkout.setText(textWorkout.getText().toString().trim());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textWorkout.getWindowToken(),0);
                av.routines.get(workoutAdapter.editnum).name=textWorkout.getText().toString();
                return false;
            }
        });
    }

    private void setWorkoutPage()
    {
        layoutWorkout=(ConstraintLayout)findViewById(R.id.layout_workout);
        textWorkoutEdit=(EditText)findViewById(R.id.textWorkoutEdit);
        butWoedit=(ImageButton)findViewById(R.id.but_woEdit);
        numDiv=(EditText)findViewById(R.id.num_div);
        butdAdd=(Button)findViewById(R.id.div_add1);
        butdDel=(Button)findViewById(R.id.div_sub1);
        listEdit=(RecyclerView)findViewById(R.id.list_edit);
        numCount=(EditText)findViewById(R.id.num_count);
        numSet=(EditText)findViewById(R.id.num_set);
        butcAdd=(Button)findViewById(R.id.div_cadd);
        butcDel=(Button)findViewById(R.id.div_csub);
        butsAdd=(Button)findViewById(R.id.div_sadd);
        butsDel=(Button)findViewById(R.id.div_ssub);
        butwOk=(Button)findViewById(R.id.but_wok);
        butwNo=(Button)findViewById(R.id.but_wno);
        textTimeusing=(TextView)findViewById(R.id.text_timeusing);

        layoutWorkout.setEnabled(false);
        layoutWorkout.setVisibility(View.INVISIBLE);

        workoutManager = new LinearLayoutManager(this);
        divsAdapter = new divsAdapter(this,av);
        listEdit.setLayoutManager(workoutManager);
        listEdit.setAdapter(divsAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getApplicationContext(),new LinearLayoutManager(this).getOrientation());

        listEdit.addItemDecoration(dividerItemDecoration);
        butdAdd.setOnClickListener(new addDelClickListener(numDiv,true));
        butdDel.setOnClickListener(new addDelClickListener(numDiv,false));
        butcAdd.setOnClickListener(new addDelClickListener(numCount,true));
        butcDel.setOnClickListener(new addDelClickListener(numCount,false));
        butsAdd.setOnClickListener(new addDelClickListener(numSet,true));
        butsDel.setOnClickListener(new addDelClickListener(numSet,false));
        numDiv.setEnabled(false);

        butWoedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textWorkoutEdit.setEnabled(true);
                textWorkoutEdit.requestFocus();
                textWorkoutEdit.setSelection(textWorkoutEdit.length());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
        textWorkoutEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                textWorkoutEdit.setEnabled(false);
                textWorkoutEdit.clearFocus();
                textWorkoutEdit.setText(textWorkoutEdit.getText().toString().trim());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textWorkoutEdit.getWindowToken(),0);
                return false;
            }
        });

        butwOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workOut wo = new workOut(textWorkoutEdit.getText().toString().trim());
                List<Integer> time= divsAdapter.getTime();
                int[] parts = new int[time.size()-3];
                for(int i=3;i<time.size();i++) parts[i-3]=time.get(i);
                wo.setBaseInfo(Integer.parseInt(numSet.getText().toString()),Integer.parseInt(numCount.getText().toString()),time.get(2));
                wo.setPartsInfo(parts);
                av.routines.get(workoutAdapter.editnum).workout.set(divsAdapter.editnum,wo);
                Animation slide_out_down = AnimationUtils.loadAnimation(settingActivity.this,R.anim.slide_out_down);
                layoutWorkout.setEnabled(false);
                layoutWorkout.startAnimation(slide_out_down);
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layoutExercise.setEnabled(true);
                        layoutWorkout.setVisibility(View.INVISIBLE);
                        workoutAdapter.notifyDataSetChanged();
                    }
                },450);
            }
        });
        butwNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation slide_out_down = AnimationUtils.loadAnimation(settingActivity.this,R.anim.slide_out_down);
                layoutWorkout.setEnabled(false);
                layoutWorkout.startAnimation(slide_out_down);
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        layoutExercise.setEnabled(true);
                        layoutWorkout.setVisibility(View.INVISIBLE);
                        workoutAdapter.notifyDataSetChanged();
                    }
                },450);
            }
        });
    }

    @Override
    public void onStartDrag(exerciseViewHolder holder) {
        exerciseTouchHelper.startDrag(holder);
    }

    @Override
    public void onWorkoutStartDrag(exerciseViewHolder holder) {workoutTouchHelper.startDrag(holder);}

    private class addDelClickListener implements View.OnClickListener {
        EditText te;
        boolean add;
        public addDelClickListener(EditText te, boolean add) {
            this.te=te;
            this.add=add;
        }

        @Override
        public void onClick(View v) {
            if(add)
                te.setText((Integer.parseInt(te.getText().toString())+1)+"");
            else
                if((Integer.parseInt(te.getText().toString()))>1)
                te.setText((Integer.parseInt(te.getText().toString())-1)+"");
            if(te.equals(numDiv))
            {
                Log.i("!!",numDiv.getText()+" "+divsAdapter.getItemCount());
                if(Integer.parseInt(numDiv.getText().toString())>divsAdapter.getItemCount()-1)
                    divsAdapter.add();
                else if(Integer.parseInt(numDiv.getText().toString())<divsAdapter.getItemCount()-1)
                    divsAdapter.del();
            }
            setTimeused();
        }
    }

}
