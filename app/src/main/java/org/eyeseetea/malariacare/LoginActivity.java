/*
 * Copyright (c) 2015.
 *
 * This file is part of Facility QA Tool App.
 *
 *  Facility QA Tool App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Facility QA Tool App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.events.ResponseEvent;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.User;
import org.hisp.dhis.android.sdk.persistence.preferences.AppPreferences;
import org.hisp.dhis.android.sdk.utils.APIException;

/**
 *
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    /**
     *
     */
    private final static String CLASS_TAG = "LoginActivity";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText serverEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private View viewsContainer;

    private AppPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPrefs = new AppPreferences(getApplicationContext());
        setupUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        Dhis2Application.bus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Dhis2Application.bus.register(this);
    }


    /**
     * Sets up the initial UI elements
     */
    private void setupUI() {
        viewsContainer = findViewById(R.id.login_views_container);
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        serverEditText = (EditText) findViewById(R.id.server_url);
        loginButton = (Button) findViewById(R.id.login_button);

        String server = mPrefs.getServerUrl();
        String username = mPrefs.getUsername();
        String password = "";

        if (server == null) {
            server = "https://";
        }

        if (username == null) {
            username = "";
            password = "";
        }

        serverEditText.setText(server);
        usernameEditText.setText(username);
        passwordEditText.setText(password);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String serverURL = serverEditText.getText().toString();

        //remove whitespace as last character for username
        if (username.charAt(username.length() - 1) == ' ') {
            username = username.substring(0, username.length() - 1);
        }

        login(serverURL, username, password);
    }

    public void login(String serverUrl, String username, String password) {
        showProgress();
        NetworkManager.getInstance().setServerUrl(serverUrl);
        NetworkManager.getInstance().setCredentials(NetworkManager.getInstance().getBase64Manager()
                .toBase64(username, password));
        Dhis2.getInstance().saveCredentials(this, serverUrl, username, password);
        Dhis2.getInstance().login(username, password);
    }

    private void showProgress() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.out_up);
        viewsContainer.startAnimation(anim);
        viewsContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onReceiveResponse(ResponseEvent event) {
        Log.e(CLASS_TAG, "on Login!");

        if (event.getResponseHolder().getItem() != null) {
            if (event.eventType == ResponseEvent.EventType.onLogin) {
                User user = (User) event.getResponseHolder().getItem();
                Log.e(CLASS_TAG, user.getName());
                user.save();
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        handleUser();
                    }
                });
            }
        } else {
            if (event.getResponseHolder() != null && event.getResponseHolder().getApiException() != null) {
                event.getResponseHolder().getApiException().printStackTrace();
                onLoginFail(event.getResponseHolder().getApiException());
            }
        }
    }

    public void onLoginFail(APIException e) {
        Dialog.OnClickListener listener = new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLoginDialog();
            }
        };
        Dhis2.saveCredentials(this, null, null, null);

        if (e.getResponse() == null) {
            String type = "";
            if (e.isHttpError()) type = "HttpError";
            else if (e.isUnknownError()) type = "UnknownError";
            else if (e.isNetworkError()) type = "NetworkError";
            else if (e.isConversionError()) type = "ConversionError";
            Dhis2.getInstance().showErrorDialog(this, getString(R.string.error_message), type + ": "
                    + e.getMessage(), listener);
        } else {
            if (e.getResponse().getStatus() == 401) {
                Dhis2.getInstance().showErrorDialog(this, getString(R.string.error_message),
                        getString(R.string.invalid_username_or_password), listener);
            } else {
                Dhis2.getInstance().showErrorDialog(this, getString(R.string.error_message),
                        getString(R.string.unable_to_login) + " " + e.getMessage(), listener);
            }
        }
    }

    private void showLoginDialog() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.in_down);
        progressBar.setVisibility(View.GONE);
        viewsContainer.setVisibility(View.VISIBLE);
        viewsContainer.startAnimation(anim);
    }

    private void handleUser() {
        mPrefs.putServerUrl(serverEditText.getText().toString());
        mPrefs.putUserName(usernameEditText.getText().toString());
        launchMainActivity();
    }

    public void launchMainActivity() {
        startActivity(new Intent(LoginActivity.this,
                ((Dhis2Application) getApplication()).getMainActivity()));
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
        super.onBackPressed();
    }
}
