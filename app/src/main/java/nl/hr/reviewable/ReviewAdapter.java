package nl.hr.reviewable;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * Created by mick on 27/11/14.
 */
public class ReviewAdapter extends ArrayAdapter<ParseObject> {

    protected Context mContext;
    protected List<ParseObject> mReview;
    protected ViewHolder holder;
    protected ParseObject reviewObject;

    protected ParseUser currentUser;
    protected String currentUsername;

    protected List<ParseObject> userLikes;

    protected Boolean userLiked = false;

    public ReviewAdapter (Context context, List<ParseObject> review) {
        super(context, R.layout.review_listitem, review);
        mContext = context;
        mReview = review;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.review_listitem, null);
            holder = new ViewHolder();

            holder.usernameHomepage = (TextView) convertView
                    .findViewById(R.id.usernameHome);
            holder.titleHomepage = (TextView) convertView
                    .findViewById(R.id.titleHome);
//            holder.reviewHomepage = (TextView) convertView
//                    .findViewById(R.id.reviewHome);
            holder.tagsHomepage = (TextView) convertView
                    .findViewById(R.id.tagsHome);
            holder.likesHome = (Button) convertView
                    .findViewById(R.id.likesHome);
            holder.imageHomepage = (ParseImageView) convertView
                    .findViewById(R.id.imageHome);

            holder.ratingHomepage = (TextView) convertView.findViewById(R.id.ratingHome);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        reviewObject = mReview.get(position);

        if(reviewObject != null) {


            // Username
            String username = reviewObject.getString("user");
            holder.usernameHomepage.setText(username);

            // Title
            String title = reviewObject.getString("userTitle");
            holder.titleHomepage.setText(title);

            // Rating
            Boolean rating = reviewObject.getBoolean("userRating");
            if(rating) {
                holder.ratingHomepage.setTextColor(mContext.getResources().getColor(R.color.green));
            }
            else {
                holder.ratingHomepage.setTextColor(mContext.getResources().getColor(R.color.red));
            }

            // Tags
            String tags = reviewObject.getString("userTags");
            String tagLines = tags.replaceAll(",", "\n");
            holder.tagsHomepage.setText(tagLines);

            Log.d("reviewable", reviewObject.getString("userTitle") + "loaded");

            ParseQuery<ParseObject> likesQuery = ParseQuery.getQuery("Likes");
            likesQuery.whereEqualTo("review", reviewObject);

            likesQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> likesList, ParseException e) {
                    if (e == null) {
                        // Success : total likes on review
                        Log.d("reviewable", reviewObject.getString("userTitle") + " " + likesList.size());

                        holder.likesHome.setText(String.valueOf(likesList.size()));
                    }
                    else {
                        Log.d("likes", e.getMessage());
                    }
                }
            });


//            // Get likes
//            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Likes");
//            query.whereEqualTo("reviewId", objectId);
//
//            query.orderByDescending("createdAt");
//            query.findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
//                    if (e == null) {
//                        // Success : total likes on review
//                        Log.d("reviewable", reviewObject.getString("userTitle") + " " + parseObjects.size());
//
//                        holder.likesHome.setText(String.valueOf(parseObjects.size()));
//                    }
//                    else {
//                        Log.d("likes", e.getMessage());
//                    }
//                }
//            });

            // Check if users liked
            currentUser = ParseUser.getCurrentUser();

            ParseQuery<ParseObject> userLikesQuery = ParseQuery.getQuery("Likes");
            userLikesQuery.whereEqualTo("review", reviewObject);
            userLikesQuery.whereEqualTo("user", currentUser);

            userLikesQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> userLikesList, ParseException e) {
                    if (e == null) {
                        userLikes = userLikesList;
                        if (userLikesList.size() >= 1) {
                            userLiked = true;
                            holder.likesHome.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
                        }
                        else {
                            userLiked = false;
                            holder.likesHome.setBackgroundColor(mContext.getResources().getColor(R.color.tab_lighter_gray));
                        }

                        Log.d("reviewable", reviewObject.getString("userTitle") + " " + userLiked);
                    }
                    else {
                        // Oops
                    }
                }
            });

//            // Check if users liked
//            ParseQuery<ParseObject> queryLike = new ParseQuery<ParseObject>("Likes");
//            queryLike.whereEqualTo("reviewId", objectId);
//            queryLike.whereEqualTo("user", currentUsername);
//
//            queryLike.orderByDescending("createdAt");
//            queryLike.findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
//                    if (e == null) {
//
//                        userLikes = parseObjects;
//                        if (parseObjects.size() >= 1) {
//                            userLiked = true;
//                            holder.likesHome.setBackgroundColor(mContext.getResources().getColor(R.color.blue));
//                        }
//                        else {
//                            userLiked = false;
//                            holder.likesHome.setBackgroundColor(mContext.getResources().getColor(R.color.tab_lighter_gray));
//                        }
//
//                        Log.d("reviewable", reviewObject.getString("userTitle") + " " + userLiked);
//                    }
//                    else {
//                        // Oops
//                    }
//                }
//            });


            // Let users like review
            holder.likesHome.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Log.d("reviewable", "Delete or add? " + reviewObject.getString("userTitle") + " " + userLiked);

                    if (userLiked == false) {

                        ParseObject likesObject = new ParseObject("Likes");

                        ParseRelation userRelation = likesObject.getRelation("user");
                        userRelation.add(currentUser);

                        ParseRelation reviewRelation = likesObject.getRelation("review");
                        reviewRelation.add(reviewObject);

                        likesObject.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {

                                } else {
                                    Log.d("add like", e.getMessage());
                                }
                            }
                        });

                    }
                    else if(userLiked == true) {
                        for(ParseObject l : userLikes) {
                            l.deleteInBackground();
                        }
                    }
                }
            });

            holder.imageHomepage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent toDetailView = new Intent(v.getContext(), ReviewDetailView.class);
                    toDetailView.putExtra("objectID", reviewObject.getObjectId());
                    v.getContext().startActivity(toDetailView);
                }
            });

            // Image
            ParseFile image = reviewObject.getParseFile("userImageFile");
            holder.imageHomepage.setParseFile(image);

            holder.imageHomepage.loadInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                }
            });
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView usernameHomepage;
        TextView titleHomepage;
        //TextView reviewHomepage;
        TextView tagsHomepage;
        Button likesHome;
        ParseImageView imageHomepage;
        TextView ratingHomepage;
    }

}
