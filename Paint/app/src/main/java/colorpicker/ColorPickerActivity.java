package colorpicker;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


import android.widget.Button;
import android.widget.Toast;

import com.example.lucas.paint.R;


public class ColorPickerActivity extends Activity {

    private ColorPicker colorPicker;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);

        colorPicker = (ColorPicker) findViewById(R.id.colorPicker);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int colorSelected = colorPicker.getColor();

            }
        });

    }

}