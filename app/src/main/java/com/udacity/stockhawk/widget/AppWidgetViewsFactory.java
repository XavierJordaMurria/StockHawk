package com.udacity.stockhawk.widget;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.StockDetails;

public class AppWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory
{
	private Context context = null;
	//private int appWidgetId;
	private ArrayList<DBItem> arrayList = new ArrayList<>();

	private class DBItem
	{
		public final String mSymbol;
		public final String mPrice;

		public DBItem(String symbol, String price)
		{
			mSymbol = symbol;
			mPrice = price;
		}
	}

	public AppWidgetViewsFactory(Context ctxt, Intent intent)
	{
		this.context = ctxt;
		/*appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		Log.e(getClass().getSimpleName(), appWidgetId + "");*/
	}

	@Override
	public void onCreate()
	{
//		arrayList.add("1");
		getDataFromDB();
	}


	@Override
	public void onDestroy()
	{
		// no-op
	}

	@Override
	public int getCount()
	{
		return (arrayList.size());
	}

	@Override
	public RemoteViews getViewAt(int position)
	{
		RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.widget_row);

		String tmpItemStr = arrayList.get(position).mSymbol + ": $" + arrayList.get(position).mPrice;
		row.setTextViewText(android.R.id.text1, tmpItemStr);

		Log.w("AppWidgetViewsFactory", "getViewAt with position: " + position + " and " + arrayList.get(position).mSymbol);
		Intent i = new Intent();
		i.putExtra(StockDetails.KEY_EXTRA_MESSAGE, arrayList.get(position).mSymbol);
		i.putExtra(StockDetails.EXTRA_POSITION, position);
		i.setAction(WidgetProvider.LIST_ITEM_ACTION_CLICKED);
		row.setOnClickFillInIntent(android.R.id.text1, i);

		return row;
	}

	@Override
	public RemoteViews getLoadingView()
	{
		return null;
	}

	@Override
	public int getViewTypeCount()
	{
		return 1;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public boolean hasStableIds()
	{
		return true;
	}

	@Override
	public void onDataSetChanged()
	{
		// Revert back to our process' identity so we can work with our
		// content provider
		final long identityToken = Binder.clearCallingIdentity();

		getDataFromDB();

		// Restore the identity - not sure if it's needed since we're going
		// to return right here, but it just *seems* cleaner
		Binder.restoreCallingIdentity(identityToken);
	}

	private void getDataFromDB()
	{
		//clean all elements, don't want to add them twice.
		arrayList.clear();

		// Run query
		Uri uri = Contract.Quote.URI;
		String[] projection = new String[] {Contract.Quote.COLUMN_SYMBOL,
				Contract.Quote.COLUMN_PRICE};

		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;

		Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

		DBItem tmpItem;
		if (cursor != null)
		{
			cursor.moveToFirst();
			for (int i = 0; i < cursor.getCount(); i++)
			{
				tmpItem = new DBItem(cursor.getString(cursor.getColumnIndexOrThrow(Contract.Quote.COLUMN_SYMBOL)),
							cursor.getString(cursor.getColumnIndexOrThrow(Contract.Quote.COLUMN_PRICE)));

				arrayList.add(tmpItem);
				cursor.moveToNext();
			}
		}

			// always close the cursor
			cursor.close();
	}
}