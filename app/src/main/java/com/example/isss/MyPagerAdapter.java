package com.example.isss;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class MyPagerAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> objectArrayList;
    LayoutInflater layoutInflater;
    int[] images = new int[6];

    public MyPagerAdapter(Context context, ArrayList<String> objectArrayList) {
        this.context = context;
        this.objectArrayList = objectArrayList;

        images[0] = R.drawable.ic_11entrance;

        if (objectArrayList.get(1).toLowerCase().equals("left")) {
            images[1] = R.drawable.ic_4left;
        } else if (objectArrayList.get(1).toLowerCase().equals("right")) {
            images[1] = R.drawable.ic_2right;
        }
        images[2] = R.drawable.ic_12aisle1;

        if (objectArrayList.get(3).toLowerCase().equals("top")) {
            images[3] = R.drawable.ic_6top;
        } else if (objectArrayList.get(3).toLowerCase().equals("center")) {
            images[3] = R.drawable.ic_7centre;
        } else if (objectArrayList.get(3).toLowerCase().equals("bottom")) {
            images[3] = R.drawable.ic_8bottom;
        }

        if (objectArrayList.get(4).toLowerCase().equals("partition1")) {
            images[4] = R.drawable.ic_9partition1;
        } else if (objectArrayList.get(4).toLowerCase().equals("partition2")) {
            images[4] = R.drawable.ic_9partition2;
        }

        images[5] = R.drawable.ic_10product;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.location_item, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setImageResource(images[position]);

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}