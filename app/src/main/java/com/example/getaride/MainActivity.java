package com.example.getaride;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        if (edtDriverOrPassenger.getText().toString().equals("Driver") || edtDriverOrPassenger.getText().toString().equals("Passenger")) {
            if (ParseUser.getCurrentUser() == null) {
                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null && e == null) {
                            FancyToast.makeText(MainActivity.this, "We have an anonymous user!", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                            user.put("as", edtDriverOrPassenger.getText().toString());
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    transitionToPassengerActivity();
                                    transitionToDriverRequestListActivity();
                                }
                            });
                        }
                    }
                });
            }
        }
    }

    enum State {
        SIGNUP, LOGIN
    }

    private State state;
    private Button btnSignUpLogin, btnOneTimeLogin;
    private RadioButton rbDriver, rbPassenger;
    private EditText edtUsername, edtPassword, edtDriverOrPassenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseInstallation.getCurrentInstallation().saveInBackground();
        if (ParseUser.getCurrentUser() != null) {
//            ParseUser.logOut();
            transitionToPassengerActivity();
            transitionToDriverRequestListActivity();
        }

        state = State.SIGNUP;

        btnSignUpLogin = findViewById(R.id.btnSignUpLogin);
        rbDriver = findViewById(R.id.rbDriver);
        rbPassenger = findViewById(R.id.rbPassenger);
        btnOneTimeLogin = findViewById(R.id.btnOneTimeLogin);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtDriverOrPassenger = findViewById(R.id.edtDriveOrPass);

        btnOneTimeLogin.setOnClickListener(this);

        btnSignUpLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == State.SIGNUP) {
                    if (rbDriver.isChecked() == false && rbPassenger.isChecked() == false) {
                        FancyToast.makeText(MainActivity.this, "Are you driver or passenger? Please take a pick!", Toast.LENGTH_SHORT, FancyToast.CONFUSING, true).show();
                        return;
                    }
                    ParseUser appUser = new ParseUser();
                    appUser.setUsername(edtUsername.getText().toString());
                    appUser.setPassword(edtPassword.getText().toString());
                    if (rbDriver.isChecked()) {
                        appUser.put("as", "Driver");
                    } else if (rbPassenger.isChecked()) {
                        appUser.put("as", "Passenger");
                    }
                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                FancyToast.makeText(MainActivity.this, "User Signed up!", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                                transitionToPassengerActivity();
                                transitionToDriverRequestListActivity();
                            }
                        }
                    });
                } else if (state == State.LOGIN) {
                    ParseUser.logInInBackground(edtUsername.getText().toString(), edtPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null && e == null) {
                                FancyToast.makeText(MainActivity.this, "User Logged in!", Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                                transitionToPassengerActivity();
                                transitionToDriverRequestListActivity();
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logInItem:
                if (state == State.SIGNUP) {
                    state = State.LOGIN;
                    item.setTitle("Sign Up");
                    btnSignUpLogin.setText("Log In");
                } else if (state == State.LOGIN) {
                    state = State.SIGNUP;
                    item.setTitle("Log In");
                    btnSignUpLogin.setText("Sign Up");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void transitionToPassengerActivity() {
        if (ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().get("as").equals("Passenger")) {
                Intent intent = new Intent(MainActivity.this, PassengerActivity.class);
                startActivity(intent);
            }
        }
    }

    private void transitionToDriverRequestListActivity() {
        if (ParseUser.getCurrentUser() != null) {
            if (ParseUser.getCurrentUser().get("as").equals("Driver")) {
                Intent intent = new Intent(MainActivity.this, DriverRequestListActivity.class);
                startActivity(intent);
            }
        }
    }
}
