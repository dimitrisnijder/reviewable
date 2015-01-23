package nl.hr.reviewable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class ReviewActivity extends Activity implements LocationListener {

    private LocationManager locationManager;
    private String provider;

    protected Button cameraButton;
    protected ImageView imageView;
    static final int REQUEST_TAKE_PHOTO = 1;
    protected EditText reviewTitle;
    protected EditText reviewText;
    protected EditText reviewTags;
    protected Switch locationSwitch;
    protected ToggleButton reviewRating;
    protected String mCurrentPhotoPath;
    protected Boolean rating = false;

    protected double lat;
    protected double lon;
    protected ParseGeoPoint loc;

    protected Button reviewButton;
    protected Bitmap photoTaken;
    protected ProgressDialog progress;
    protected ProgressDialog progressLocation;

    protected boolean saveLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView titleTextView = (TextView) findViewById(titleId);
        titleTextView.setTextSize(getResources().getDimension(R.dimen.title_proxima_size));
        titleTextView.setTextColor(getResources().getColor(R.color.white));
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Regular.otf");
        titleTextView.setTypeface(face);

        Parse.initialize(this, "HS0km68yDCSvgftT2KILmFET7DFNESfH1rhVSmR2", "X4G5wb3DokD8aARe8lnLAk2HHDxdGTtsmhQQLw99");

        reviewTitle = (EditText)findViewById(R.id.reviewTitle);
        reviewText = (EditText)findViewById(R.id.reviewText);
        reviewTags = (EditText)findViewById(R.id.reviewTags);
        reviewButton = (Button)findViewById(R.id.reviewButton);
        reviewRating = (ToggleButton)findViewById(R.id.reviewRating);
        cameraButton = (Button)findViewById(R.id.cameraButton);
        imageView = (ImageView)findViewById(R.id.mImageView);
        locationSwitch = (Switch)findViewById(R.id.saveLocation);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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
                Boolean userRating = rating;
                loc = new ParseGeoPoint(lat, lon);

                if (photoTaken == null) {
                    // If review is empty
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewActivity.this);
                    builder.setMessage("Please upload a photo")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (userTitle.equals("")) {
                    // If title is empty
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
                else if (userTitle.length() < 3) {
                    // If title is under 3 chars
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReviewActivity.this);
                    builder.setMessage("Title needs at least 3 characters")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (userReview.equals("")) {
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
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photoTaken.compress(Bitmap.CompressFormat.JPEG, 60, stream);
                    byte[] image = stream.toByteArray();

                    // Generate random number for filename
                    Random r = new Random();
                    int i1 = r.nextInt(999999999 - 100000000) + 10000000;

                    // Create file in Parse
                    ParseFile file = new ParseFile(currentUsername+i1+".jpg", image);

                    // Save to parse
                    ParseObject reviewObject = new ParseObject("Review");
                    reviewObject.put("userTitle", userTitle);
                    reviewObject.put("userReview", userReview);
                    reviewObject.put("userTags", userTags);
                    reviewObject.put("user", currentUsername);
                    reviewObject.put("userRating", userRating);

                    if(saveLocation) {
                        reviewObject.put("location", loc);
                    }

                    reviewObject.put("userImageFile", file);

                    progress = new ProgressDialog(ReviewActivity.this, ProgressDialog.STYLE_SPINNER);
                    progress.setMessage("Reviewing...");
                    progress.show();

                    locationManager.removeUpdates(ReviewActivity.this);

                    reviewObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                progress.hide();

                                // To home screen
                                Intent goToHome = new Intent(ReviewActivity.this, HomeActivity.class);
                                startActivity(goToHome);
                                finish();
                            } else {
                                // Oops
                                Log.d("Error on review", e.getMessage());
                            }
                        }
                    });

                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        reviewRating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rating = isChecked;
            }
        });

        locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveLocation = isChecked;
                if(saveLocation == true) {

                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 0, ReviewActivity.this);
                    }
                    else {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300, 0, ReviewActivity.this);
                    }

                    progressLocation = new ProgressDialog(ReviewActivity.this, ProgressDialog.STYLE_SPINNER);
                    progressLocation.setMessage("Fetching location...");
                    progressLocation.show();

                }
            }
        });

    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                Log.d("Photo error", e.getMessage());
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            int targetW = 500;
            int targetH = 500;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            photoTaken = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            imageView.setImageBitmap(photoTaken);

            galleryAddPic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(saveLocation == true) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 0, ReviewActivity.this);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 300, 0, ReviewActivity.this);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();

            if(progressLocation != null) {
                progressLocation.hide();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
