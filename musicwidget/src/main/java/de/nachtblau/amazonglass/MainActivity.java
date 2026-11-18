package de.nachtblau.amazonglass;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView permissionStatus;
    private TextView permissionDescription;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(24), dp(36), dp(24), dp(24));
        root.setBackgroundColor(Color.rgb(7, 17, 38));

        TextView brand = text("N A C H T B L A U   /   M U S I C", 11, 0xFF72DFF4, true);
        root.addView(brand);

        TextView title = text("Amazon Music\nWidget", 30, Color.WHITE, true);
        LinearLayout.LayoutParams titleParams = params(-1, -2);
        titleParams.topMargin = dp(10);
        root.addView(title, titleParams);

        TextView intro = text("Dein Player für den Startbildschirm. Cover, Titel und Steuerung direkt in NachtBlau.", 15, 0xFFB8C9DE, false);
        LinearLayout.LayoutParams introParams = params(-1, -2);
        introParams.topMargin = dp(12);
        root.addView(intro, introParams);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(18), dp(18), dp(18));
        card.setBackground(roundRect(0xFF101F34, 0xFF284660, 1, 20));
        LinearLayout.LayoutParams cardParams = params(-1, -2);
        cardParams.topMargin = dp(28);
        root.addView(card, cardParams);

        permissionStatus = text("", 18, Color.WHITE, true);
        card.addView(permissionStatus);
        permissionDescription = text("", 14, 0xFFB8C9DE, false);
        LinearLayout.LayoutParams statusParams = params(-1, -2);
        statusParams.topMargin = dp(8);
        card.addView(permissionDescription, statusParams);

        Button grant = button("Benachrichtigungszugriff prüfen", 0xFF00BFD9, 0xFF061726);
        LinearLayout.LayoutParams grantParams = params(-1, dp(52));
        grantParams.topMargin = dp(16);
        card.addView(grant, grantParams);
        grant.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));

        TextView stepsTitle = text("WIDGET HINZUFÜGEN", 11, 0xFF72DFF4, true);
        LinearLayout.LayoutParams stepsTitleParams = params(-1, -2);
        stepsTitleParams.topMargin = dp(26);
        root.addView(stepsTitle, stepsTitleParams);

        TextView steps = text("Startbildschirm gedrückt halten → Widgets → NachtBlau Music Player", 15, Color.WHITE, false);
        LinearLayout.LayoutParams stepsParams = params(-1, -2);
        stepsParams.topMargin = dp(8);
        root.addView(steps, stepsParams);

        Button amazon = button("Amazon Music öffnen", 0xFF192C43, Color.WHITE);
        LinearLayout.LayoutParams amazonParams = params(-1, dp(52));
        amazonParams.topMargin = dp(20);
        root.addView(amazon, amazonParams);
        amazon.setOnClickListener(view -> {
            Intent launch = getPackageManager().getLaunchIntentForPackage("com.amazon.mp3");
            if (launch != null) startActivity(launch);
        });

        TextView privacy = text("Android gewährt den Zugriff systemweit. Das Widget ruft ausschließlich die Amazon-Music-Wiedergabe ab.", 12, 0xFF8EA3B9, false);
        LinearLayout.LayoutParams privacyParams = params(-1, -2);
        privacyParams.topMargin = dp(20);
        root.addView(privacy, privacyParams);

        setContentView(root);
    }

    @Override protected void onResume() {
        super.onResume();
        if (permissionStatus == null) return;
        String enabled = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        boolean allowed = enabled != null && enabled.contains(getPackageName());
        permissionStatus.setText(allowed ? "●  Zugriff ist aktiv" : "○  Zugriff noch nicht aktiv");
        permissionStatus.setTextColor(allowed ? 0xFF65E6A3 : 0xFFFFC66D);
        permissionDescription.setText(allowed
                ? "Amazon Music kann Cover, Titel und Wiedergabe an das Widget liefern."
                : "Erforderlich, damit Titel, Cover und Wiedergabesteuerung im Widget erscheinen.");
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return view;
    }

    private Button button(String label, int background, int foreground) {
        Button view = new Button(this);
        view.setText(label);
        view.setAllCaps(false);
        view.setTextColor(foreground);
        view.setTextSize(15);
        view.setBackground(roundRect(background, background, 0, 16));
        return view;
    }

    private GradientDrawable roundRect(int color, int stroke, int strokeDp, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        if (strokeDp > 0) drawable.setStroke(dp(strokeDp), stroke);
        return drawable;
    }

    private LinearLayout.LayoutParams params(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
