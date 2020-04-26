package dk.appproject.quiznchill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class MenuListAdaptor extends RecyclerView.Adapter<MenuListAdaptor.ListViewHolder> {

    private OnListItemListener onlistItemListenerLocal;

    public MenuListAdaptor( OnListItemListener onListItemListener){

        //Data

        onlistItemListenerLocal = onListItemListener;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public OnListItemListener onListItemListenerViewHolder;
        public ListViewHolder(View v, OnListItemListener onListItemListener){
            super(v);
        }

        @Override
        public void onClick(View v) {
            onListItemListenerViewHolder.onListItemClick(getAdapterPosition());
        }
    }

    @Override
    public MenuListAdaptor.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleritem_menu, parent, false);

        //ListViewHolder viewHolder = new ListViewHolder(v, onlistItemListenerLocal);
        //return viewHolder;
        return null;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface OnListItemListener{
        void onListItemClick(int index);
    }
}
