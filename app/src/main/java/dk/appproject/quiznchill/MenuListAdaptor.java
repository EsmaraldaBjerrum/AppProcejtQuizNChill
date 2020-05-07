package dk.appproject.quiznchill;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MenuListAdaptor extends RecyclerView.Adapter<MenuListAdaptor.ListViewHolder> {

    private OnListItemListener onListItemListenerLocal;
    private List<Game> games;
    private Player user;

    public MenuListAdaptor(List<Game> games, Player user, OnListItemListener onListItemListener){
        this.user = user;
        this.games = games;
        onListItemListenerLocal = onListItemListener;
    }

    public void setPlayersGames(List<Game> newGames) {
        this.games = newGames;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CardView gameCard;
        public TextView quizName;
        public ImageView statusImage;
        public OnListItemListener onListItemListenerViewHolder;

        public ListViewHolder(View v, OnListItemListener onListItemListener){
            super(v);
            gameCard = v.findViewById(R.id.cardMenuItem);
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
        Color c = new Color();

        if(games.get(position).isActive()){
            //Iteration through players to find current user
            for(Player p : games.get(position).getPlayers()){
                if(p.getName().equals(user.getName())){
                    if(p.isFinishedQuiz()){
                        //holder.gameCard.setCardBackgroundColor(R.color.colorWaitingForOpponents);
                        holder.statusImage.setImageResource(R.drawable.waiting_icon);
                    }else{
                        //holder.gameCard.setCardBackgroundColor(R.color.colorWaitingToBePlayed);
                        holder.statusImage.setImageResource(R.drawable.startgame_icon);
                    }
                }
            }
        }else{
            //Iterate through players to find highest answer rate
            // ToDo: Det ligger på GameObjektet nu, så du burde vel bare kunne hente det derfra nu
            List<String> currentWinner = new ArrayList<>();
            int currentHigh = 0;
            for(Player p : games.get(position).getPlayers()){
                if(p.getCorrectAnswers() > currentHigh){
                    currentWinner.clear();
                    currentHigh = p.getCorrectAnswers();
                    currentWinner.add(p.getName());
                }else if(p.getCorrectAnswers() == currentHigh){
                    currentWinner.add(p.getName());
                }
            }

            if(currentWinner.contains(user.getName())){
                holder.statusImage.setImageResource(R.drawable.winner_icon);
            }else{
                holder.statusImage.setImageResource(R.drawable.loser_icon);
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            holder.gameCard.setElevation(position);
        }
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public interface OnListItemListener{
        void onListItemClick(int index);
    }
}
