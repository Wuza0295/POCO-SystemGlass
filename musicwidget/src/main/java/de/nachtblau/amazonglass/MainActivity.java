package de.nachtblau.amazonglass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 36, 28, 24);
        root.setGravity(Gravity.CENTER_VERTICAL);
        root.setBackgroundColor(0xFF071126);

        TextView title = new TextView(this);
        title.setText("NachtBlau Music Player");
        title.setTextSize(24);
        title.setTextColor(0xFFFFFFFF);

        TextView info = new TextView(this);
        info.setText("Für Albumcover und Musiksteuerung braucht Android den Zugriff auf aktive Medienbenachrichtigungen. Erlaube diesen Zugriff einmalig und füge danach das Widget über die Widget-Auswahl zum Startbildschirm hinzu.");
        info.setTextSize(16);
        info.setTextColor(0xFFD4E3F3);
        info.setPadding(0, 18, 0, 24);

        Button grant = new Button(this);
        grant.setText("Benachrichtigungszugriff öffnen");
        grant.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));

        Button amazon = new Button(this);
        amazon.setText("Amazon Music öffnen");
        amazon.setOnClickListener(view -> {
            Intent launch = getPackageManager().getLaunchIntentForPackage("com.amazon.mp3");
            if (launch != null) startActivity(launch);
        });

        root.addView(title);
        root.addView(info);
        root.addView(grant);
        root.addView(amazon);
        setContentView(root);
    }
}
