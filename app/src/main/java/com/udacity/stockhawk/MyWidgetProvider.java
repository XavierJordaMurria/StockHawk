package com.udacity.stockhawk;

import java.util.HashMap;
import java.util.Random;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.sync.QuoteSyncJob;

public class MyWidgetProvider extends AppWidgetProvider
{
    private static final String ACTION_CLICK = "ACTION_CLICK";
    private HashMap<String, String> map = new HashMap<>();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds)
    {
        // Get all ids
        int[] allWidgetIds = getWidgetIds(context);

        for (int widgetId : allWidgetIds)
        {
            // create some random data
            int number = (new Random().nextInt(100));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);
            Log.w("WidgetExample", String.valueOf(number));
            // Set the text
//            remoteViews.setTextViewText(R.id.update, String.valueOf(number));

            // Register an onClickListener
            Intent intent = new Intent(context, MyWidgetProvider.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.stockValue, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if (action != null && action.equals(QuoteSyncJob.ACTION_DATA_UPDATED))
        {
            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            int[] appWidgetId = getWidgetIds(context);

            final int N = appWidgetId.length;

            if (N < 1)
            {
                return ;
            }
            else
            {
                int id = appWidgetId[N-1];
//                updateWidget(context, appWidgetManager, id ,name , value);
            }
        }

        else {
            super.onReceive(context, intent);
        }


        // Run query
        Uri uri = Contract.Quote.URI;
        String[] projection = new String[] {Contract.Quote.COLUMN_SYMBOL,
                Contract.Quote.COLUMN_PRICE};

        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null)
        {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++)
            {
                map.put(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Quote.COLUMN_SYMBOL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Contract.Quote.COLUMN_PRICE)));
                cursor.moveToNext();
            }

            // always close the cursor
            cursor.close();
        }
    }

    static void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String title, int value)
    {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.stockName, title);
        views.setTextViewText(R.id.stockValue, Integer.toString(value));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private int[] getWidgetIds(Context context)
    {
        ComponentName thisWidget = new ComponentName(context,
                MyWidgetProvider.class);

        return AppWidgetManager.getInstance(context).getAppWidgetIds(thisWidget);
    }


}