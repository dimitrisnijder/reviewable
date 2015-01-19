package nl.hr.reviewable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.CountCallback;
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

import java.util.Date;
import java.util.List;

/**
 * Created by mick on 27/11/14.
 */
public class ReviewAdapter extends BaseAdapter {

    protected Context mContext;
    protected List<ParseObject> mReview;

    protected ParseUser currentUser;
    protected String currentUsername;

    protected List<ParseObject> userLikes;

    protected Boolean userLiked;

    public ReviewAdapter (Context context, List<ParseObject> review) {
        mContext = context;
        mReview = review;
    }

    @Override
    public int getCount() {
        return mReview.size();
    }

    @Override
    public Object getItem(int pos) {
        return mReview.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater mInflater = ((Activity) mContext).getLayoutInflater();
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.review_listitem, parent, false);
            holder = new ViewHolder();

            holder.usernameHomepage = (TextView) convertView.findViewById(R.id.usernameHome);
            holder.titleHomepage = (TextView) convertView.findViewById(R.id.titleHome);
            holder.tagsHomepage = (TextView) convertView.findViewById(R.id.tagsHome);
            holder.likesHome = (Button) convertView.findViewById(R.id.likesHome);
            holder.imageHomepage = (ParseImageView) convertView.findViewById(R.id.imageHome);
            holder.ratingHomepage = (TextView) convertView.findViewById(R.id.ratingHome);
            holder.createdHomepage = (TextView)convertView.findViewById(R.id.createdHome);

            //setting up the Views
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(mReview.get(position) != null) {

            // Username
            String username = mReview.get(position).getString("user");
            holder.usernameHomepage.setText(username);

            // Title
            String title = mReview.get(position).getString("userTitle");
            holder.titleHomepage.setText(title);

            Date created = mReview.get(position).getCreatedAt();
            Date current = new Date();

            long diff = current.getTime() - created.getTime();
            String difference;

            int minutes = (int) (diff / (60 * 1000));
            int hours = (int) (diff / (60 * 60 * 1000));
            int days = (int) (diff / (24 * 60 * 60 * 1000));

            if(minutes < 60) {
                if(minutes == 1) {
                    difference = minutes + " minute ago";
                }
                else {
                    difference = minutes + " minutes ago";
                }
            }
            else if(hours < 23) {
                if(hours == 1) {
                    difference = hours + " hour ago";
                }
                else {
                    difference = hours + " hours ago";
                }
            }
            else {
                if(days == 1) {
                    difference = days + " day ago";
                }
                else {
                    difference = days + " days ago";
                }
            }

            holder.createdHomepage.setText(difference);



            // Rating
            Boolean rating = mReview.get(position).getBoolean("userRating");
            if(rating) {
                holder.ratingHomepage.setBackground(mContext.getResources().getDrawable(R.drawable.review_good));
            }
            else {
                holder.ratingHomepage.setBackground(mContext.getResources().getDrawable(R.drawable.review_bad));
            }

            // Tags
            String tags = mReview.get(position).getString("userTags");
            String tagLines = tags.replaceAll(",", "\n");
            holder.tagsHomepage.setText(tagLines);

            // Count likes
            ParseQuery<ParseObject> likesQuery = ParseQuery.getQuery("Likes");
            likesQuery.whereEqualTo("review", mReview.get(position));

            likesQuery.countInBackground(new CountCallback() {
                public void done(int count, ParseException e) {
                    if (e == null) {
                        holder.likesHome.setText(String.valueOf(count));
                    } else {
                        Log.d("likes", e.getMessage());
                    }
                }
            });

            // Check if users liked
            currentUser = ParseUser.getCurrentUser();

            ParseQuery<ParseObject> userLikesQuery = ParseQuery.getQuery("Likes");
            userLikesQuery.whereEqualTo("review", mReview.get(position));
            userLikesQuery.whereEqualTo("user", currentUser);

            userLikesQuery.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> userLikesList, ParseException e) {
                    if (e == null) {

                        if (userLikesList.size() >= 1) {
                            userLiked = true;
                            holder.likesHome.setBackground(mContext.getResources().getDrawable(R.drawable.heart_darker));
                        }
                        else {
                            userLiked = false;
                            holder.likesHome.setBackground(mContext.getResources().getDrawable(R.drawable.heart_light));
                        }

                    }
                    //else {
                        // Oops
                    //}
                }
            });

            holder.imageHomepage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent toDetailView = new Intent(v.getContext(), ReviewDetailView.class);
                    toDetailView.putExtra("objectID", mReview.get(position).getObjectId());
                    toDetailView.putExtra("from", "home");
                    v.getContext().startActivity(toDetailView);
                }
            });

            // Image
            ParseFile image = mReview.get(position).getParseFile("userImageFile");
            holder.imageHomepage.setParseFile(image);

            holder.imageHomepage.loadInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                }
            });

            // Let users like review
            holder.likesHome.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (userLiked == false) {

                        ParseObject likesObject = new ParseObject("Likes");

                        ParseRelation userRelation = likesObject.getRelation("user");
                        userRelation.add(currentUser);

                        ParseRelation reviewRelation = likesObject.getRelation("review");
                        reviewRelation.add(mReview.get(position));

                        holder.likesHome.setBackground(mContext.getResources().getDrawable(R.drawable.heart_darker));
                        int likes = Integer.parseInt(holder.likesHome.getText().toString());
                        likes++;
                        holder.likesHome.setText(likes + "");
                        userLiked = true;

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
                    else {
                        ParseQuery<ParseObject> userLikesQuery = ParseQuery.getQuery("Likes");
                        userLikesQuery.whereEqualTo("review", mReview.get(position));
                        userLikesQuery.whereEqualTo("user", currentUser);

                        holder.likesHome.setBackground(mContext.getResources().getDrawable(R.drawable.heart_light));
                        int likes = Integer.parseInt(holder.likesHome.getText().toString());
                        likes--;
                        Log.d("likes", holder.titleHomepage.getText() + " " + likes);
                        holder.likesHome.setText(likes + "");
                        userLiked = false;

                        userLikesQuery.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> userLikesList, ParseException e) {
                                if (e == null) {
                                    for(ParseObject l : userLikesList) {
                                        l.deleteInBackground();
                                    }
                                }
                                else {
                                    Log.d("delete like", e.getMessage());
                                }
                            }
                        });
                    }
                }
            });

        }

        return convertView;
    }

    public static class ViewHolder {
        TextView usernameHomepage;
        TextView titleHomepage;
        TextView createdHomepage;
        TextView tagsHomepage;
        Button likesHome;
        ParseImageView imageHomepage;
        TextView ratingHomepage;
    }

}
