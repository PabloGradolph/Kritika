package es.uc3m.mobileApps.kritika.Actions;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import es.uc3m.mobileApps.kritika.R;

/**
 * Helper class for managing notifications.
 */
public class NotificationHelper {

    // Notification channel for Rating notifications
    public static final String CHANNEL_ID_RATING = "rating_channel";
    public static final CharSequence CHANNEL_NAME_RATING = "Notificaciones de Rating";

    // Notification channel for Review notifications
    public static final String CHANNEL_ID_REVIEW = "review_channel";
    public static final CharSequence CHANNEL_NAME_REVIEW = "Notificaciones de Review";

    // Notification channel for Add to List notifications
    public static final String CHANNEL_ID_ADD_TO_LIST = "add_to_list_channel";
    public static final CharSequence CHANNEL_NAME_ADD_TO_LIST = "Notificaciones de Añadir a Lista";

    // Notification channel for Add Media notifications
    public static final String CHANNEL_ID_ADD_MEDIA = "add_meddia_channel";
    public static final CharSequence CHANNEL_NAME_ADD_MEDIA = "Notificaciones de Añadir a Media";

    /**
     * Show a notification on a specific channel.
     *
     * @param context   Context
     * @param title     Notification title
     * @param content   Notification content
     * @param channelId Channel ID
     */
    public static void showNotification(Context context, String title, String content, String channelId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel if running on Android Oreo (API 26) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, getChannelName(channelId), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Build notification using the specified channel
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        notificationManager.notify(1, builder.build());
    }

    /**
     * Get the channel name based on the channel ID.
     *
     * @param channelId Channel ID
     * @return Channel name
     */
    private static CharSequence getChannelName(String channelId) {
        switch (channelId) {
            case CHANNEL_ID_RATING:
                return CHANNEL_NAME_RATING;
            case CHANNEL_ID_REVIEW:
                return CHANNEL_NAME_REVIEW;
            case CHANNEL_ID_ADD_TO_LIST:
                return CHANNEL_NAME_ADD_TO_LIST;
            case CHANNEL_ID_ADD_MEDIA:
                return CHANNEL_NAME_ADD_MEDIA;
            default:
                return null;
        }
    }
}
