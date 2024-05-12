package com.example.easytodo.utils;

import android.os.AsyncTask;

import com.example.easytodo.models.DB;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class AddNewUserBack extends AsyncTask<GoogleSignInAccount, Void, Void> {
    @Override
    protected Void doInBackground(GoogleSignInAccount... googleSignInAccounts) {
        DB.addUser(googleSignInAccounts[0]);
        return null;
    }
}
