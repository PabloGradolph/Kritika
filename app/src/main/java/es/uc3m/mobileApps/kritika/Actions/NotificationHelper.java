package es.uc3m.mobileApps.kritika.Actions;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import es.uc3m.mobileApps.kritika.R;

public class NotificationHelper {

    // Canal para notificaciones de Rating
    public static final String CHANNEL_ID_RATING = "rating_channel";
    public static final CharSequence CHANNEL_NAME_RATING = "Notificaciones de Rating";

    // Canal para notificaciones de Review
    public static final String CHANNEL_ID_REVIEW = "review_channel";
    public static final CharSequence CHANNEL_NAME_REVIEW = "Notificaciones de Review";

    // Canal para notificaciones de Añadir a lista
    public static final String CHANNEL_ID_ADD_TO_LIST = "add_to_list_channel";
    public static final CharSequence CHANNEL_NAME_ADD_TO_LIST = "Notificaciones de Añadir a Lista";

    // Canal para notificaciones de Añadir Media
    public static final String CHANNEL_ID_ADD_MEDIA = "add_meddia_channel";
    public static final CharSequence CHANNEL_NAME_ADD_MEDIA = "Notificaciones de Añadir a Media";

    // Método para mostrar una notificación en un canal específico
    public static void mostrarNotificacion(Context context, String titulo, String contenido, String channelId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Crear el canal de notificación si se ejecuta en una versión de Android Oreo (API 26) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, getChannelName(channelId), NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Construir la notificación utilizando el canal especificado
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(titulo)
                .setContentText(contenido)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Mostrar la notificación
        notificationManager.notify(1, builder.build());
    }

    // Método para obtener el nombre del canal según el identificador del canal
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
