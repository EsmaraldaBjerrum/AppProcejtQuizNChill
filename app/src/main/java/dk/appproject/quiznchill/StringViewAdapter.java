package dk.appproject.quiznchill;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StringViewAdapter extends RecyclerView.Adapter<StringViewAdapter.ViewHolder> {

    private List<String> list;
    private OnClickListener onClickListener;
    private boolean isQuizzes;


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private OnClickListener onClickListener;
        private TextView text;
        private boolean isQuizzes;

        public ViewHolder(View view, OnClickListener listener, boolean IsQuizzes){
            super(view);

            onClickListener = listener;
            view.setOnClickListener(this);
            text = view.findViewById(R.id.tvViewString);
            isQuizzes = IsQuizzes;
        }

        @Override
        public void onClick(View v) {

            if (isQuizzes)
                onClickListener.onQuizClick(getAdapterPosition());
            else
                onClickListener.onFriendClick(getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public StringViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_string_item, parent, false);

        return new ViewHolder(view, onClickListener, isQuizzes);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String quiz = list.get(position);
        holder.text.setText(quiz);
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
        void onFriendClick(int position);
    }
}
