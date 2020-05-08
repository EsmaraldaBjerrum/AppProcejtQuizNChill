package dk.appproject.quiznchill.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import dk.appproject.quiznchill.dtos.Game;
import dk.appproject.quiznchill.dtos.Player;
import dk.appproject.quiznchill.R;

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
        public TextView gameStatus;
        public ImageView statusImage;
        public OnListItemListener onListItemListenerViewHolder;

        public ListViewHolder(View v, OnListItemListener onListItemListener){
            super(v);
            gameCard = v.findViewById(R.id.cardMenuItem);
            quizName = v.findViewById(R.id.txtMenuQuizName);
            gameStatus = v.findViewById(R.id.txtMenuStatus);
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

        //Check if user is quiz master
        Boolean userIsQuizMaster = false;
        if(games.get(position).getQuizMaster() != null){
            if(games.get(position).getQuizMaster().getName().equals(user.getName())){
                userIsQuizMaster = true;
            }
        }
        //Checking status for game
        if(games.get(position).isActive()){
            //Check if user i quiz master
            if(userIsQuizMaster){
                holder.statusImage.setImageResource(R.drawable.quizmaster_icon);
                holder.gameStatus.setText(R.string.FriendsAreStillPlaying);
            }else{

                //Iteration through players to find current user
                for(Player p : games.get(position).getPlayers()){
                    if(p.getName().equals(user.getName())){
                        if(p.isFinishedQuiz()){
                            holder.statusImage.setImageResource(R.drawable.waiting_icon);
                            holder.gameStatus.setText(R.string.WaitingForOpponentsToBeFinish);
                        }else{
                            holder.statusImage.setImageResource(R.drawable.startgame_icon);
                            holder.gameStatus.setText(R.string.playGamePrompt);
                        }
                    }
                }
            }
        }else{
            //Setting the name of the winner or stating multiple winners
            if(games.get(position).getWinners().size() == 1) {
                holder.gameStatus.setText(games.get(position).getWinners().get(0));
            }else{
                holder.gameStatus.setText(R.string.multipleWinners);
            }
            //Setting image
            if(userIsQuizMaster){
                holder.statusImage.setImageResource(R.drawable.quizmaster_icon);
            }else{
                if(games.get(position).getWinners().contains(user.getName())){
                    holder.statusImage.setImageResource(R.drawable.winner_icon);
                }else{
                    holder.statusImage.setImageResource(R.drawable.loser_icon);
                }
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
