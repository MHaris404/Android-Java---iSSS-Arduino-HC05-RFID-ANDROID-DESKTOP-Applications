package com.example.isss;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.glxn.qrgen.android.QRCode;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class activity_qrcode extends AppCompatActivity {
    ImageView myImage;
    ListView qrCode_listview;
    TextView txt_slide, txt_qrCode_totalPrice;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseReference;
    ArrayAdapter adapter;
    private BottomSheetBehavior bottomSheetBehavior;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        qrCode_listview = findViewById(R.id.listview_qrCode);
        txt_slide = findViewById(R.id.txt_slide);
        txt_qrCode_totalPrice = findViewById(R.id.txt_qrCode_totalPrice);
        myImage = (ImageView) findViewById(R.id.imageView_qrCode);

        //
        txt_qrCode_totalPrice.setText(String.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("TOTALITY", -2)));

        //
        Bundle extras = getIntent().getExtras();
        String[] array = extras.getStringArray("final2");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, array) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.LTGRAY);
                return view;
            }
        };
        qrCode_listview.setAdapter(adapter);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int state) {
                if (state == 4) {
                    txt_slide.setText("Slide up for Full View");
                } else if (state == 3) {
                    txt_slide.setText("Slide down for Collapsed View");
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseReference = firebaseDatabase.getReference("Bills");

        int n = nDigitRandomNo(7);
        Bitmap myBitmap = QRCode.from(String.valueOf(n)).withSize(300, 300).withColor(0xFF01031E, 0xFFFFFFFF).bitmap(); //hexa
        List<String> list = Arrays.asList(array);
        firebaseReference.child(String.valueOf(n)).setValue(list);
        myImage.setImageBitmap(myBitmap);

        qrCode_listview.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
//                        Toast.makeText(activity_qrcode.this, "down", Toast.LENGTH_SHORT).show();
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow NestedScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);

//                        Toast.makeText(activity_qrcode.this, "up", Toast.LENGTH_SHORT).show();
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    private int nDigitRandomNo(int digits) {
        int max = (int) Math.pow(10, (digits)) - 1; //for digits =7, max will be 9999999
        int min = (int) Math.pow(10, digits - 1); //for digits = 7, min will be 1000000
        int range = max - min; //This is 8999999
        Random r = new Random();
        int x = r.nextInt(range);// This will generate random integers in range 0 - 8999999
        int nDigitRandomNo = x + min; //Our random rumber will be any random number x + min
        return nDigitRandomNo;
    }

}