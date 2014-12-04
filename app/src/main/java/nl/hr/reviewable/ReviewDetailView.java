package nl.hr.reviewable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class ReviewDetailView extends Activity {

    String objectId;
    protected TextView mUser;
    protected TextView mTitle;
    protected TextView mReview;
    protected TextView mTags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail_view);

        mUser = (TextView)findViewById(R.id.reviewDetailUser);
        mTitle = (TextView)findViewById(R.id.reviewDetailTitle);
        mReview = (TextView)findViewById(R.id.reviewDetailView);
        mTags = (TextView)findViewById(R.id.reviewDetailTags);

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
        return super.onOptionsItemSelected(item);
    }
}
