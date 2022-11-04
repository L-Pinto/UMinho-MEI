package com.example.mysensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

public class StatsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TextView daymonth;
    private StatsData statsData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        statsData = new StatsData();
        statsData.getSleepStats();
        System.out.println(statsData.sleepStats);
        pieChart = findViewById(R.id.pieChart);
        daymonth = findViewById(R.id.daymonth);
        setupPieChart();
        loadPieChartDataThread();
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Sleep Statistics");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);

       /* Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);*/
    }

    private void loadPieChartData() {
        if (statsData.sleepStats != null) {
            Calendar cal = Calendar.getInstance();
            String s = statsData.sleepStats.day + "/" + statsData.sleepStats.month + "/"+ cal.get(Calendar.YEAR);
            daymonth.setText(s);
            ArrayList<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(statsData.sleepStats.deepsleep, "Deep Sleep"));
            entries.add(new PieEntry(statsData.sleepStats.lightsleep, "Light Sleep"));
            if(statsData.sleepStats.rem != 0) entries.add(new PieEntry(statsData.sleepStats.rem, "REM"));
            if(statsData.sleepStats.awake != 0) entries.add(new PieEntry(statsData.sleepStats.awake, "Awake"));

            ArrayList<Integer> colors = new ArrayList<>();
            for (int color: ColorTemplate.JOYFUL_COLORS) {
                colors.add(color);
            }

            for (int color: ColorTemplate.MATERIAL_COLORS) {
                colors.add(color);
            }

            PieDataSet dataSet = new PieDataSet(entries, "Expense Category");
            dataSet.setColors(colors);

            PieData data = new PieData(dataSet);
            data.setDrawValues(true);
            data.setValueFormatter(new PercentFormatter(pieChart));
            data.setValueTextSize(12f);
            data.setValueTextColor(Color.BLACK);

            pieChart.setData(data);
            pieChart.invalidate();

            pieChart.animateY(1400, Easing.EaseInOutQuad);
        }
    }
    // Thread para mostrar os dados quando estiverem disponiveis
    public void loadPieChartDataThread() {
        new Thread() {
            public void run() {
                while (true) {
                    if(statsData.sleepStats != null) {
                        runOnUiThread(() -> loadPieChartData());
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
}