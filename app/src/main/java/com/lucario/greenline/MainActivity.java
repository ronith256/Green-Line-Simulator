package com.lucario.greenline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity implements ColorPickerDialog.ColorPickerListener{
    private SeekBar lineWidthSeekBar;
    private View colorPreview;
    private SwitchMaterial flicker, enableRBG;
    private AppCompatButton startButton;
    public static int lineWidth = 10;
    public static int color = Color.GREEN;
    public static boolean flickerB = false;
    public static boolean isRGB = false;
    private final int REQUEST_OVERLAY_PERMISSION = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lineWidthSeekBar = findViewById(R.id.seekBarLineWidth);
        colorPreview = findViewById(R.id.preview);
        flicker = findViewById(R.id.flickerSwitch);
        enableRBG = findViewById(R.id.rgbSwitch);
        startButton = findViewById(R.id.buttonStart);

        flicker.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                flickerB = true;
            } else {
                flickerB = false;
            }
        });

        enableRBG.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if(isChecked){
                isRGB = true;
            } else {
                isRGB = false;
            }
        });

        lineWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lineWidth = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        colorPreview.setOnClickListener(e->{
            ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this);
            colorPickerDialog.show(getSupportFragmentManager(), "colorPickerDialog");
        });

        startButton.setOnClickListener(e->{
            Intent intent = new Intent(this, DrawLineService.class);
            intent.putExtra("color", color).putExtra("line-width", lineWidth);
            if(startButton.getText().toString().equals("Start")){
                startButton.setText("Stop");
                if (!Settings.canDrawOverlays(this)) {
                    Intent set = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
                } else {
                    startService(intent);
                }
            } else {
                startButton.setText("Start");
                stopService(intent);
            }
        });

    }

    @Override
    public void onColorSelected(int color) {
        colorPreview.setBackgroundColor(color);
        MainActivity.color = color;
        System.out.println(color);
    }
}