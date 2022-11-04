package com.example.mysensorapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class LineChart_ratingday extends AppCompatActivity {

    private StatsData statsData;
    LineChart mpLineChart;
    TextView t,x,y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart_time);

        mpLineChart = (LineChart) findViewById(R.id.line_chart);

        t = findViewById(R.id.textLineChart);
        t.setText("Your Ratings per day");

        x = findViewById(R.id.xTitle);
        x.setText("Days");
        y = findViewById(R.id.yTitle);
        y.setText("Ratings");

        statsData = new StatsData();
        statsData.getRatingDayStats();
        loadLineChartDataThread();

       /* XAxis xAxis = mpLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //to hide right Y and top X border
        YAxis rightYAxis = mpLineChart.getAxisRight();
        rightYAxis.setEnabled(false);
        YAxis leftYAxis = mpLineChart.getAxisLeft();
        leftYAxis.setEnabled(true);
        XAxis topXAxis = mpLineChart.getXAxis();
        topXAxis.setEnabled(true);*/


    }

    private void loadLineChartData() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        List<String> xAxisValues = dataDays();
        LineDataSet lineDataSet = new LineDataSet(dataValues1(),"Daily user sleep quality");
        dataSets.add(lineDataSet);


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

        mpLineChart.getDescription().setEnabled(false);
        lineDataSet.setLineWidth(3);
        lineDataSet.setColor(Color.BLACK);
        lineDataSet.setValueTextSize(10);

        xAxis.setLabelCount(lineDataSet.getEntryCount(), true);
    }

    // Thread para mostrar os dados quando estiverem disponiveis
    public void loadLineChartDataThread() {
        new Thread() {
            public void run() {
                while (true) {
                    if(statsData.ratingDayStats != null) {
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
        ArrayList<Entry> dataVals = new ArrayList<>();
        int counter=0;
        for(RatingDayStats as : statsData.ratingDayStats) {
            //System.out.println(as.getDay());
            dataVals.add(new Entry(counter, as.getRating()));
            counter++;
        }
        return  dataVals;
    }

    private ArrayList<String> dataDays(){
        ArrayList<String> dataVals = new ArrayList<String>();
        for(RatingDayStats as : statsData.ratingDayStats) {
            System.out.println(as.getDay());
            dataVals.add(as.getDay() +"/" + as.getMonth());
        }
        return  dataVals;
    }


}