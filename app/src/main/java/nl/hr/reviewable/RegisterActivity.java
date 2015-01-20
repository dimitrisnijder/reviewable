package nl.hr.reviewable;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class RegisterActivity extends Activity {

    protected EditText username;
    protected EditText email;
    protected EditText password;
    protected Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView titleTextView = (TextView) findViewById(titleId);
        titleTextView.setTextSize(getResources().getDimension(R.dimen.title_proxima_size));
        titleTextView.setTextColor(getResources().getColor(R.color.white));
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Regular.otf");
        titleTextView.setTypeface(face);

        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background1));

        username = (EditText)findViewById(R.id.usernameRegister);
        email = (EditText)findViewById(R.id.emailRegister);
        password = (EditText)findViewById(R.id.passwordRegister);
        registerButton = (Button)findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ParseUser user = new ParseUser();

                String usernameStr = username.getText().toString().trim();
                String usernameLC = usernameStr.toLowerCase();
                String passwordStr = password.getText().toString().trim();
                String emailStr = email.getText().toString().trim();

                user.setUsername(usernameLC);
                user.setPassword(passwordStr);
                user.setEmail(emailStr);

                if (usernameStr.equals("")) {
                    //Toast username = Toast.makeText(RegisterActivity.this, "Sorry, no username no account.", Toast.LENGTH_LONG);
                    //username.show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Username is required")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (emailStr.equals("")) {
                    //Toast email = Toast.makeText(RegisterActivity.this, "Sorry, no email no account.", Toast.LENGTH_LONG);
                    //email.show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("E-mail is required")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (passwordStr.equals("")) {
                    //Toast password = Toast.makeText(RegisterActivity.this, "Sorry, no password no account.", Toast.LENGTH_LONG);
                    //password.show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Password is required")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if (passwordStr.length() < 6) {
                    //Toast password = Toast.makeText(RegisterActivity.this, "Sorry, the minimum password length equals 6.", Toast.LENGTH_LONG);
                    //password.show();

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("The minimum password length equals 6")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    // Create the AlertDialog object and return it
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {

                                // Signed up
                                Toast registeredMessage = Toast.makeText(RegisterActivity.this, "Successfully registered.", Toast.LENGTH_LONG);
                                registeredMessage.show();

                                Intent sendIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(sendIntent);
                                finish();

                            } else {

                                switch (e.getCode()) {
                                    case ParseException.USERNAME_TAKEN:
                                        //Toast usernameTakenError = Toast.makeText(RegisterActivity.this, "Sorry, this username has already been taken.", Toast.LENGTH_LONG);
                                        //usernameTakenError.show();

                                        AlertDialog.Builder builderUT = new AlertDialog.Builder(RegisterActivity.this);
                                        builderUT.setMessage("Sorry, this username has already been taken.")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialogUT = builderUT.create();
                                        dialogUT.show();

                                        break;
                                    case ParseException.PASSWORD_MISSING:
                                        //Toast passwordMissingError = Toast.makeText(RegisterActivity.this, "Sorry, you must supply a password to register.", Toast.LENGTH_LONG);
                                        //passwordMissingError.show();

                                        AlertDialog.Builder builderPM = new AlertDialog.Builder(RegisterActivity.this);
                                        builderPM.setMessage("Sorry, you must supply a password to register.")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialogPM = builderPM.create();
                                        dialogPM.show();

                                        break;
                                    case ParseException.EMAIL_MISSING:
                                        //Toast emailMissingError = Toast.makeText(RegisterActivity.this, "Sorry, you must supply a email to register.", Toast.LENGTH_LONG);
                                        //emailMissingError.show();

                                        AlertDialog.Builder builderEM = new AlertDialog.Builder(RegisterActivity.this);
                                        builderEM.setMessage("Sorry, you must supply a email to register.")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialogEM = builderEM.create();
                                        dialogEM.show();

                                        break;
                                    case ParseException.EMAIL_TAKEN:
                                        //Toast emailTakenError = Toast.makeText(RegisterActivity.this, "Sorry, this email is already taken.", Toast.LENGTH_LONG);
                                        //emailTakenError.show();

                                        AlertDialog.Builder builderET = new AlertDialog.Builder(RegisterActivity.this);
                                        builderET.setMessage("Sorry, this email is already taken.")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialogET = builderET.create();
                                        dialogET.show();

                                        break;
                                    case ParseException.INVALID_EMAIL_ADDRESS:
                                        //Toast emailInvalidError = Toast.makeText(RegisterActivity.this, "Sorry, the email is invalid.", Toast.LENGTH_LONG);
                                        //emailInvalidError.show();

                                        AlertDialog.Builder builderIEA = new AlertDialog.Builder(RegisterActivity.this);
                                        builderIEA.setMessage("Sorry, the email is invalid.")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialogIEA = builderIEA.create();
                                        dialogIEA.show();

                                        break;
                                    case ParseException.USERNAME_MISSING:
                                        //Toast usernameMissingError = Toast.makeText(RegisterActivity.this, "Sorry, you must supply a username to register.", Toast.LENGTH_LONG);
                                        //usernameMissingError.show();

                                        AlertDialog.Builder builderUM = new AlertDialog.Builder(RegisterActivity.this);
                                        builderUM.setMessage("Sorry, you must supply a username to register.")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialogUM = builderUM.create();
                                        dialogUM.show();

                                        break;
                                    default:
                                        //Toast errorMessage = Toast.makeText(RegisterActivity.this, "The registration did not succeed.", Toast.LENGTH_LONG);
                                        //errorMessage.show();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        builder.setMessage("The registration did not succeed.")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        // Create the AlertDialog object and return it
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                }
//                            Toast registeredErrorMessage = Toast.makeText(RegisterActivity.this, "The registration did not succeed.", Toast.LENGTH_LONG);
//                            registeredErrorMessage.show();
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                } else {
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
