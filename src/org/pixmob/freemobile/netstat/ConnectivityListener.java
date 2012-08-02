/*
 * Copyright (C) 2012 Pixmob (http://github.com/pixmob)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pixmob.freemobile.netstat;

import static org.pixmob.freemobile.netstat.BuildConfig.DEBUG;
import static org.pixmob.freemobile.netstat.Constants.TAG;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Listens to network connectivity updates.
 * @author Pixmob
 */
public class ConnectivityListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            setupAlarm(context);
        }
    }

    public static void setupAlarm(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo ni = cm.getActiveNetworkInfo();

        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent uploadIntent = PendingIntent.getService(context, 0, new Intent(context,
                SyncService.class), PendingIntent.FLAG_CANCEL_CURRENT);

        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            // The device is connected to Internet: schedule statistics
            // upload.
            if (DEBUG) {
                Log.d(TAG, "Scheduling statistics upload");
            }
            am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                    AlarmManager.INTERVAL_HALF_HOUR, uploadIntent);
        } else {
            // The device cannot connect to Internet: cancel pending
            // statistics upload.
            if (DEBUG) {
                Log.d(TAG, "Statistics upload schedule canceled");
            }
            am.cancel(uploadIntent);
        }
    }
}
