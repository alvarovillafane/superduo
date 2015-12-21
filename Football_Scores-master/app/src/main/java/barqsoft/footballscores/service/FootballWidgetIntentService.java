package barqsoft.footballscores.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.RemoteViews;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.widget.FSWidgetProvider;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class FootballWidgetIntentService extends IntentService {

    private static final String [] SCORES_COLUMNS = {
            DatabaseContract.ScoresEntry.MATCH_ID,
            DatabaseContract.ScoresEntry.DATE_COL,
            DatabaseContract.ScoresEntry.HOME_COL,
            DatabaseContract.ScoresEntry.HOME_GOALS_COL,
            DatabaseContract.ScoresEntry.AWAY_COL,
            DatabaseContract.ScoresEntry.AWAY_GOALS_COL,
            DatabaseContract.ScoresEntry.LEAGUE_COL,
            DatabaseContract.ScoresEntry.TIME_COL
    };

    private static final int INDEX_MATCH_ID = 0;
    private static final int INDEX_DATE_COL = 1;
    private static final int INDEX__HOME_COL = 2;
    private static final int INDEX_HOME_GOALS_COL = 3;
    private static final int INDEX_AWAY_COL = 4;
    private static final int INDEX_AWAY_GOALS_COL = 5;
    private static final int INDEX_LEAGUE_COL = 6;
    private static final int INDEX_TIME_COL = 7;



    public FootballWidgetIntentService() {
        super("FootballWidgetIntentService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                FSWidgetProvider.class));

        Uri allMatches = DatabaseContract.BASE_CONTENT_URI;
        Uri matchesPlayed = DatabaseContract.ScoresEntry.buildURIWithScore();
        Cursor data = getContentResolver().query(matchesPlayed, SCORES_COLUMNS, null, null,
                DatabaseContract.ScoresEntry.DATE_COL + " DESC, "
                        + DatabaseContract.ScoresEntry.TIME_COL + " DESC");

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        int matchId = data.getInt(INDEX_MATCH_ID);
        String dateMatch = data.getString(INDEX_DATE_COL);
        String homeTeam = data.getString(INDEX__HOME_COL);
        String homeScore = data.getString(INDEX_HOME_GOALS_COL);
        String awayTeam = data.getString(INDEX_AWAY_COL);
        String awayScore = data.getString(INDEX_AWAY_GOALS_COL);
        int leagueMatch = data.getInt(INDEX_LEAGUE_COL);
        int timeMatch = data.getInt(INDEX_TIME_COL);
        data.close();


        for (int appWidgetId : appWidgetIds) {
            // Find the correct layout based on the widget's width
            int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
            int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_default_width);
            int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_large_width);
            int layoutId = R.layout.widget_football_scores;

            // TODO: 20/12/2015 Implement largeWidth for next Version

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setTextViewText(R.id.widget_home_team, homeTeam);
            views.setTextViewText(R.id.widget_away_team, awayTeam);
            views.setTextViewText(R.id.widget_score, homeScore + " - " + awayScore);
            views.setTextViewText(R.id.widget_home_score, homeScore);
            views.setTextViewText(R.id.widget_away_score, awayScore);
            views.setTextViewText(R.id.widget_date_match, dateMatch);
            views.setTextViewText(R.id.widget_time_match, String.valueOf(timeMatch));

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_default_width);
    }


}
