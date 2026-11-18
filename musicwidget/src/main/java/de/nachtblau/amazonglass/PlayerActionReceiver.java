package de.nachtblau.amazonglass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.media.session.PlaybackState;

public class PlayerActionReceiver extends BroadcastReceiver {
    static final String PREVIOUS = "de.nachtblau.amazonglass.PREVIOUS";
    static final String TOGGLE = "de.nachtblau.amazonglass.TOGGLE";
    static final String NEXT = "de.nachtblau.amazonglass.NEXT";
    static final String OPEN = "de.nachtblau.amazonglass.OPEN";

    @Override public void onReceive(Context context, Intent intent) {
        if (OPEN.equals(intent.getAction())) {
            Intent launch = context.getPackageManager().getLaunchIntentForPackage("com.amazon.mp3");
            if (launch == null) launch = new Intent(context, MainActivity.class);
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launch);
            return;
        }
        MediaController controller = AmazonWidgetProvider.findAmazon(context);
        if (controller != null) {
            if (PREVIOUS.equals(intent.getAction())) controller.getTransportControls().skipToPrevious();
            else if (NEXT.equals(intent.getAction())) controller.getTransportControls().skipToNext();
            else if (TOGGLE.equals(intent.getAction())) {
                PlaybackState state = controller.getPlaybackState();
                if (state != null && state.getState() == PlaybackState.STATE_PLAYING) controller.getTransportControls().pause();
                else controller.getTransportControls().play();
            }
        }
        AmazonWidgetProvider.updateAll(context);
    }
}
