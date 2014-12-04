package nl.hr.reviewable;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class HomeActivity extends ListActivity {

    protected List<ParseObject> mReview;

    protected Typeface face;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getActionBar().setDisplayShowHomeEnabled(false);

        Parse.initialize(this, "HS0km68yDCSvgftT2KILmFET7DFNESfH1rhVSmR2", "X4G5wb3DokD8aARe8lnLAk2HHDxdGTtsmhQQLw99");

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Show home screen with reviews
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Review");
            query.orderByDescending("createdAt");
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                    if (e == null) {
                        // Success : list of reviews

                        mReview = parseObjects;
                        ReviewAdapter adapter = new ReviewAdapter(getListView().getContext(), mReview);
                        setListAdapter(adapter);
                    }
                    else {
                        // Oops


                    }
                }
            });

        } else {
            // Go to login screen
            Intent goToLogin = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(goToLogin);
        }

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView titleTextView = (TextView) findViewById(titleId);
        titleTextView.setTextSize(getResources().getDimension(R.dimen.title_size));
        titleTextView.setTextColor(getResources().getColor(R.color.white));
        face = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        titleTextView.setTypeface(face);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.new_review:
                Intent reviewIntent = new Intent(HomeActivity.this, ReviewActivity.class);
                startActivity(reviewIntent);
                this.finish();

                break;
            case R.id.logout:
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    ParseUser.logOut();
                }

                Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                this.finish();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject reviewObject = mReview.get(position);
        String objectId = reviewObject.getObjectId();

        Intent toDetailView = new Intent(HomeActivity.this, ReviewDetailView.class);
        toDetailView.putExtra("objectID",objectId);
        startActivity(toDetailView);

        // Get ID
        //Toast.makeText(getApplicationContext(), objectId, Toast.LENGTH_LONG).show();
    }
}
