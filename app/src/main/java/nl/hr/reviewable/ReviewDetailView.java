package nl.hr.reviewable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class ReviewDetailView extends Activity {

    String objectId;
    protected TextView mUser;
    protected TextView mTitle;
    protected TextView mReview;
    protected TextView mTags;
    protected TextView mRating;
    protected TextView mLikes;
    protected ParseImageView mImage;

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail_view);

        mUser = (TextView)findViewById(R.id.usernameDetail);
        mTitle = (TextView)findViewById(R.id.titleDetail);
        mReview = (TextView)findViewById(R.id.reviewDetail);
        mTags = (TextView)findViewById(R.id.tagsDetail);
        mRating = (TextView)findViewById(R.id.ratingDetail);
        mLikes = (TextView)findViewById(R.id.likesDetail);
        mImage = (ParseImageView)findViewById(R.id.imageDetail);

        // Intent
        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectID");

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Review");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    // Success -> Status

                    String userName = parseObject.getString("user");
                    mUser.setText(userName);

                    String userTitle = parseObject.getString("userTitle");
                    mTitle.setText(userTitle);

                    String userReview = parseObject.getString("userReview");
                    mReview.setText(userReview);

                    String userTags = parseObject.getString("userTags");
                    mTags.setText(userTags);

                    // Nog aangemaakt worden in Parse
                    //String userRating = parseObject.getString();
                    //mRating.setText(userRating);

                    // Nog aangemaakt worden in Parse
                    //String userLikes = parseObject.getString();
                    //mLikes.setText(userLikes);

                    ParseFile userImage = parseObject.getParseFile("userImageFile");
                    mImage.setParseFile(userImage);

                    mImage.loadInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            // The image is loaded and displayed!
                        }
                    });
                }
                else {
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
