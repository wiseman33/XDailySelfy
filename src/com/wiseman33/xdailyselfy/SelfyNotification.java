/**
 * This file is part of XDailySelfy.
 *
 *   Foobar is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Foobar is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *    
 *    Danil Knysh, 2015
 */
package com.wiseman33.xdailyselfy;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class SelfyNotification extends Service {

	private long[] mVibratePattern = { 0, 200, 200, 300 };
	private static final int NOTIFICATION_ID = 333;
	private static final String NOTIFICATION_TEXT = "It's time to take a selfy!";

	
	private void sendNotification() {
		Intent mainIntent = new Intent(this, MainActivity.class);

        NotificationManager notificationManager
            = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        
        Notification noti = new Notification.Builder(this)
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(this, 0, mainIntent,
                              PendingIntent.FLAG_UPDATE_CURRENT))
            .setContentTitle("XDailyeSelfy")
            .setDefaults(Notification.DEFAULT_ALL)
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setTicker("XDailyeSelfy message")
            .setWhen(System.currentTimeMillis())
            .setVibrate(mVibratePattern)
            .setContentText(NOTIFICATION_TEXT)
            .build();

        notificationManager.notify(NOTIFICATION_ID, noti);
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	sendNotification();
    	
    	return super.onStartCommand(intent, flags, startId);
    }
   
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}