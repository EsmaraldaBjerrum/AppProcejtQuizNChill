package dk.appproject.quiznchill;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MenuListAdaptor extends RecyclerView.Adapter<MenuListAdaptor.ListViewHolder> {

    private OnListItemListener onListItemListenerLocal;
    private List<Game> games;

    public MenuListAdaptor(List<Game> games, OnListItemListener onListItemListener){

        this.games = games;
        onListItemListenerLocal = onListItemListener;
    }

    public void setPlayersGames(List<Game> newGames) {
        this.games = newGames;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView quizName;
        public ImageView statusImage;
        public OnListItemListener onListItemListenerViewHolder;

        public ListViewHolder(View v, OnListItemListener onListItemListener){
            super(v);
            quizName = v.findViewById(R.id.txtMenuQuizName);
            statusImage = v.findViewById(R.id.imageMenuStatus);
            onListItemListenerViewHolder = onListItemListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onListItemListenerViewHolder.onListItemClick(getAdapterPosition());
        }
    }

    @Override
    public MenuListAdaptor.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleritem_menu, parent, false);
        ListViewHolder viewHolder = new ListViewHolder(v, onListItemListenerLocal);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        holder.quizName.setText(games.get(position).getQuizName());
        //if()
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public interface OnListItemListener{
        void onListItemClick(int index);
    }
}
