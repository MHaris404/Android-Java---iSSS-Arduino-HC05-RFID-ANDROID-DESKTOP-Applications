package com.example.isss;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class dialog_location extends DialogFragment {
    ViewPager viewPager;
    TabLayout tabLayout;
    MyPagerAdapter myPagerAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_location, null);

        builder.setView(view)
                .setTitle("Location")
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        builder.setCancelable(false);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager, true);

        activity_scannedProduct scannedProduct = (activity_scannedProduct) getActivity();
        ArrayList<String> objectArrayList = scannedProduct.view_location();
        myPagerAdapter = new MyPagerAdapter(getContext(), objectArrayList);
        viewPager.setAdapter(myPagerAdapter);

        TextView txt_curr = view.findViewById(R.id.txt_current);
        TextView txt_curr0 = view.findViewById(R.id.txt_current0);
        TextView txt_next = view.findViewById(R.id.txt_next);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pageText(position, txt_curr, txt_curr0, txt_next, objectArrayList);
            }

            @Override
            public void onPageSelected(int position) {
                pageText(position, txt_curr, txt_curr0, txt_next, objectArrayList);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return builder.create();
    }

    private void pageText(int position, TextView txt_curr, TextView txt_curr0, TextView txt_next, ArrayList<String> objectArrayList) {
        switch (position) {
            case 0:
                txt_curr.setText("Entrance");
                txt_next.setText(objectArrayList.get(1));
                break;
            case 1:
                String a = objectArrayList.get(1);
                txt_curr.setText(a);
                txt_next.setText(objectArrayList.get(2));
                break;
            case 2:
                String ab = objectArrayList.get(3);
                txt_curr.setText(objectArrayList.get(2));
                txt_next.setText(ab + " shelf");
                break;
            case 3:
                String abc = objectArrayList.get(3);
                txt_curr.setText(abc + " shelf");
                txt_next.setText(objectArrayList.get(4));
                break;
            case 4:
                txt_curr.setText(objectArrayList.get(4));
                txt_next.setText(objectArrayList.get(5));
                break;
            case 5:
                txt_curr.setText(objectArrayList.get(5));
                break;
        }
    }
}
