package nl.hr.reviewable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
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
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ReviewDetailView extends Activity {

    String objectId;

    protected TextView mUser;
    protected TextView mTitle;
    protected TextView mReview;
    protected TextView mTags;
    protected TextView mRating;
    protected TextView mLocation;
    protected ParseImageView mImage;
    protected TextView mCreated;
    protected ParseUser currentUser;
    protected Boolean userLiked;
    protected Boolean ownReview = false;
    protected MenuItem deleteItem;
    protected List<ParseObject> userLikes;
    protected Geocoder geocoder;
    public ParseObject review;
    protected Button mLikeButton;
    private ShareActionProvider mShareActionProvider;

    protected double lat;
    protected double lon;
    protected String address;

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
        mLocation = (TextView)findViewById(R.id.locationDetail);
        mCreated = (TextView)findViewById(R.id.createdDetail);
        mLikeButton = (Button)findViewById(R.id.likeButton);

        // Reverse Geocoding
        geocoder = new Geocoder(this, Locale.ENGLISH);

        // Intent
        Intent intent = getIntent();
        objectId = intent.getStringExtra("objectID");

        currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Review");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    // Success -> Status
                    review = parseObject;

                    String userName = review.getString("user");
                    mUser.setText(userName);

                    if(userName.equals(currentUser.getUsername())) {
                        deleteItem.setVisible(true);
                    }

                    String userTitle = review.getString("userTitle");
                    mTitle.setText(userTitle);

                    getActionBar().setTitle(userTitle);

                    String userReview = review.getString("userReview");
                    mReview.setText(userReview);

                    // Tags
                    String tags = review.getString("userTags");
                    String tagLines = tags.replaceAll(",", "\n");
                    mTags.setText(tagLines);

                    ParseGeoPoint location = review.getParseGeoPoint("location");

                    if(location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();

                        if(lat == 0 && lon == 0) {
                            mLocation.setText("No location");
                            mLocation.setClickable(false);
                        }
                        else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Geocoder.isPresent()) {
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

                                    if (addresses.size() != 0) {

                                        Address returnedAddress = addresses.get(0);
                                        StringBuilder strReturnedAddress = new StringBuilder("");

                                        for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                                            if (returnedAddress.getAddressLine(i) != null) {
                                                if (i != 0) {
                                                    strReturnedAddress.append(", ");
                                                }
                                                strReturnedAddress.append(returnedAddress.getAddressLine(i));
                                            }
                                        }

                                        address = strReturnedAddress.toString();

                                        mLocation.setText(strReturnedAddress);
                                        mLocation.setClickable(true);
                                        mLocation.setTextColor(getResources().getColor(R.color.blue));
                                    }
                                } catch (IOException b) {
                                    Log.e("Get street", b.getMessage());
                                }
                            }
                        }
                    }
                    else {
                        mLocation.setText("No location");
                        mLocation.setClickable(false);
                    }

                    Date created = review.getCreatedAt();
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
                    else if(hours < 24) {
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

                    mCreated.setText(difference);

                    // Rating
                    Boolean rating = review.getBoolean("userRating");
                    if(rating) {
                        mRating.setBackground(getResources().getDrawable(R.drawable.review_like));
                    }
                    else {
                        mRating.setBackground(getResources().getDrawable(R.drawable.review_dislike));
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

        mLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=%d&q=%s",
                        lat, lon, 11, address);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                ReviewDetailView.this.startActivity(intent);
            }
        });
    }

    public void didUserLike(ParseObject review) {

        ParseQuery<ParseObject> userLikesQuery = ParseQuery.getQuery("Likes");
        userLikesQuery.whereEqualTo("review", review);
        userLikesQuery.whereEqualTo("user", currentUser);

        userLikesQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userLikesList, ParseException e) {
                if (e == null) {
                    userLikes = userLikesList;
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
        deleteItem = menu.findItem(R.id.action_delete);
        deleteItem.setVisible(false);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) menuItem.getActionProvider();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Review");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject parseObject, ParseException e) {
                        if (e == null) {
                            // Success -> Status
                            review = parseObject;

                            String userTitle = review.getString("userTitle");
                            // Create the share Intent
                            String playStoreLink = "https://play.google.com/store/apps/details?id=" +
                                    getPackageName();
                            String yourShareText = "I've just read this amazing review: " + userTitle + ". Download Reviewable to read it. " + playStoreLink;
                            Intent shareIntent = ShareCompat.IntentBuilder.from(ReviewDetailView.this)
                                    .setType("text/plain").setText(yourShareText).getIntent();
                            // Set the share Intent
                            mShareActionProvider.setShareIntent(shareIntent);
                        }
                    }
                });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            try {
                                review.delete();
                                for (ParseObject like : userLikes) {
                                    like.delete();
                                }
                            }
                            catch (ParseException e) {
                                Log.e("Delete error", e.getMessage());
                                break;
                            }

                            Intent sendIntent = new Intent(ReviewDetailView.this, HomeActivity.class);
                            startActivity(sendIntent);
                            finish();

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(ReviewDetailView.this);
            builder.setMessage("Are you sure you want to delete this review?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
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
