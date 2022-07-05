package com.krab.todolist;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent1 = new Intent(context, NotificationActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "todo")
				.setSmallIcon(R.drawable.ic_launcher_background)
				.setContentTitle("Todo Task")
				.setContentText("")
				.setAutoCancel(true)
				.setDefaults(NotificationCompat.DEFAULT_ALL)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setContentIntent(pendingIntent);

		NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
		notificationManagerCompat.notify(123, builder.build());
	}
}
