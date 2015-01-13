package nl.hr.reviewable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class EditAccountActivity extends Activity {

    protected EditText username;
    protected EditText email;
    protected EditText oldPassword;
    protected EditText newPassword;
    protected EditText repeatPassword;
    protected Button updateButton;

    protected String usernameStr;
    protected String emailStr;
    protected String oldPasswordStr;
    protected String newPasswordStr;
    protected String repeatPasswordStr;

    protected String currentUsername;
    protected String currentEmail;

    protected Boolean editAccount = false;
    protected Boolean editPassword = false;
    protected Boolean noPassword = false;
    protected Boolean passwordAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);

        int titleId = getResources().getIdentifier("action_bar_title", "id", "android");
        TextView titleTextView = (TextView) findViewById(titleId);
        titleTextView.setTextSize(getResources().getDimension(R.dimen.title_proxima_size));
        titleTextView.setTextColor(getResources().getColor(R.color.white));
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Regular.otf");
        titleTextView.setTypeface(face);

        username = (EditText)findViewById(R.id.usernameUpdate);
        email = (EditText)findViewById(R.id.emailUpdate);
        oldPassword = (EditText)findViewById(R.id.passwordOldUpdate);
        newPassword = (EditText)findViewById(R.id.passwordUpdate);
        repeatPassword = (EditText)findViewById(R.id.passwordRepeatUpdate);
        updateButton = (Button)findViewById(R.id.updateButton);

        final ParseUser currentUser = ParseUser.getCurrentUser();
        currentUsername = currentUser.getUsername();
        currentEmail = currentUser.getEmail();

        username.setText(currentUsername);
        email.setText(currentEmail);

        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                usernameStr = username.getText().toString().trim();
                emailStr = email.getText().toString().trim();
                oldPasswordStr = oldPassword.getText().toString().trim();
                newPasswordStr = newPassword.getText().toString().trim();
                repeatPasswordStr = repeatPassword.getText().toString().trim();

                if(usernameStr.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
                    builder.setMessage("Username is required")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if(emailStr.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
                    builder.setMessage("E-mail is required")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    editAccount = true;
                }

                if(oldPasswordStr.equals("") && newPasswordStr.equals("") && repeatPasswordStr.equals("")){
                    noPassword = true;
                }
                else {
                    try {
                        ParseUser.logIn(currentUsername, oldPasswordStr);
                        passwordAccepted = true;
                    }
                    catch (ParseException e){
                        if (e != null) {
                            passwordAccepted = false;
                        }
                    }

                    if(oldPasswordStr.equals("")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
                        builder.setMessage("Old password is required")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if(!passwordAccepted){
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
                        builder.setMessage("Current password is not correct")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if(newPasswordStr.equals("")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
                        builder.setMessage("New password is required")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if (newPasswordStr.length() < 6) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
                        builder.setMessage("The minimum password length equals 6")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else if(!repeatPasswordStr.equals(newPasswordStr)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditAccountActivity.this);
                        builder.setMessage("Password confirmation does not match new password")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else {
                        editPassword = true;
                    }
                }

                if((editAccount == true && noPassword == true) || (editAccount == true && editPassword == true)) {
                    if(editAccount) {
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Review");
                        query.whereEqualTo("user", currentUsername);

                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> reviews, com.parse.ParseException e) {
                                if (e == null) {
                                    for(ParseObject r : reviews) {
                                        r.put("user", usernameStr);
                                        r.saveInBackground();
                                    }
                                }
                                else {
                                    Log.e("Update user error", e.getMessage());
                                }
                            }
                        });

                        currentUser.setUsername(usernameStr);
                        currentUser.setEmail(emailStr);
                    }

                    if(editPassword) {
                        currentUser.setPassword(newPasswordStr);
                    }

                    currentUser.saveInBackground();

                    Toast loggedInMessage = Toast.makeText(EditAccountActivity.this, "Successfully updated account", Toast.LENGTH_LONG);
                    loggedInMessage.show();

                    Intent sendIntent = new Intent(EditAccountActivity.this, HomeActivity.class);
                    startActivity(sendIntent);
                    finish();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_account, menu);
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
