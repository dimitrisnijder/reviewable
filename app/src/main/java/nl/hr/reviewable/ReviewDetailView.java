package nl.hr.reviewable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
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


public class ReviewDetailView extends Activity {

    String objectId;
    protected TextView mUser;
    protected TextView mTitle;
    protected TextView mReview;
    protected TextView mTags;
    protected TextView mRating;
    protected ParseImageView mImage;

    protected ParseUser currentUser;
    protected Boolean userLiked;

    public ParseObject review;

    protected Button mLikeButton;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail_view);

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView titleTextView = (TextView) findViewById(titleId);
        titleTextView.setTextSize(getResources().getDimension(R.dimen.title_proxima_size));
        titleTextView.setTextColor(getResources().getColor(R.color.white));
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Regular.otf");
        titleTextView.setTypeface(face);

        mUser = (TextView)findViewById(R.id.usernameDetail);
        mTitle = (TextView)findViewById(R.id.titleDetail);
        mReview = (TextView)findViewById(R.id.reviewDetail);
        mTags = (TextView)findViewById(R.id.tagsDetail);
        mRating = (TextView)findViewById(R.id.ratingDetail);
        mImage = (ParseImageView)findViewById(R.id.imageDetail);

        mLikeButton = (Button)findViewById(R.id.likeButton);


        // Intent
        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectID");

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Review");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    // Success -> Status
                    review = parseObject;

                    String userName = review.getString("user");
                    mUser.setText(userName);

                    String userTitle = review.getString("userTitle");
                    mTitle.setText(userTitle);

                    getActionBar().setTitle(userTitle);

                    String userReview = review.getString("userReview");
                    mReview.setText(userReview);

                    String userTags = review.getString("userTags");
                    mTags.setText(userTags);

                    // Rating
                    Boolean rating = review.getBoolean("userRating");
                    if(rating) {
                        mRating.setBackground(getResources().getDrawable(R.drawable.review_good));
                    }
                    else {
                        mRating.setBackground(getResources().getDrawable(R.drawable.review_bad));
                    }

                    ParseFile userImage = review.getParseFile("userImageFile");
                    mImage.setParseFile(userImage);

                    mImage.loadInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            // The image is loaded and displayed!
                        }
                    });

                    // Count likes
                    ParseQuery<ParseObject> likesQuery = ParseQuery.getQuery("Likes");
                    likesQuery.whereEqualTo("review", review);

                    likesQuery.countInBackground(new CountCallback() {
                        public void done(int count, ParseException e) {
                            if (e == null) {
                                mLikeButton.setText(String.valueOf(count));
                            } else {
                                Log.d("likes", e.getMessage());
                            }
                        }
                    });

                    // Check if users liked
                    didUserLike(review);

                }
                else {
                    // Oops
                }
            }
        });

        // Let users like review
        mLikeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //didUserLike(review);

                if (userLiked == false) {

                    ParseObject likesObject = new ParseObject("Likes");

                    ParseRelation userRelation = likesObject.getRelation("user");
                    userRelation.add(currentUser);

                    ParseRelation reviewRelation = likesObject.getRelation("review");
                    reviewRelation.add(review);

                    mLikeButton.setBackground(getResources().getDrawable(R.drawable.heart_darker));
                    int likes = Integer.parseInt(mLikeButton.getText().toString());
                    likes++;
                    mLikeButton.setText(likes + "");
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
                    userLikesQuery.whereEqualTo("review", review);
                    userLikesQuery.whereEqualTo("user", currentUser);

                    mLikeButton.setBackground(getResources().getDrawable(R.drawable.heart_light));
                    int likes = Integer.parseInt(mLikeButton.getText().toString());
                    likes--;
                    mLikeButton.setText(likes + "");
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

    public void didUserLike(ParseObject review) {
        // Check if users liked
        currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> userLikesQuery = ParseQuery.getQuery("Likes");
        userLikesQuery.whereEqualTo("review", review);
        userLikesQuery.whereEqualTo("user", currentUser);

        userLikesQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userLikesList, ParseException e) {
                if (e == null) {

                    if (userLikesList.size() >= 1) {
                        userLiked = true;
                        mLikeButton.setBackground(getResources().getDrawable(R.drawable.heart_darker));
                    } else {
                        userLiked = false;
                        //holder.likesHome.setBackgroundColor(mContext.getResources().getColor(R.color.tab_lighter_gray));
                    }
                } else {
                    // Oops
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.review_detail_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_share) {
            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) item.getActionProvider();

            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Here is the share content body";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);

        }
    }
}
