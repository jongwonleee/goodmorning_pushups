package jongwonapp.goodmorningpushups;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by Administrator on 2018-08-26.
 */

public class exerciseViewHolder extends RecyclerView.ViewHolder {

    public TextView textName;
    public ImageButton butDrag;
    public Button butEdit;
    OnListItemClickListener itemListener;
    public exerciseViewHolder(@NonNull View itemView) {
        super(itemView);
        textName=(TextView)itemView.findViewById(R.id.text_exercise);
        butDrag=(ImageButton)itemView.findViewById(R.id.but_drag);
        butEdit=(Button)itemView.findViewById(R.id.but_edit);
        butEdit.setVisibility(View.INVISIBLE);
        butEdit.setEnabled(false);
        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                itemListener.onListItemClick(getAdapterPosition());
            }
        });
        butEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.onEditButtonClick(getAdapterPosition());
            }
        });
    }

    public boolean click(boolean clicked)
    {
        if(clicked) {
            itemView.setBackgroundColor(Color.LTGRAY);
            butEdit.setVisibility(View.VISIBLE);
            butEdit.setEnabled(true);
        }else {
            itemView.setBackgroundColor(Color.WHITE);
            butEdit.setVisibility(View.INVISIBLE);
            butEdit.setEnabled(false);
        }
        return clicked;
    }
    public void setOnListItemClickListener(exerciseViewHolder.OnListItemClickListener onListItemClickListener){
        itemListener = onListItemClickListener;
    }
    public interface OnListItemClickListener{
        public void onListItemClick(int position);
        public void onEditButtonClick(int position);
    }
}

