package de.systemglass.poco;
import android.appwidget.*;import android.content.*;import android.app.*;import android.os.*;import android.net.*;import android.widget.*;import java.text.*;import java.util.*;
public class GlassWidget extends AppWidgetProvider {
public void onUpdate(Context c,AppWidgetManager m,int[] ids){for(int id:ids)render(c,m,id);}
static void render(Context c,AppWidgetManager m,int id){
BatteryManager b=(BatteryManager)c.getSystemService(Context.BATTERY_SERVICE);int battery=b.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
ActivityManager am=(ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();am.getMemoryInfo(mi);
StatFs fs=new StatFs(Environment.getDataDirectory().getPath());long free=fs.getAvailableBytes()/1073741824L;
ConnectivityManager cm=(ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);Network n=cm.getActiveNetwork();NetworkCapabilities cap=n==null?null:cm.getNetworkCapabilities(n);
String net=cap==null?"Offline":cap.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)?"WLAN":cap.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)?"Mobilfunk":"Verbunden";
RemoteViews v=new RemoteViews(c.getPackageName(),R.layout.widget);
v.setTextViewText(R.id.stats,"Akku: "+battery+"%    RAM: "+String.format(Locale.GERMANY,"%.1f",mi.availMem/1073741824.0)+" GB frei\nSpeicher: "+free+" GB frei    Netz: "+net);
v.setTextViewText(R.id.footer,Build.MANUFACTURER+" "+Build.MODEL+" · "+new SimpleDateFormat("HH:mm",Locale.GERMANY).format(new Date()));
m.updateAppWidget(id,v);
}}