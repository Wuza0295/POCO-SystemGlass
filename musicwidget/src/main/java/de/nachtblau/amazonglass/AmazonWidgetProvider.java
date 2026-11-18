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
        v.setOnClickPendingIntent(R.id.track_info, action(context, PlayerActionReceiver.OPEN, id * 10 + 6));
        MediaController c = findAmazon(context);
        if (c != null) {
            MediaMetadata m = c.getMetadata();
            String title = m == null ? "Amazon Music" : m.getString(MediaMetadata.METADATA_KEY_TITLE);
            String artist = m == null ? "Wiedergabe aktiv" : m.getString(MediaMetadata.METADATA_KEY_ARTIST);
            v.setTextViewText(R.id.title, title == null || title.isEmpty() ? "Amazon Music" : title);
            v.setTextViewText(R.id.artist, artist == null || artist.isEmpty() ? "Amazon Music" : artist);
            Bitmap art = m == null ? null : m.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
            if (art == null && m != null) art = m.getBitmap(MediaMetadata.METADATA_KEY_ART);
            if (art != null) v.setImageViewBitmap(R.id.art, art);
            PlaybackState s = c.getPlaybackState();
            boolean playing = s != null && s.getState() == PlaybackState.STATE_PLAYING;
            v.setImageViewResource(R.id.play, playing ? R.drawable.ic_pause : R.drawable.ic_play);
            v.setContentDescription(R.id.play, playing ? "Wiedergabe pausieren" : "Wiedergabe starten");
            long duration = m == null ? 0 : m.getLong(MediaMetadata.METADATA_KEY_DURATION);
            long position = s == null ? 0 : Math.max(0, s.getPosition());
            v.setProgressBar(R.id.progress, 1000, duration > 0 ? (int)Math.min(1000, position * 1000 / duration) : 0, false);
        } else {
            v.setTextViewText(R.id.title, "Amazon Music");
            v.setTextViewText(R.id.artist, "Widget verbinden – App öffnen");
            v.setImageViewResource(R.id.play, R.drawable.ic_play);
            v.setContentDescription(R.id.play, "Wiedergabe starten");
            v.setProgressBar(R.id.progress, 1000, 0, false);
        }
        manager.updateAppWidget(id, v);
    }

    static MediaController findAmazon(Context context) {
        try {
            MediaSessionManager msm = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
            List<MediaController> sessions = msm.getActiveSessions(new ComponentName(context, AmazonMusicListener.class));
            for (MediaController c : sessions) if ("com.amazon.mp3".equals(c.getPackageName())) return c;
        } catch (Exception ignored) { }
        return null;
    }
}
