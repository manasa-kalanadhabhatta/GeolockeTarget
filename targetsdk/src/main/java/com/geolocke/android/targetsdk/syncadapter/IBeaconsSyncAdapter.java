/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.geolocke.android.targetsdk.syncadapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;



import com.geolocke.android.targetsdk.authenticator.AccountGeneral;
import com.geolocke.android.targetsdk.beans.IBeacon;
import com.geolocke.android.targetsdk.contentprovider.IBeaconsContract;

//import com.geolocke.android.targetsdk.contentprovider.TvShow;
//import com.geolocke.android.targetsdk.contentprovider.TvShowsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * TvShowsSyncAdapter implementation for syncing sample TvShowsSyncAdapter contacts to the
 * platform ContactOperations provider.  This sample shows a basic 2-way
 * sync between the client and a sample server.  It also contains an
 * example of how to update the contacts' status messages, which
 * would be useful for a messaging or social networking client.
 */
public class IBeaconsSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "IBeaconsSyncAdapter";
    //private static final String TAG = "TvShowsSyncAdapter";

    //private final AccountManager mAccountManager;

    public static void initializeSyncAdapter(Context context) throws Exception {
        getSyncAccount(context);
    }

    public IBeaconsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        //mAccountManager = AccountManager.get(context);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) throws Exception {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account account=null;
        for(Account lAccount: accountManager.getAccounts()){
            if(lAccount.type.equals(AccountGeneral.ACCOUNT_TYPE)){
                account = lAccount;
            }
        }
        if(account!=null){
            return account;
        }
        else{
            try {
                createSyncAccount(context,"hello","how","are");
            } catch (Exception e) {
                e.printStackTrace();
            }
            onAccountCreated(account, context);
        }
        return account;
    }



    private static void createSyncAccount(Context context,String username,String password,String email) throws Exception{


        // Create the account type and default account
        Account newAccount = new Account(AccountGeneral.ACCOUNT_NAME, AccountGeneral.ACCOUNT_TYPE);

        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager)context .getSystemService(context.ACCOUNT_SERVICE);

        Bundle userData = new Bundle();
        userData.putString(AccountGeneral.USERNAME, username);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, password, userData)) {
            accountManager.setAuthToken(newAccount, AccountGeneral.ACCOUNT_TYPE, username);
            ContentResolver.setIsSyncable(newAccount, IBeaconsContract.AUTHORITY, 1);
            ContentResolver.setMasterSyncAutomatically(true); // enables AutoSync


            /*
         * Since we've created an account
         */
            IBeaconsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
            ContentResolver.setSyncAutomatically(newAccount, IBeaconsContract.AUTHORITY, true);

        /*
         * Finally, let's do a sync to get things started
         */
            syncImmediately(context);
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            Log.i(TAG, "Account Created");
        } else {
            Log.i(TAG, "Account Can't Created Some Error Occur");
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) throws Exception {

    }

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) throws Exception {
        Account account = getSyncAccount(context);
        String authority = IBeaconsContract.AUTHORITY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    /**
            * Helper method to have the sync adapter sync immediately
    *
            * @param context The context used to access the account service
    */
    public static void syncImmediately(Context context) throws Exception {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                IBeaconsContract.AUTHORITY, bundle);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
        ContentProviderClient provider, SyncResult syncResult) {

        // Building a print of the extras we got
        StringBuilder sb = new StringBuilder();
        if (extras != null) {
            for (String key : extras.keySet()) {
                sb.append(key + "[" + extras.get(key) + "] ");
            }
        }

        Log.d("geolocke", TAG + "> onPerformSync for account[" + account.name + "]. Extras: "+sb.toString());

        try {
            // Get the auth token for the current account and
            // the userObjectId, needed for creating items on Parse.com account
            //String authToken = mAccountManager.blockingGetAuthToken(account,
                    //AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);
            //String userObjectId = mAccountManager.getUserData(account,
                    //AccountGeneral.USERDATA_USER_OBJ_ID);



            Log.d("geolocke", TAG + "> Get remote IBeacons");
            // Get shows from remote

            //CHECK
            List<IBeacon> remoteIBeacons = new ArrayList<IBeacon>();
            Cursor curIBeacons = provider.query(IBeaconsContract.CONTENT_URI, null, null, null, null);
            if (curIBeacons!= null) {
                while (curIBeacons.moveToNext()) {
                    remoteIBeacons.add(IBeacon.fromCursor(curIBeacons));
                }
                curIBeacons.close();
            }
            /*
            List<TvShow> remoteTvShows = new ArrayList<TvShow>();
            Cursor curTvShows = provider.query(TvShowsContract.CONTENT_URI_REMOTE, null, null, null, null);
            if (curTvShows != null) {
                while (curTvShows.moveToNext()) {
                    remoteTvShows.add(TvShow.fromCursor(curTvShows));
                }
                curTvShows.close();
            }
            */

            /*
            Log.d("udinic", TAG + "> Get local TV Shows");
            // Get shows from local
            ArrayList<TvShow> localTvShows = new ArrayList<TvShow>();
            curTvShows = provider.query(TvShowsContract.CONTENT_URI, null, null, null, null);
            if (curTvShows != null) {
                while (curTvShows.moveToNext()) {
                    localTvShows.add(TvShow.fromCursor(curTvShows));
                }
                curTvShows.close();
            }

            // See what Local shows are missing on Remote
            ArrayList<TvShow> showsToRemote = new ArrayList<TvShow>();
            for (TvShow localTvShow : localTvShows) {
                if (!remoteTvShows.contains(localTvShow))
                    showsToRemote.add(localTvShow);
            }

            */

            /*

            if (showsToRemote.size() == 0) {
                Log.d("udinic", TAG + "> No local changes to update server");
            } else {
                Log.d("udinic", TAG + "> Updating remote server with local changes");

                // Updating remote tv shows
                for (TvShow remoteTvShow : showsToRemote) {
                    Log.d("udinic", TAG + "> Local -> Remote [" + remoteTvShow.name + "]");
                    int i = 0;
                    ContentValues showsToRemoteValues[] = new ContentValues[showsToRemote.size()];
                    for (TvShow localTvShow : showsToRemote) {
                        Log.d("udinic", TAG + "> Remote -> Local [" + localTvShow.name + "]");
                        showsToRemoteValues[i++] = localTvShow.getContentValues();
                    }
                    provider.bulkInsert(TvShowsContract.CONTENT_URI_REMOTE, showsToRemoteValues);
                }
            }

            */


            Log.d("geolocke", TAG + "> Finished.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

