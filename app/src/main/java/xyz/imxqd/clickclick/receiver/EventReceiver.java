package xyz.imxqd.clickclick.receiver;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import xyz.imxqd.clickclick.log.LogUtils;

/**
 * worked on api 25 or lower
 */
public class EventReceiver extends BroadcastReceiver {
    private static final String TAG = "EventReceiver";

    private static final String QQ_MUSIC_REFRESH_NOTIFICATION_ACTION = "com.tencent.qqmusic.ACTION_REFRESH_PLAYER_NOTIFICATION.QQMusicPhone";
    private static final String QQ_MUSIC_IS_LIKE_KEY = "KEY_IS_FAVOURITE";
    public static final String EVENT_DELETE_PICTURE = "event_delete_picture";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(intent.getAction(), QQ_MUSIC_REFRESH_NOTIFICATION_ACTION)) {
            LogUtils.d( "QQMusic isLike = " + intent.getBooleanExtra(QQ_MUSIC_IS_LIKE_KEY, false));
        } else if (TextUtils.equals(intent.getAction(), EVENT_DELETE_PICTURE)) {
            LogUtils.d("event_delete_picture");
            if (intent.getData() != null) {
                NotificationManagerCompat.from(context).cancelAll();
                context.getContentResolver().delete(intent.getData(), null, null);
            }
        }
    }
}
