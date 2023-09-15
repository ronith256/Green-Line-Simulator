package com.lucario.greenline;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class ColorPickerDialog extends AppCompatDialogFragment {
    private SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    private int selectedRedValue, selectedGreenValue, selectedBlueValue;
    private View colorPreview;

    private ColorPickerListener colorPickerListener;

    public ColorPickerDialog(ColorPickerListener listener) {
        this.colorPickerListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_color_picker, null);
        colorPreview = view.findViewById(R.id.colorPreview);
        redSeekBar = view.findViewById(R.id.redSeekBar);
        greenSeekBar = view.findViewById(R.id.greenSeekBar);
        blueSeekBar = view.findViewById(R.id.blueSeekBar);

        redSeekBar.setMax(255);
        greenSeekBar.setMax(255);
        blueSeekBar.setMax(255);

        redSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        greenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        blueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        builder.setView(view)
                .setTitle("Color Picker");
        return builder.create();
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (seekBar == redSeekBar) {
                    selectedRedValue = progress;
                } else if (seekBar == greenSeekBar) {
                    selectedGreenValue = progress;
                } else if (seekBar == blueSeekBar) {
                    selectedBlueValue = progress;
                }

                updateSelectedColor();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Do nothing
        }
    };

    private void updateSelectedColor() {
        int color = Color.rgb(selectedRedValue, selectedGreenValue, selectedBlueValue);
        colorPreview.setBackgroundColor(color);
        colorPickerListener.onColorSelected(color);
    }

    public interface ColorPickerListener {
        void onColorSelected(int color);
    }
}