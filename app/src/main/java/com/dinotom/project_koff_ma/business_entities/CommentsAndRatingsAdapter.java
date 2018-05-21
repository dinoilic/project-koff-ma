package com.dinotom.project_koff_ma.business_entities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dinotom.project_koff_ma.R;
import com.dinotom.project_koff_ma.pojo.business_entities.CommentAndRating;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class CommentsAndRatingsAdapter extends RecyclerView.Adapter<CommentsAndRatingsAdapter.ViewHolder>
{
    private List<CommentAndRating> commentAndRatings;
    private Context mCtx;
    private String userPk;

    CommentsAndRatingsAdapter(Context mCtx, String userPk)
    {
        this.mCtx = mCtx;
        commentAndRatings = new ArrayList<>();
        this.userPk = userPk;
    }

    @Override
    public CommentsAndRatingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.commentandrating_card, parent, false);

        return new CommentsAndRatingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommentsAndRatingsAdapter.ViewHolder holder, final int position)
    {
        String fullName = MessageFormat.format("{0} {1}",
                commentAndRatings.get(position).getUser().get(0),
                commentAndRatings.get(position).getUser().get(1));
        holder.userFullName.setText(fullName);
        holder.userRating.setRating(commentAndRatings.get(position).getRating());
        holder.updatedAt.setText(commentAndRatings.get(position).getUpdatedAt());
        holder.comment.setText(commentAndRatings.get(position).getComment());

        if(!commentAndRatings.get(position).getUser().get(3).equals(userPk) ||
                commentAndRatings.get(position).getComment().isEmpty())
        {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    public void addItems(List<CommentAndRating> items)
    {
        this.commentAndRatings.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount()
    {
        return commentAndRatings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView userFullName;
        private RatingBar userRating;
        private TextView updatedAt;
        private TextView comment;
        private Button editButton;
        private Button deleteButton;

        public ViewHolder(final View itemView)
        {
            super(itemView);

            userFullName = itemView.findViewById(R.id.user_full_name);
            userRating = itemView.findViewById(R.id.user_rating);
            updatedAt = itemView.findViewById(R.id.updated_at);
            comment = itemView.findViewById(R.id.comment);
            editButton = itemView.findViewById(R.id.edit_comment_button);
            deleteButton = itemView.findViewById(R.id.delete_comment_button);
        }
    }
}