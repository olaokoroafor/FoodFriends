package com.example.foodfriends.misc;

import android.util.Log;

import com.example.foodfriends.models.Comment;
import com.example.foodfriends.models.Restaurant;
import com.example.foodfriends.observable_models.RestaurantObservable;
import com.example.foodfriends.observable_models.UserObservable;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class CommentFetchHelper {
    private static final String TAG = "CommentFetchHelper";
    List<Comment> comments;


    public CommentFetchHelper(List<Comment> comments){
        this.comments = comments;
    }

    /**
     * Adds comment for user and restaurant to the database**/
    public void addComment(String s, UserObservable user, RestaurantObservable restaurant, CommentListener commentListener) {
        Comment newComment = new Comment();
        newComment.setText(s);
        newComment.setUser(user.getUser());
        newComment.setRestaurant(restaurant.getRestaurant());
        newComment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error saving comment: " + e);
                    //Toast.makeText(this, "Error while saving", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.i(TAG, "Post save was successful!");
                    comments.add(newComment);
                    commentListener.dataChanged();
                }
            }
        });
    }

    /**
     * Adds all the restaurant's comments to the list
     * */
    public void fetchComments(RestaurantObservable restaurant, CommentListener listener) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);

        query.whereEqualTo("restaurant", restaurant.getRestaurant());
        // include data referred by user key
        query.include(Comment.USER_KEY);
        query.include(Comment.RESTAURANT_KEY);
        // order posts by creation date (newest first)
        query.addAscendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> db_comments, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }

                // for debugging purposes let's print every post description to logcat
                for (Comment comment : db_comments) {
                    Log.i(TAG, "Comment: " + comment.getText() + ", username: " + comment.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                comments.addAll(db_comments);
                listener.dataChanged();
            }
        });
    }
}
