package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.MainActivity;

public class WidgetProvider extends AppWidgetProvider
{
	public static String LIST_ITEM_ACTION_CLICKED = "LIST_ITEM_ACTION_CLICKED";
	public static String UPDATE_LIST = "com.appwidget.list.UPDATE_LIST";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		
		Log.e("app widget id - ", appWidgetIds.length+"");

		for (int i = 0; i < appWidgetIds.length; i++)
		{
			Intent svcIntent = new Intent(context, WidgetService.class);
			//svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
			//svcIntent.setData(Uri.parse(svcIntent .toUri(Intent.URI_INTENT_SCHEME)));

			RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget);
			widget.setRemoteAdapter(R.id.words, svcIntent);

			Intent clickIntent = new Intent(context, MainActivity.class);
			PendingIntent clickPI = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			widget.setPendingIntentTemplate(R.id.words, clickPI);
			
			
			Intent updateBTNIntent= new Intent(context, WidgetProvider.class);
			updateBTNIntent.setAction(UPDATE_LIST);
            PendingIntent pendingIntentRefresh = PendingIntent.getBroadcast(context,0, updateBTNIntent, 0);
            widget.setOnClickPendingIntent(R.id.update_list, pendingIntentRefresh);
            
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}

		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		super.onReceive(context, intent);

		if(intent.getAction().equalsIgnoreCase(UPDATE_LIST))
			updateWidget(context);

		Log.d("WidgetProvider", "onReceive");
	}
	
	private void updateWidget(Context context)
	{
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	    int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
	    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.words);
	}
}