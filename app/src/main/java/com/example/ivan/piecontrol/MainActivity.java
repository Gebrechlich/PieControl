package com.example.ivan.piecontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private PieControl pieControl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pieControl = (PieControl)findViewById(R.id.pie1);

        ArrayList<PieControl.SectionParameters> sectionsParams = new ArrayList<>();
        sectionsParams.add(new PieControl.SectionParameters(R.drawable.cat_laptop_bw, R.drawable.cat_laptop));
        sectionsParams.add(new PieControl.SectionParameters(R.drawable.cat_paper_bw, R.drawable.cat_paper));
        sectionsParams.add(new PieControl.SectionParameters(R.drawable.cat_pirate_bw, R.drawable.cat_pirate));
        sectionsParams.add(new PieControl.SectionParameters(R.drawable.cat_rascal_bw, R.drawable.cat_rascal));
        sectionsParams.add(new PieControl.SectionParameters(R.drawable.cat_sing_bw, R.drawable.cat_sing));

        pieControl.setSectiondsParams(sectionsParams);
        pieControl.setChecked(0);

        pieControl.setOnCheckedChangeListener(new PieControl.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(int id) {
                Log.d("Pie", "Checked id " + id);
            }
        });
    }
}
