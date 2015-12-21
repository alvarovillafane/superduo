package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.FootballWidgetIntentService;
import barqsoft.footballscores.service.MyFetchService;

/**
 * Implementation of App Widget functionality.
 */
public class FSWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, FootballWidgetIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context,@NonNull Intent intent) {
        super.onReceive(context, intent);
        if( MyFetchService.ACTION_DATA_UPDATED.equals(intent.getAction()) ){
            context.startService(new Intent(context, FootballWidgetIntentService.class));
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, FootballWidgetIntentService.class));
    }

}

