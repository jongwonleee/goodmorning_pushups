package jongwonapp.goodmorningpushups;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.Visibility;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.audiofx.AudioEffect.ERROR;

public class MainActivity extends AppCompatActivity {

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    SharedPreferences pref;
    ApplicationVariables av;
    ConstraintSet con;
    ConstraintLayout mainLayout;
    Button butStart,butSetting;
    ProgressBar progressBar,progBack;
    CheckBox butPause;
    LinearLayout layoutCheckbox;
    ConstraintLayout checklist;
    TextView textRoutine,textActnow,butNext;

    TextToSpeech tts;
    TimerThread timer;
    SoundPool beep;
    int soundId;
    workOut workout;
    Routine routine;
    boolean inWorkout,inSetting,inAction,checklistIn;


    @Override
    protected void onDestroy() {
        if(tts!=null)
        {
            tts.stop();
            tts.shutdown();
        }
        if(beep!=null)
        {
            beep.release();
        }
        if(timer!=null)
            timer.close();
        SharedPreferences.Editor editor=pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(av.routines);
        if(av.routines!=null) editor.putString("routines",json);
        if(av.today>=0)editor.putInt("today",av.today);
        editor.commit();
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        av = (ApplicationVariables)getApplication();
        pref= PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        Gson gson = new Gson();
        String json = pref.getString("routines","");
        if(json=="") {
            av.routines = new ArrayList<Routine>();
            Log.i("!!","no saved data");
        }else{
            Log.i("!!",json);
            av.routines=gson.fromJson(json,new TypeToken<List<Routine>>(){}.getType());}
        av.today=pref.getInt("today",0);

        /*workout = new workOut("팔굽혀펴기");
        workout.setBaseInfo(5,20,300);
        int[] a = {20,15};
        workout.setPartsInfo(a);*/
        routine = av.routines.get(av.today);
        routine.position=0;
        inWorkout=false;
        inSetting=false;
        inWorkout=false;
        checklistIn=false;
        butSetting=(Button)findViewById(R.id.but_setting);
        butStart=(Button)findViewById(R.id.button);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progBack=(ProgressBar)findViewById(R.id.progressBackground);
        butPause=(CheckBox)findViewById(R.id.checkBox);
        layoutCheckbox=(LinearLayout)findViewById(R.id.checkBoxLayout);
        checklist=(ConstraintLayout)findViewById(R.id.layout_checklist);
        textRoutine=(TextView)findViewById(R.id.textRoutine);
        textActnow=(TextView)findViewById(R.id.textActNow);
        butNext=(TextView)findViewById(R.id.but_next);

        textRoutine.setText(routine.name);
        textActnow.setText(routine.getNow().name);
        butNext.setText(routine.getNext().name);
        timer= new TimerThread();
        timer.setDaemon(true);
        timer.start();
        timer.paused=true;
        beep = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        soundId = beep.load(this,R.raw.beep,1);

        con = new ConstraintSet();
        mainLayout= (ConstraintLayout)findViewById(R.id.mainLayout);
        con.clone(mainLayout);

        Animation slide_out= AnimationUtils.loadAnimation(MainActivity.this,R.anim.checklist_out);
        slide_out.setFillAfter(true);
        checklist.setEnabled(false);
        checklist.startAnimation(slide_out);

        butNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer.mills==100) timer.mills=5;
                else timer.mills=100;
            }
        });
        butSetting.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Animation slide_out= AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_out_down);
                slide_out.setFillAfter(true);
                checklist.setEnabled(false);
                checklist.startAnimation(slide_out);
                checklistIn=false;
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, settingActivity.class);
                        startActivity(intent);
                        inSetting=true;
                    }
                },500);
            }
        });


        mainLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){

            @Override
            public boolean onSwipeBottom() {
                if(checklistIn&&!inAction&&!inWorkout)
                {
                    inAction=true;
                    Animation slide_out= AnimationUtils.loadAnimation(MainActivity.this,R.anim.checklist_out);
                    slide_out.setFillAfter(true);
                    checklist.setEnabled(false);
                    butSetting.setEnabled(false);
                    checklist.startAnimation(slide_out);
                    Handler delayHandler = new Handler();
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            inAction=false;
                            butStart.setEnabled(true);
                            checklistIn=false;
                        }
                    },500);
                }

                return super.onSwipeBottom();
            }

            @Override
            public boolean onSwipeTop() {
                if(!checklistIn&&!inAction&&!inWorkout) {
                    inAction=true;
                    butStart.setEnabled(false);

                    Animation slide_in = AnimationUtils.loadAnimation(MainActivity.this,R.anim.checklist_in);
                    slide_in.setFillAfter(true);
                    checklist.startAnimation(slide_in);
                    Handler delayHandler = new Handler();
                    delayHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            inAction=false;
                            checklistIn=true;
                            butSetting.setEnabled(true);
                            checklist.setEnabled(true);
                        }
                    },500);
                }
                return super.onSwipeTop();
            }
        });

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=ERROR){
                    tts.setLanguage(Locale.KOREAN);
                    tts.setPitch(1.0f);
                    tts.setSpeechRate(0.9f);
                }
            }
        });

        butPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.pause();
            }
        });
        butStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                butStart.setEnabled(false);
                inWorkout=true;
                workout = routine.getNow();
                timer.setTimeInfo(workout.getTimeInfo());
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 10000,0);
                progressAnimator.setInterpolator(new DecelerateInterpolator());
                progressAnimator.setDuration(2000);
                progressAnimator.start();
                Animation checklist_in_tohalf = AnimationUtils.loadAnimation(MainActivity.this,R.anim.checklist_in_tohalf);
                checklist_in_tohalf.setFillAfter(true);
                checklist.startAnimation(checklist_in_tohalf);

                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progBack, "progress", 10000,0);
                        progressAnimator.setInterpolator(new DecelerateInterpolator());
                        progressAnimator.setDuration(2000);
                        progressAnimator.start();
                    }
                },1000);
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animation slide_out_left = AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_out_left);
                        slide_out_left.setFillAfter(true);
                        butStart.startAnimation(slide_out_left);

                    }
                },3000);
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        butStart.setVisibility(View.GONE);
                        Animation slide_in_left = AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_in_left);
                        slide_in_left.setFillAfter(true);
                        layoutCheckbox.startAnimation(slide_in_left);
                        butPause.setVisibility(View.VISIBLE);

                    }
                },3500);
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        butPause.setEnabled(true);
                        tts.speak(workout.name+" 준비",TextToSpeech.QUEUE_FLUSH, null);
                        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progBack, "progress", 0,10000);
                        progressAnimator.setInterpolator(new DecelerateInterpolator());
                        progressAnimator.setDuration(2000);
                        progressAnimator.start();
                    }
                },4000);
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timer.paused=false;
                        tts.speak("시작.",TextToSpeech.QUEUE_FLUSH, null);
                        progressBar.setMax(workout.getTotalTime());
                    }
                },6000);
            }
        });
    }

    @Override
    protected void onResume() {
        if(inSetting==true)
        {
            Animation checklist_in = AnimationUtils.loadAnimation(MainActivity.this,R.anim.checklist_in_tobase);
            checklist_in.setFillAfter(true);
            routine = av.routines.get(av.today);
            textRoutine.setText(routine.name);
            textActnow.setText(routine.getNow().name);
            butNext.setText(routine.getNext().name);
            checklist.startAnimation(checklist_in);
            Handler delayHandler = new Handler();
            delayHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    inAction=false;
                    butStart.setEnabled(true);
                    checklistIn=false;
                }
            },500);

        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void toNext(final int time)
    {
  /*      butStart.setEnabled(false);
        inWorkout=true;
        workout = routine.getNow();*/


        Handler delayHandler = new Handler(Looper.getMainLooper());
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", time,0);
                progressAnimator.setInterpolator(new DecelerateInterpolator());
                progressAnimator.setDuration(2000);
                progressAnimator.start();
                Animation checklist_in_tohalf = AnimationUtils.loadAnimation(MainActivity.this,R.anim.checklist_out_fromhalf);
                checklist_in_tohalf.setFillAfter(true);
                checklist.startAnimation(checklist_in_tohalf);
            }
        },100);
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progBack, "progress", time,0);
                progressAnimator.setInterpolator(new DecelerateInterpolator());
                progressAnimator.setDuration(2000);
                progressAnimator.start();
                routine.next();
                workout = routine.getNow();
                textActnow.setText(routine.getNow().name);
                butNext.setText(routine.getNext().name);
            }
        },1100);
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation slide_out_left = AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_out_left);
                slide_out_left.setFillAfter(true);
                layoutCheckbox.startAnimation(slide_out_left);

            }
        },3100);
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                butPause.setVisibility(View.GONE);
                Animation slide_in_left = AnimationUtils.loadAnimation(MainActivity.this,R.anim.slide_in_left);
                slide_in_left.setFillAfter(true);
                butStart.startAnimation(slide_in_left);
                butStart.setVisibility(View.VISIBLE);

            }
        },3600);
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progBack.setMax(10000);
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progBack, "progress", 0,10000);
                progressAnimator.setInterpolator(new DecelerateInterpolator());
                progressAnimator.setDuration(2000);
                progressAnimator.start();
            }
        },4100);
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                butStart.setEnabled(true);
                progressBar.setMax(10000);
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0,10000);
                progressAnimator.setInterpolator(new DecelerateInterpolator());
                progressAnimator.setDuration(2000);
                progressAnimator.start();
            }
        },5100);
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                butStart.setEnabled(true);
                inWorkout=false;
            }
        },7100);
    }

    public String korean(int i)
    {
        String korean[][]={{"하나","둘","셋","넷","다섯","여섯","일곱","여덟","아홉"},{"열","스물","서른","마흔","쉰","예순","일흔","여든","아흔"}};
        String temp="";
        if(i>100){
            int hund = i/100;
            temp = (hund*100)+" ";
            i%=100;
        }
        if(i>0)
        {
            if(i>=10) temp+= korean[1][i/10-1]+" ";
            if(i%10!=0)temp+=korean[0][i%10-1];
        }
        return temp;
    }

    class TimerThread extends Thread{
        int time,div,divTime,set,count,mills;
        boolean paused,run;
        //int[] timeInfo = {5,20,600,15,10,10,10};
        List<Integer> timeInfo;
        public TimerThread() {
            time=0;
            div=0;
            divTime=0;
            set=0;
            count=0;
            mills=100;
            paused=false;
            run=true;
            timeInfo=new ArrayList<Integer>();
        }
        public void setTimeInfo(List<Integer> t)
        {
            timeInfo =t;
        }
        public void close()
        {
            run=false;
        }
        public void pause()
        {
            if(!paused)
            {
                paused=true;
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", time,progressBar.getMax());
                progressAnimator.setInterpolator(new DecelerateInterpolator());
                progressAnimator.setDuration(2000);
                butPause.setEnabled(false);
                progressAnimator.start();
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        butPause.setEnabled(true);
                    }
                },2000);
            }else
            {
                ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress",progressBar.getMax() ,time);
                progressAnimator.setInterpolator(new DecelerateInterpolator());
                progressAnimator.setDuration(4000);
                butPause.setEnabled(false);
                progressAnimator.start();
                Handler delayHandler = new Handler();
                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tts.speak(korean(3), TextToSpeech.QUEUE_ADD, null);
                    }
                },1000);

                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tts.speak(korean(2), TextToSpeech.QUEUE_ADD, null);
                    }
                },2000);

                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tts.speak(korean(1), TextToSpeech.QUEUE_ADD, null);
                    }
                },3000);

                delayHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tts.speak("시작.", TextToSpeech.QUEUE_ADD, null);
                        butPause.setEnabled(true);
                        paused=false;
                    }
                },4000);
            }
        }
        //timeInfo[] : {세트,카운트,휴식,구분동작1,구분동작2,...} 0.1초 단위
        @Override
        public void run() {
            //super.run();
            while(run)
            {
                while(!paused) {
                    time++;
                    progressBar.setProgress(time);
                    divTime++;
                    if (set < timeInfo.get(0)) {
                        if (count < timeInfo.get(1)) {
                            if (divTime >= timeInfo.get(div+3)) {
                                if (div < timeInfo.size() - 4) {
                                    divTime = 0;
                                    div++;
                                    beep.play(soundId, 0.7f, 0.7f, 1, 0, 1.0f);
                                } else if (div >= timeInfo.size() - 4) {
                                    divTime = 0;
                                    div = 0;
                                    count++;
                                    tts.speak(korean(count), TextToSpeech.QUEUE_FLUSH, null);
                                    if (count >= timeInfo.get(1)) {
                                        set++;
                                        if (set < timeInfo.get(0)) {
                                            tts.speak((timeInfo.get(2) / 10) + " 초간 휴식", TextToSpeech.QUEUE_ADD, null);
                                        } else {
                                            tts.speak("수고하셨습니다.", TextToSpeech.QUEUE_ADD, null);
                                            butPause.setChecked(true);
                                            butPause.setEnabled(false);
                                            paused=true;
                                            count = 0;
                                            divTime = 0;
                                            div = 0;
                                            if(!routine.isEnd()) {
                                                MainActivity.this.toNext(time);
                                            }else inWorkout=false;
                                        }
                                    }
                                }
                            }
                        } else {
                            if (divTime >= timeInfo.get(2)) {
                                count = 0;
                                divTime = 0;
                                div = 0;
                                tts.speak("set " + (set + 1), TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }

                    try {
                        Thread.sleep(mills);//100으로 맞추기
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
