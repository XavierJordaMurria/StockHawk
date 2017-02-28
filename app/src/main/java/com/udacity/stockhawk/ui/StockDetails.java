package com.udacity.stockhawk.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.common.base.Strings;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by xj1 on 20/02/2017.
 */

public class StockDetails extends Activity
{
    // The name of the extra data sent through an {@link Intent}.
    public final static String KEY_EXTRA_MESSAGE = "com.stockhawk.MESSAGE";
    private ArrayList<Entry> entries = new ArrayList<>();
    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.stock_details);

        // Get the message from the Intent.
        Intent intent = getIntent();
        String message = Strings.nullToEmpty(intent.getStringExtra(KEY_EXTRA_MESSAGE));

        lineChart = (LineChart) findViewById(R.id.chart);
        // Show message.
        getCursorAtPosition(1);
    }

    /**
     * Creates an {@link Intent} for {@link StockDetails} with the message to be displayed.
     * @param context the {@link Context} where the {@link Intent} will be used
     * @param message a {@link String} with text to be displayed
     * @return an {@link Intent} used to start {@link StockDetails}
     */
    static protected Intent newStartIntent(Context context, String message)
    {
        Intent newIntent = new Intent(context, StockDetails.class);
        newIntent.putExtra(KEY_EXTRA_MESSAGE, message);
        return newIntent;
    }

    public Cursor getCursorAtPosition(int dataPosition)
    {
        // Run query
        Uri uri = Contract.Quote.URI;
        String[] projection = new String[] {Contract.Quote.COLUMN_HISTORY};

        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;

        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        String tmp1;

        if (cursor != null)
        {
            cursor.moveToPosition(dataPosition);
            int symbolColumn = cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY);
            tmp1 = cursor.getString(symbolColumn);

            String[] splitArr = tmp1.split("\n");

            ArrayList<String> labels = new ArrayList<String>();

            int index = 0;
            for(String pair : splitArr)
            {
//                getDate(Long.valueOf(pair.split(", ")[1]), "dd/MM/yyyy hh:mm:ss.SSS")
//                entries.add(new Entry(Float.valueOf(pair.split(", ")[0]), Long.valueOf(pair.split(", ")[1])));
                entries.add(new Entry(Float.valueOf(pair.split(", ")[1]), index));
                labels.add(getDate(Long.valueOf(pair.split(", ")[0]), "dd/MM/yyyy hh:mm"));
                index++;
            }

            LineDataSet dataset = new LineDataSet(entries, "# of Calls");

            LineData data = new LineData(labels, dataset);
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            dataset.setDrawCubic(false);
            dataset.setDrawFilled(false);

            lineChart.setData(data);
            lineChart.animateY(5000);
        }


        return cursor;
    }

    /**
     * Return date in specified format.
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
