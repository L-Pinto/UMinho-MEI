package com.example.mysensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Rating_StatsActivity extends AppCompatActivity {
    // variable for our bar chart
    BarChart barChart;

    // variable for our bar data.
    BarData barData;

    // variable for our bar data set.
    BarDataSet barDataSet;

    // array list for storing entries.
    ArrayList barEntriesArrayList;

    // Stats Data stored in firebase
    private StatsData statsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_stats); // xml file name

        // initializing variable for bar chart.
        barChart = findViewById(R.id.idBarChart);


        statsData = new StatsData();
        statsData.getRatingCounterStats();

        loadLineChartDataThread();
    }


    public void loadBarChartData() {
        // calling method to get bar entries.
        getBarEntries();

        // creating a new bar data set.
        barDataSet = new BarDataSet(barEntriesArrayList, "Rating Distribution");

        barDataSet.setValueFormatter(new StackedValueFormatter(false,"",0));
        //barChart.getXAxis().setValueFormatter(new StackedValueFormatter(false,"suffix",0));


        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // adding color to our bar data set.
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis =  barChart.getXAxis();
        //xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis rightYAxis =  barChart.getAxisRight();

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);

        rightYAxis.setEnabled(false);
        barChart.setDrawGridBackground(false);

    }

    // Thread para mostrar os dados quando estiverem disponiveis
    public void loadLineChartDataThread() {
        new Thread() {
            public void run() {
                while (true) {
                    if(statsData.ratingCounterStats != null) {
                        runOnUiThread(() -> loadBarChartData());
                        break;
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.home_button) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getBarEntries() {
        // creating a new array list
        barEntriesArrayList = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntriesArrayList.add(new BarEntry(1f, statsData.ratingCounterStats.getR00()));
        barEntriesArrayList.add(new BarEntry(2f, statsData.ratingCounterStats.getR05()));
        barEntriesArrayList.add(new BarEntry(3f, statsData.ratingCounterStats.getR10()));
        barEntriesArrayList.add(new BarEntry(4f, statsData.ratingCounterStats.getR15()));
        barEntriesArrayList.add(new BarEntry(5f, statsData.ratingCounterStats.getR20()));
        barEntriesArrayList.add(new BarEntry(6f, statsData.ratingCounterStats.getR25()));
        barEntriesArrayList.add(new BarEntry(7f, statsData.ratingCounterStats.getR30()));
        barEntriesArrayList.add(new BarEntry(8f, statsData.ratingCounterStats.getR35()));
        barEntriesArrayList.add(new BarEntry(9f, statsData.ratingCounterStats.getR40()));
        barEntriesArrayList.add(new BarEntry(10f,statsData.ratingCounterStats.getR45()));
        barEntriesArrayList.add(new BarEntry(11f,statsData.ratingCounterStats.getR50()));
    }




}