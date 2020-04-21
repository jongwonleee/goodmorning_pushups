package jongwonapp.goodmorningpushups;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by Administrator on 2018-08-27.
 */

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private final OnItemMoveListener itemMoveListener;

    public ItemTouchHelperCallback(OnItemMoveListener itemMoveListener) {
        this.itemMoveListener = itemMoveListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags,0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        if(viewHolder.getItemViewType()!=target.getItemViewType()) return false;
        itemMoveListener.OnItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    public interface OnItemMoveListener{
        boolean OnItemMove(int fromPosition,int toPosition);
    }

}
