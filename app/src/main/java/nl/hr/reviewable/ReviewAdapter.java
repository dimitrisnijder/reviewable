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
                holder.ratingHomepage.setTextColor(this.getContext().getResources().getColor(R.color.green));
            }
            else {
                holder.ratingHomepage.setTextColor(this.getContext().getResources().getColor(R.color.red));
            }

            // Tags
            String tags = reviewObject.getString("userTags");
            String tagLines = tags.replaceAll(",", "\n");
            holder.tagsHomepage.setText(tagLines);

            final String objectId = reviewObject.getObjectId();

            // Get likes
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Likes");
            query.whereEqualTo("reviewId", objectId);

            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                    if (e == null) {
                        // Success : total likes on review
                        Log.d("lijstje", reviewObject.getString("userTitle") + " " + parseObjects.size());

                        holder.likesHome.setText(String.valueOf(parseObjects.size()));
                    }
                    else {
                        // Oops
                    }
                }
            });

            // Let users like review
            holder.likesHome.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Get user
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    String currentUsername = currentUser.getUsername();

                    // Likes test
                    ParseObject likesObject = new ParseObject("Likes");
                    likesObject.put("reviewId", objectId);
                    likesObject.put("user", currentUsername);


                    likesObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("SUCCESS", "YES");
                            } else {
                                Log.d("FAIL", "KUT");
                            }
                        }
                    });
                }
            });

            holder.imageHomepage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent toDetailView = new Intent(v.getContext(), ReviewDetailView.class);
                    toDetailView.putExtra("objectID",objectId);
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
