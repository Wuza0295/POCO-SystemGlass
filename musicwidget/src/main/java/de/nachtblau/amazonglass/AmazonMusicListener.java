package de.nachtblau.amazonglass;

import android.content.ComponentName;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AmazonMusicListener extends NotificationListenerService {
    private final List<MediaController> controllers = new ArrayList<>();
    private final Map<MediaController, MediaController.Callback> callbacks = new HashMap<>();

    @Override public void onListenerConnected() {
        refreshSessions();
    }

    @Override public void onNotificationPosted(StatusBarNotification sbn) {
        if ("com.amazon.mp3".equals(sbn.getPackageName())) refreshSessions();
    }

    @Override public void onNotificationRemoved(StatusBarNotification sbn) {
        if ("com.amazon.mp3".equals(sbn.getPackageName())) refreshSessions();
    }

    private void refreshSessions() {
        for (Map.Entry<MediaController, MediaController.Callback> entry : callbacks.entrySet()) {
            entry.getKey().unregisterCallback(entry.getValue());
        }
        callbacks.clear();
        controllers.clear();
        try {
            MediaSessionManager manager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
            List<MediaController> active = manager.getActiveSessions(new ComponentName(this, AmazonMusicListener.class));
            if (active != null) {
                for (MediaController controller : active) {
                    if (!"com.amazon.mp3".equals(controller.getPackageName())) continue;
                    MediaController.Callback callback = new MediaController.Callback() {
                        @Override public void onMetadataChanged(android.media.MediaMetadata metadata) {
                            AmazonWidgetProvider.updateAll(AmazonMusicListener.this);
                        }
                        @Override public void onPlaybackStateChanged(android.media.session.PlaybackState state) {
                            AmazonWidgetProvider.updateAll(AmazonMusicListener.this);
                        }
                        @Override public void onSessionDestroyed() {
                            refreshSessions();
                        }
                    };
                    controllers.add(controller);
                    callbacks.put(controller, callback);
                    controller.registerCallback(callback);
                }
            }
        } catch (SecurityException ignored) { }
        AmazonWidgetProvider.updateAll(this);
    }
}
