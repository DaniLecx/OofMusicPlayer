//package com.danilecx.oofmusicplayer;
//
//import android.app.Notification;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.RemoteViews;
//import android.widget.Toast;
//
//public class NotificationService extends Service {
//
//    Notification status;
//    private final String LOG_TAG = "NotificationService";
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
//            showNotification();
//            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
////            Intent notificationIntent = new Intent(NotificationService.this, MainActivity.class);
////            notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
//        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
//            Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
//            Log.i(LOG_TAG, "Clicked Previous");
//        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
//            Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
//            Log.i(LOG_TAG, "Clicked Play");
//        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
//            Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
//            Log.i(LOG_TAG, "Clicked Next");
//        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
//            Log.i(LOG_TAG, "Received Stop Foreground Intent");
//            Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
//            stopForeground(true);
//            stopSelf();
//        }
//        return START_STICKY;
//    }
//
//    private void showNotification() {
//        // Using RemoteViews to bind custom layouts into Notification
//        RemoteViews views = new RemoteViews(getPackageName(), R.layout.status_bar);
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        Intent previousIntent = new Intent(this, NotificationService.class);
//        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
//        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);
//
//        Intent playIntent = new Intent(this, NotificationService.class);
//        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
//        PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);
//
//        Intent nextIntent = new Intent(this, NotificationService.class);
//        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
//        PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);
//
//        Intent closeIntent = new Intent(this, NotificationService.class);
//        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
//        PendingIntent pcloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);
//
//        views.setOnClickPendingIntent(R.id.status_bar_previous, ppreviousIntent);
//        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
//        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
//        views.setOnClickPendingIntent(R.id.status_bar_close, pcloseIntent);
//
//
//        views.setTextViewText(R.id.status_bar_track_name, "Song Title");
//
//        status = new Notification.Builder(this).build();
//        status.contentView = views;
//        status.flags = Notification.FLAG_ONGOING_EVENT;
//        status.icon = R.drawable.ic_launcher_background;
//        status.contentIntent = pendingIntent;
//        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
//    }
//
//}