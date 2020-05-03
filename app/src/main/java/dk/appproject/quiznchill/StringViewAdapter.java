package dk.appproject.quiznchill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StringViewAdapter extends RecyclerView.Adapter<StringViewAdapter.ViewHolder> {

    private List<String> list;
    private OnClickListener onClickListener;
    private boolean isQuizzes;
    private int quizSelectedPosition = -1;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private OnClickListener onClickListener;
        private TextView text;
        private CheckBox checkBox;
        private boolean isQuizzes;

        public ViewHolder(View view, OnClickListener listener, boolean IsQuizzes){
            super(view);

            onClickListener = listener;
            view.setOnClickListener(this);
            text = view.findViewById(R.id.tvViewString);
            isQuizzes = IsQuizzes;
            checkBox = view.findViewById(R.id.cbViewString);
        }

        @Override
        public void onClick(View v) {

            checkBox.setChecked(!checkBox.isChecked());

            if (isQuizzes)
                onClickListener.onQuizClick(getAdapterPosition());
            else
                onClickListener.onFriendClick(getAdapterPosition(), checkBox.isChecked());
        }
    }

    @NonNull
    @Override
    public StringViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_string_item, parent, false);

        return new ViewHolder(view, onClickListener, isQuizzes);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        String quiz = list.get(position);
        holder.text.setText(quiz);

        //Inspired by https://stackoverflow.com/questions/39127008/how-can-i-select-only-one-checkbox-in-recyclerview-and-notifydataset-changed
        holder.checkBox.setChecked(quizSelectedPosition == position);
        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                quizSelectedPosition = holder.getAdapterPosition();
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public StringViewAdapter(List<String> List, OnClickListener listener, boolean IsQuizzes) {

        list = List;
        onClickListener = listener;
        isQuizzes = IsQuizzes;
    }

    public interface OnClickListener{
        void onQuizClick(int position);
        void onFriendClick(int position, boolean addOpponent);
    }
}
