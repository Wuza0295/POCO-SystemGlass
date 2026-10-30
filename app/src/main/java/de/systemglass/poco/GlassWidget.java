package de.systemglass.poco;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.app.ActivityManager;
import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GlassWidget extends AppWidgetProvider {
    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) render(context, manager, id, getClass());
    }

    static void render(Context c, AppWidgetManager manager, int id, Class<?> provider) {
        BatteryManager batteryManager = (BatteryManager)c.getSystemService(Context.BATTERY_SERVICE);
        int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        ActivityManager am = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memory = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memory);
        String ram = String.format(Locale.GERMANY, "%.1f", memory.availMem / 1073741824.0);
        StatFs fs = new StatFs(Environment.getDataDirectory().getPath());
        long storage = fs.getAvailableBytes() / 1073741824L;
        ConnectivityManager cm = (ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = cm.getActiveNetwork();
        NetworkCapabilities caps = network == null ? null : cm.getNetworkCapabilities(network);
        String connection = caps == null ? "Offline" :
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ? "WLAN" :
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ? "Mobilfunk" : "Verbunden";
        String clock = new SimpleDateFormat("HH:mm", Locale.GERMANY).format(new Date());
        int layout = provider == GlassWidgetXL.class ? R.layout.widget_detail :
                     provider == GlassWidgetLarge.class ? R.layout.widget_dashboard : R.layout.widget_mini;
        RemoteViews v = new RemoteViews(c.getPackageName(), layout);
        if (layout == R.layout.widget_mini) {
            v.setTextViewText(R.id.miniBattery, "AKKU  " + battery + "%");
            v.setTextViewText(R.id.miniInfo, "RAM " + ram + " GB  ·  Frei " + storage + " GB");
        } else if (layout == R.layout.widget_dashboard) {
            v.setTextViewText(R.id.dashBattery, battery + "%");
            v.setTextViewText(R.id.dashRam, ram + " GB");
            v.setTextViewText(R.id.dashBottom, "SPEICHER " + storage + " GB   ·   " + connection);
        } else {
            v.setTextViewText(R.id.detailBattery, "AKKU   " + battery + "%");
            v.setTextViewText(R.id.detailRam, "RAM FREI   " + ram + " GB");
            v.setTextViewText(R.id.detailStorage, "SPEICHER FREI   " + storage + " GB");
            v.setTextViewText(R.id.detailNetwork, "NETZWERK   " + connection);
            v.setTextViewText(R.id.detailDevice, "GERÄT   " + Build.MANUFACTURER + " " + Build.MODEL);
            v.setTextViewText(R.id.detailTime, "AKTUALISIERT   " + clock);
        }
        manager.updateAppWidget(id, v);
    }
}