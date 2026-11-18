package de.nachtblau.amazonglass;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.widget.RemoteViews;
import java.util.List;

public class AmazonWidgetProvider extends AppWidgetProvider {
    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) updateOne(context, manager, id);
    }

    static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, AmazonWidgetProvider.class));
        for (int id : ids) updateOne(context, manager, id);
    }

    private static PendingIntent action(Context c, String name, int code) {
        Intent i = new Intent(c, PlayerActionReceiver.class).setAction(name);
        return PendingIntent.getBroadcast(c, code, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static void updateOne(Context context, AppWidgetManager manager, int id) {
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget_player);
        v.setOnClickPendingIntent(R.id.previous, action(context, PlayerActionReceiver.PREVIOUS, id * 10 + 1));
        v.setOnClickPendingIntent(R.id.play, action(context, PlayerActionReceiver.TOGGLE, id * 10 + 2));
        v.setOnClickPendingIntent(R.id.next, action(context, PlayerActionReceiver.NEXT, id * 10 + 3));
        v.setOnClickPendingIntent(R.id.open, action(context, PlayerActionReceiver.OPEN, id * 10 + 4));
        v.setOnClickPendingIntent(R.id.art, action(context, PlayerActionReceiver.OPEN, id * 10 + 5));

        MediaController controller = findAmazon(context);
        if (controller != null) {
            MediaMetadata metadata = controller.getMetadata();
            String title = metadata == null ? null : metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
            String artist = metadata == null ? null : metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
            v.setTextViewText(R.id.title, title == null || title.isEmpty() ? "Amazon Music" : title);
            v.setTextViewText(R.id.artist, artist == null || artist.isEmpty() ? "Wiedergabe aktiv" : artist);
            Bitmap art = metadata == null ? null : metadata.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
            if (art == null && metadata != null) art = metadata.getBitmap(MediaMetadata.METADATA_KEY_ART);
            if (art != null) v.setImageViewBitmap(R.id.art, art);
            PlaybackState state = controller.getPlaybackState();
            boolean playing = state != null && state.getState() == PlaybackState.STATE_PLAYING;
            v.setTextViewText(R.id.play, playing ? "Ⅱ" : "▶");
            long duration = metadata == null ? 0 : metadata.getLong(MediaMetadata.METADATA_KEY_DURATION);
            long position = state == null ? 0 : Math.max(0, state.getPosition());
            int progress = duration > 0 ? (int)Math.min(1000, position * 1000 / duration) : 0;
            v.setProgressBar(R.id.progress, 1000, progress, false);
        } else {
            v.setTextViewText(R.id.title, "Amazon Music");
            v.setTextViewText(R.id.artist, "Wiedergabe starten");
            v.setTextViewText(R.id.play, "▶");
            v.setProgressBar(R.id.progress, 1000, 0, false);
        }
        manager.updateAppWidget(id, v);
    }

    static MediaController findAmazon(Context context) {
        try {
            MediaSessionManager manager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
            List<MediaController> sessions = manager.getActiveSessions(new ComponentName(context, AmazonMusicListener.class));
            if (sessions == null) return null;
            for (MediaController controller : sessions) {
                if ("com.amazon.mp3".equals(controller.getPackageName())) return controller;
            }
        } catch (SecurityException ignored) { }
        return null;
    }
}
