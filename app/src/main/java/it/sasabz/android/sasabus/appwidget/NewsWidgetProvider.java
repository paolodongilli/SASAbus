package it.sasabz.android.sasabus.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.RemoteViews;

import it.sasabz.android.sasabus.R;

/**
 * An app widget provider (widgets on the home screen pages) for the news delivered from Sasa SpA-AG.
 *
 * @author David Dejori
 */
public class NewsWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_news);

            views.setTextViewText(R.id.test1, Html.fromHtml("<b>Bozen:</b> Umleitung der Linien 7A und 7B"));
            views.setTextViewText(R.id.test2, Html.fromHtml("<b>Bozen:</b> Umleitung der Linien 10A und 10B"));
            views.setTextViewText(R.id.test3, Html.fromHtml("<b>Bozen:</b> Umleitung der Linien 5, 6 und 9"));

            Intent intent = new Intent(context, NewsWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

            // update all widgets
            // intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            // update this widget only
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget_news_refresh, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}