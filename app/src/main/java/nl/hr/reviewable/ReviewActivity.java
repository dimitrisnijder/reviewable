package nl.hr.reviewable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class ReviewActivity extends Activity {

    protected EditText reviewTitle;
    protected EditText reviewText;
    protected EditText reviewTags;

    protected Button reviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        Parse.initialize(this, "HS0km68yDCSvgftT2KILmFET7DFNESfH1rhVSmR2", "X4G5wb3DokD8aARe8lnLAk2HHDxdGTtsmhQQLw99");

        // Initialize
        reviewTitle = (EditText)findViewById(R.id.reviewTitle);
        reviewText = (EditText)findViewById(R.id.reviewText);
        reviewTags = (EditText)findViewById(R.id.reviewTags);

        reviewButton = (Button)findViewById(R.id.reviewButton);

        // Review
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Get user
            ParseUser currentUser = ParseUser.getCurrentUser();
            String currentUsername = currentUser.getUsername();

            // Get review
            String userTitle = reviewTitle.getText().toString();
            String userReview = reviewText.getText().toString();
            String userTags = reviewTags.getText().toString();

            if (userTitle.isEmpty()) {
                // If review is empty
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewActivity.this);
                builder.setMessage("Title is empty")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else if (userReview.isEmpty()) {
                // If review is empty
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewActivity.this);
                builder.setMessage("Review is empty")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                // Save to parse
                ParseObject reviewObject = new ParseObject("Review");
                reviewObject.put("userTitle", userTitle);
                reviewObject.put("userReview", userReview);
                reviewObject.put("userTags", userTags);
                reviewObject.put("user", currentUsername);
                reviewObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            // Great success
                            Toast.makeText(ReviewActivity.this, "Reviewabled!", Toast.LENGTH_LONG).show();

                            // To home screen
                            Intent goToHome = new Intent(ReviewActivity.this, HomeActivity.class);
                            startActivity(goToHome);
                        } else {
                            // Oops
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReviewActivity.this);
                            builder.setMessage("Login failed")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                        }
                                    });
                            // Create the AlertDialog object and return it
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });

            }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_review, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
