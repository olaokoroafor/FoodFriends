package com.example.foodfriends.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.foodfriends.R;
import com.example.foodfriends.models.Comment;
import com.example.foodfriends.observable_models.UserObservable;
import com.parse.ParseFile;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    private Context context;
    private List<Comment> comments;

    public CommentsAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    /**
     * Inflates view of the item comment xml**/
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Calls binder for a comment at particular position**/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment);

    }

    /**
     * Returns length of comment list**/
    @Override
    public int getItemCount() {
        return comments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvUsername;
        private ImageView ivProfilePic;
        private TextView tvCommentBody;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvCommentUsername);
            ivProfilePic = itemView.findViewById(R.id.ivCommentPfp);
            tvCommentBody = itemView.findViewById(R.id.tvCommentBody);
        }

        /**
         * Binds the xml elements to comment data
         * */
        public void bind(Comment comment) {
            //bind data to view elements
            tvUsername.setText(comment.getUser().getUsername());
            tvCommentBody.setText(comment.getText());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(65));
            ParseFile pfpUrl = new UserObservable(comment.getUser()).getProfilePhoto();
            if (pfpUrl != null){
                Glide.with(context).applyDefaultRequestOptions(requestOptions).load(pfpUrl.getUrl()).into(ivProfilePic);
            }
            else{
                Glide.with(context).applyDefaultRequestOptions(requestOptions).load(context.getResources().getIdentifier("ic_baseline_face_24", "drawable", context.getPackageName())).into(ivProfilePic);
            }

        }

    }

    // Clean all elements of the recycler
    public void clear() {
        comments.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Comment> list) {
        comments.addAll(list);
        notifyDataSetChanged();
    }

}