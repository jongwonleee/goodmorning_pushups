package jongwonapp.goodmorningpushups;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Administrator on 2018-08-30.
 */

public class divsViewHolder extends RecyclerView.ViewHolder {
    TextView text_div;
    EditText num_sec;
    Button up1,up01,down1,down01;
    float sec;
    OnItemButtonClickListener itemButtonClickListener;
    public divsViewHolder(View itemView) {
        super(itemView);
        text_div = (TextView)itemView.findViewById(R.id.text_div);
        num_sec=(EditText)itemView.findViewById(R.id.num_sec);
        up1=(Button)itemView.findViewById(R.id.div_add1);
        up01=(Button)itemView.findViewById(R.id.div_add01);
        down1=(Button)itemView.findViewById(R.id.div_sub1);
        down01=(Button)itemView.findViewById(R.id.div_sub01);
        num_sec.setText(new Float(sec/10).toString());
        up1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sec+=10;
                num_sec.setText(sec/10+"");
            }
        });
        up01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sec+=1;
                num_sec.setText(sec/10+"");
            }
        });
        down1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sec>=20) {
                    sec -= 10;
                    num_sec.setText(sec/10+"");
                }
            }
        });
        down01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sec>=11) {
                    sec -= 1;
                    num_sec.setText(sec/10+"");

                }
            }
        });
        num_sec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try{
                    sec = (Float.parseFloat(num_sec.getText().toString()))*10;
                    itemButtonClickListener.onItemButtonClick(getAdapterPosition(),sec);
                }catch (NumberFormatException e)
                {
                    sec =0;
                    num_sec.setText(sec+"");
                }

            }
        });
    }
    public void setSec(int s)
    {
        sec=s;
        num_sec.setText(sec/10+"");
    }

    public void setOnItemButtonClickListener(divsViewHolder.OnItemButtonClickListener onListItemClickListener){
        itemButtonClickListener = onListItemClickListener;
    }
    public interface OnItemButtonClickListener{
        public void onItemButtonClick(int position,float sec);
    }
}
