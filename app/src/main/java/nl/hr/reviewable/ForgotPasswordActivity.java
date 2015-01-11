package nl.hr.reviewable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;


public class ForgotPasswordActivity extends Activity {

    protected EditText resetPasswordEmail;
    protected Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView titleTextView = (TextView) findViewById(titleId);
        titleTextView.setTextSize(getResources().getDimension(R.dimen.title_proxima_size));
        titleTextView.setTextColor(getResources().getColor(R.color.white));
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Regular.otf");
        titleTextView.setTypeface(face);

        resetPasswordEmail = (EditText)findViewById(R.id.resetPasswordEmail);
        resetPasswordButton = (Button)findViewById(R.id.resetPasswordButton);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String emailStr = resetPasswordEmail.getText().toString().trim();

                ParseUser.requestPasswordResetInBackground(emailStr, new RequestPasswordResetCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null) {
                            Toast registeredMessage = Toast.makeText(ForgotPasswordActivity.this, "An e-mail to reset your password has been sent to your e-mail address.", Toast.LENGTH_LONG);
                            registeredMessage.show();

                            Intent sendIntent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                            startActivity(sendIntent);
                            finish();
                        }
                        else {
                            Toast registeredErrorMessage = Toast.makeText(ForgotPasswordActivity.this, "E-mail could not be sent.", Toast.LENGTH_LONG);
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
        getMenuInflater().inflate(R.menu.menu_forgot_password, menu);
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
