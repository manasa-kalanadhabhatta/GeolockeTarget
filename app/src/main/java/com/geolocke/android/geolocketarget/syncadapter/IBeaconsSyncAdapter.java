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
package com.geolocke.android.geolocketarget.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;


import com.geolocke.android.geolocketarget.beans.IBeacon;
import com.geolocke.android.geolocketarget.contentprovider.IBeaconsContract;

//import com.geolocke.android.geolocketarget.contentprovider.TvShow;
//import com.geolocke.android.geolocketarget.contentprovider.TvShowsContract;

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

    public IBeaconsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        //mAccountManager = AccountManager.get(context);
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

