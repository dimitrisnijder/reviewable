package nl.hr.reviewable;

import android.content.Context;
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

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject reviewObject = mReview.get(position);

        if(reviewObject != null) {
            // Username
            String username = reviewObject.getString("user");
            holder.usernameHomepage.setText(username);

            // Title
            String title = reviewObject.getString("userTitle");
            holder.titleHomepage.setText(title);

//            // Review
//            String review = reviewObject.getString("userReview");
//            holder.reviewHomepage.setText(review);

            // Tags
            String tags = reviewObject.getString("userTags");
            String tagLines = tags.replaceAll(",", "\n");
            holder.tagsHomepage.setText(tagLines);

            final String objectId = reviewObject.getObjectId();

            // Get likes
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Likes");
            query.whereEqualTo("reviewId", objectId);
            // likes.reviewId = review.objectId

            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                    if (e == null) {
                        // Success : list of reviews
                        Log.d("lijstje", "opgehaald " + parseObjects.size());
                        // Count parse objects
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
                                //Toast likedMessage = Toast.makeText(ReviewDetailView.this, "Liked!", Toast.LENGTH_LONG);
                                //likedMessage.show();
                                Log.d("SUCCESS", "YES");
                            } else {
                                //Toast notLikedMessage = Toast.makeText(ReviewDetailView.this, "Something went wrong!", Toast.LENGTH_LONG);
                                //notLikedMessage.show();
                                Log.d("FAIL", "KUT");
                            }
                        }
                    });
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
    }

}
