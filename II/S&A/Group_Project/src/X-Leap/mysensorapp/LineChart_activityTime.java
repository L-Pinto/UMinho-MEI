package com.example.mysensorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LineChart_activityTime extends AppCompatActivity {

    private StatsData statsData;
    LineChart mpLineChart;
    TextView t;
    TextView x, y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart_time);

        mpLineChart = (LineChart) findViewById(R.id.line_chart);
        t = findViewById(R.id.textLineChart);
        t.setText("Your Phone Activity");

        x = findViewById(R.id.xTitle);
        x.setText("Days");

        y = findViewById(R.id.yTitle);
        y.setText("Activity Time 2h");

        statsData = new StatsData();
        statsData.getActivityStats();
        loadLineChartDataThread();

    }


    private void loadLineChartData() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();


        List<String> xAxisValues = dataDays();
        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(),"Daily Activity Time");
        dataSets.add(lineDataSet1);

        mpLineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));
        LineData data = new LineData(dataSets);
        mpLineChart.setData(data);
        //mpLineChart.invalidate();

        XAxis xAxis = mpLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis rightYAxis = mpLineChart.getAxisRight();

        mpLineChart.getXAxis().setDrawGridLines(false);
        mpLineChart.getAxisLeft().setDrawGridLines(false);
        mpLineChart.getAxisRight().setDrawGridLines(false);

        rightYAxis.setEnabled(false);

        mpLineChart.setDrawGridBackground(false);
        mpLineChart.setBackgroundColor(Color.WHITE);
        lineDataSet1.setLineWidth(3);
        lineDataSet1.setColor(Color.BLACK);
        lineDataSet1.setValueTextSize(10);
        mpLineChart.getDescription().setEnabled(false);

        xAxis.setLabelCount(lineDataSet1.getEntryCount(), true);

    }

    // Thread para mostrar os dados quando estiverem disponiveis
    public void loadLineChartDataThread() {
        new Thread() {
            public void run() {
                while (true) {
                    if(statsData.activityStats != null) {
                        runOnUiThread(() -> loadLineChartData());
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

    private ArrayList<Entry> dataValues1(){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        int counter = 0;
        for(ActivityStats as : statsData.activityStats) {
            System.out.println(as.getDay());
            dataVals.add(new Entry(counter,as.getActivitytime()));
            counter++;
        }

        return  dataVals;
    }

    private ArrayList<String> dataDays(){
        ArrayList<String> dataVals = new ArrayList<String>();
        for(ActivityStats as : statsData.activityStats) {
            System.out.println(as.getDay());
            dataVals.add(as.getDay() +"/" + as.getMonth());
        }
        return  dataVals;
    }
}