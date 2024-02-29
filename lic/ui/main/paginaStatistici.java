package com.dam.lic.ui.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dam.lic.R;
import com.dam.lic.ReadWriteCommandDetails;
import com.dam.lic.Stare;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class paginaStatistici extends Fragment {
   BarChart barChart;
   PieChart pieChart;
   Button btnDescarcaCsv;
   List<BarEntry> listaEntrySetBar= new ArrayList<BarEntry>();
   List<ReadWriteCommandDetails> listaComenzi = new ArrayList<>();
   String[] xAxisLables = new String[365];
   TextView faraDate, tvPie,tvBar;

   String uid;
    int i = 0;
   SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate =inflater.inflate(R.layout.fragment_pagina_statistici, container,false );
        barChart=inflate.findViewById(R.id.barChart);
        pieChart = inflate.findViewById(R.id.pieChart);
        faraDate = inflate.findViewById(R.id.tvFaraDate);
        tvBar= inflate.findViewById(R.id.tvBar);
        tvPie= inflate.findViewById(R.id.tvPie);
        btnDescarcaCsv = inflate.findViewById(R.id.butonDescarcaCSV);
        barChart.getDescription().setEnabled(true);
        pieChart.getDescription().setEnabled(true);
        uid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Commands");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaComenzi.clear();
                listaEntrySetBar.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    if (childSnapshot.child("courier").getValue().equals(uid) && ((childSnapshot.child("state").getValue().equals(Stare.INCHEIATA.toString())) || childSnapshot.child("state").getValue().equals(Stare.LIVRATA.toString()))) {

                        ReadWriteCommandDetails comanda = new ReadWriteCommandDetails(Integer.parseInt(childSnapshot.child("noPackages").getValue().toString()),
                                childSnapshot.child("height").getValue().toString(), childSnapshot.child("length").getValue().toString(), childSnapshot.child("width").getValue().toString(), childSnapshot.child("weight").getValue().toString(),
                                (Boolean) childSnapshot.child("fragile").getValue(),
                                childSnapshot.child("senderName").getValue().toString(), childSnapshot.child("senderPhone").getValue().toString(),
                                childSnapshot.child("senderCounty").getValue().toString(), childSnapshot.child("senderLoc").getValue().toString(), childSnapshot.child("senderAddress").getValue().toString(),
                                childSnapshot.child("recipientName").getValue().toString(), childSnapshot.child("recipientPhone").getValue().toString(),
                                childSnapshot.child("recipientCounty").getValue().toString(), childSnapshot.child("recipientLoc").getValue().toString(), childSnapshot.child("recipientAddress").getValue().toString(),
                                childSnapshot.child("sender").getValue().toString(), childSnapshot.child("recipient").getValue().toString(), childSnapshot.child("courier").getValue().toString(),
                                (Boolean) childSnapshot.child("cashPayment").getValue(), Stare.valueOf(childSnapshot.child("state").getValue().toString()), childSnapshot.child("date").getValue().toString(), childSnapshot.child("recipientLat").getValue().toString(), childSnapshot.child("recipientLng").getValue().toString(), childSnapshot.child("senderLat").getValue().toString(), childSnapshot.child("senderLng").getValue().toString(), Float.parseFloat(childSnapshot.child("price").getValue().toString()));
                        comanda.setEnd(childSnapshot.child("end").getValue().toString());
                        listaComenzi.add(comanda);


                    }

                }
                if (listaComenzi.isEmpty()) {
                    tvPie.setVisibility(View.GONE);
                    tvBar.setVisibility(View.GONE);
                    barChart.setVisibility(View.GONE);
                    pieChart.setVisibility(View.GONE);
                    faraDate.setVisibility(View.VISIBLE);
                } else {
                    tvPie.setVisibility(View.VISIBLE);
                    tvBar.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.VISIBLE);
                    pieChart.setVisibility(View.VISIBLE);
                    faraDate.setVisibility(View.GONE);
                    showBarChart();
                    showPieChart();
                    btnDescarcaCsv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                try {
                                    String destination = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                                    String timestamp = String.valueOf(System.currentTimeMillis());
                                    String fileName = "comenzi_" + timestamp + ".csv";
                                    File file = new File(destination,fileName);
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    FileWriter writer=new FileWriter(file);
                                    writer.append("Data,Pret,Plata Cash\n");

                                    for (ReadWriteCommandDetails comanda : listaComenzi) {
                                        String row = comanda.getDate() + "," + comanda.getPrice() + "," + comanda.isCashPayment() + "\n";
                                        writer.append(row);
                                    }

                                    writer.flush();
                                    writer.close();
                                    Toast.makeText(getContext(),"S-a salvat fisierul comenzi.csv",Toast.LENGTH_SHORT).show();


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                int REQUEST_CODE=123;
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                            }
                        }
                    });

                }
            }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        return inflate;

    }

    private void showPieChart() {
        if(listaComenzi.size()>0) {
           int nrCash = (int) listaComenzi.stream().filter(c->c.isCashPayment()).count();
           int nrCard = (int) listaComenzi.stream().filter(c->!(c.isCashPayment())).count();
           List<PieEntry> entries = new ArrayList<>();
           PieEntry pieEntryCash = new PieEntry(nrCash,"Plati efectuate cash");
           PieEntry pieEntryCard = new PieEntry(nrCard, "Plati efectuate cu cardul");
           entries.add(pieEntryCash);
           entries.add(pieEntryCard);


            PieDataSet pieDataSet = new PieDataSet(entries,"(Tipuri de plati)");
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            pieDataSet.setValueTextColor(ColorTemplate.rgb("#ffdc32"));
            pieDataSet.setValueTextSize(20f);
            PieData pieData = new PieData(pieDataSet);
            pieChart.getDescription().setEnabled(false);
            pieChart.setData(pieData);
            pieChart.getLegend().setTextSize(16f);
            pieChart.getLegend().setYOffset(-10f);
            pieChart.getLegend().setTextColor(ColorTemplate.rgb("#ffdc32"));
            pieChart.setHoleColor(ColorTemplate.rgb("#02674b"));
            pieChart.getLegend().setDirection(Legend.LegendDirection.RIGHT_TO_LEFT);
            pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
            barChart.setFocusable(false);
            pieChart.setExtraOffsets(20f,10f,20f,50f);
            pieChart.animateY(3000);
            pieChart.setRotationEnabled(false);


        } else {
            pieChart.getDescription().setText("Nu exista date...");
        }

    }

    private void showBarChart() {



                if(listaComenzi.size()>0)
                {   Map<Date,Double> hartaValori = new HashMap<>();
                    hartaValori = listaComenzi.stream()
                        .collect(Collectors.toMap(command -> {
                                    try {
                                        return dateFormat.parse(command.getEnd());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                },
                                command -> (double) command.getPrice(),
                                Double::sum));

                    Map<Date,Double> sortedMap = new TreeMap<>(hartaValori);
                    for (Date data: sortedMap.keySet())
                    {
                            float yValue = hartaValori.get(data).floatValue();
                            listaEntrySetBar.add(new BarEntry(i,yValue));
                            xAxisLables[i]=dateFormat.format(data);
                            i++;
                    }
                        XAxis xAxis = barChart.getXAxis();
                        xAxis.setLabelCount(listaEntrySetBar.size());
                        IndexAxisValueFormatter xAxisFormatter = new IndexAxisValueFormatter(xAxisLables);
                        xAxis.setValueFormatter(xAxisFormatter);
                       // xAxis.setAxisMinimum(minimum);
                        xAxis.setTextSize(15f);
                        xAxis.setLabelRotationAngle(60);
                        xAxis.setGranularity(1f);
                        xAxis.setTextColor(ColorTemplate.rgb("#ffdc32"));
                        YAxis yAxisl = barChart.getAxisLeft();
                        yAxisl.setTextColor(ColorTemplate.rgb("#ffdc32"));
                        yAxisl.setTextSize(15f);
                        YAxis yAxisr =barChart.getAxisRight();
                        yAxisr.setTextColor(ColorTemplate.rgb("#ffdc32"));
                        yAxisr.setTextSize(15f);



                    BarDataSet dataSet = new BarDataSet(listaEntrySetBar,"PROFIT PE ZI");
                    dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
                   dataSet.setValueTextColor(ColorTemplate.rgb("#ffdc32"));
                    dataSet.setValueTextSize(16f);


                    BarData barData = new BarData(dataSet);
                    barChart.setData(barData);
                    barChart.getLegend().setTextColor(ColorTemplate.rgb("#ffdc32"));
                    barChart.getLegend().setTextSize(20f);
                    barChart.getLegend().setYOffset(30f);
                    barChart.getLegend().setXOffset(-30f);
                    barChart.setExtraOffsets(50f,20f,50f,30f);
                    barChart.setFocusable(false);
                    barChart.getDescription().setEnabled(false);
                //.setText("Chart-ul de mai sus prezintă evoluția profitului dvs pe parcursul zilelor.\nProfitul este reprezentat pe axa verticală, în timp ce pe axa orizontală sunt afișate zilele.");
                    barChart.animateY(3000);


                }
                else
                {
                    barChart.getDescription().setText("Nu exista date...");
                }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
