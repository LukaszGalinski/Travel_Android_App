package com.example.myapplication.categories_list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.Rate;

import java.util.List;

public class CommentsList extends ArrayAdapter<Rate>
{
    private Activity context;
    private List<Rate> commentsList;
    public CommentsList(Activity context, List<Rate> commentsList){
        super(context, R.layout.comment_list_layout, commentsList);

        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.comment_list_layout, null, true);

        TextView viewUserName = (TextView) listViewItem.findViewById(R.id.userName_comments);
        TextView viewDate = (TextView) listViewItem.findViewById(R.id.DateAndTime_comments);
        TextView message = (TextView) listViewItem.findViewById(R.id.comments_comments);
        RatingBar ratingBar = (RatingBar) listViewItem.findViewById(R.id.userRatingComments);

        Rate rate = commentsList.get(position);
        viewUserName.setText(rate.getPerson());
        viewDate.setText(rate.getDate());
        message.setText(rate.getComment());
        ratingBar.setRating(rate.getRate());

        return listViewItem;
    }
}
