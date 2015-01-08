package nl.hr.reviewable;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
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

        username = (EditText)findViewById(R.id.usernameRegister);
        email = (EditText)findViewById(R.id.emailRegister);
        password = (EditText)findViewById(R.id.passwordRegister);
        registerButton = (Button)findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ParseUser user = new ParseUser();

                Log.d("HALLOOO", email.getEditableText().toString());

                String usernameStr = username.getText().toString().trim();
                String usernameLC = usernameStr.toLowerCase();
                String passwordStr = password.getText().toString().trim();
                String emailStr = email.getText().toString().trim();
                final String emailLC = emailStr.toLowerCase();

                user.setUsername(usernameLC);
                user.setPassword(passwordStr);
                user.setEmail(emailLC);

                //Log.e("email", emailLC);

                if (emailLC == "") {
                    Toast email = Toast.makeText(RegisterActivity.this, "Sorry, no email no account.", Toast.LENGTH_LONG);
                    email.show();
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
                                Log.e("Error", e.getCode() + "");
                                switch (e.getCode()) {
                                    case ParseException.USERNAME_TAKEN:
                                        Toast usernameTakenError = Toast.makeText(RegisterActivity.this, "Sorry, this username has already been taken.", Toast.LENGTH_LONG);
                                        usernameTakenError.show();

                                        break;
                                    case ParseException.PASSWORD_MISSING:
                                        Toast passwordMissingError = Toast.makeText(RegisterActivity.this, "Sorry, you must supply a password to register.", Toast.LENGTH_LONG);
                                        passwordMissingError.show();

                                        break;
                                    case ParseException.EMAIL_MISSING:
                                        Toast emailMissingError = Toast.makeText(RegisterActivity.this, "Sorry, you must supply a email to register.", Toast.LENGTH_LONG);
                                        emailMissingError.show();

                                        break;
                                    case ParseException.EMAIL_TAKEN:
                                        Toast emailTakenError = Toast.makeText(RegisterActivity.this, "Sorry, this email is already taken.", Toast.LENGTH_LONG);
                                        emailTakenError.show();

                                        break;
                                    case ParseException.INVALID_EMAIL_ADDRESS:
                                        Toast emailInvalidError = Toast.makeText(RegisterActivity.this, "Sorry, the email is invalid.", Toast.LENGTH_LONG);
                                        emailInvalidError.show();

                                        break;
                                    case ParseException.USERNAME_MISSING:
                                        Toast usernameMissingError = Toast.makeText(RegisterActivity.this, "Sorry, you must supply a username to register.", Toast.LENGTH_LONG);
                                        usernameMissingError.show();

                                        break;
                                    default:
                                        Toast errorMessage = Toast.makeText(RegisterActivity.this, "The registration did not succeed.", Toast.LENGTH_LONG);
                                        errorMessage.show();
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
