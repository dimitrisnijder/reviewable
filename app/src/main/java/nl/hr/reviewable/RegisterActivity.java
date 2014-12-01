package nl.hr.reviewable;

import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

        username = (EditText)findViewById(R.id.usernameRegister);
        email = (EditText)findViewById(R.id.emailRegister);
        password = (EditText)findViewById(R.id.passwordRegister);
        registerButton = (Button)findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ParseUser user = new ParseUser();

                String usernameStr = username.getText().toString().trim();
                String passwordStr = password.getText().toString().trim();
                String emailStr = email.getText().toString().trim();

                user.setUsername(usernameStr);
                user.setPassword(passwordStr);
                user.setEmail(emailStr);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {

                        if(e == null) {
                            // Signed up
                            Toast registeredMessage = Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_LONG);
                            registeredMessage.show();

                            Intent sendIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(sendIntent);

                        }
                        else {
                            Toast registeredErrorMessage = Toast.makeText(RegisterActivity.this, "Registration not successful", Toast.LENGTH_LONG);
                            registeredErrorMessage.show();
                        }
                    }
                });
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
