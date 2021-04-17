package com.example.isss;

import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    public Map<String, Object> mData;
    public ArrayList<Pair<String, Object>> mActualData;
    int total_PRICE = 0;

    public MyAdapter(Map<String, Object> mData) {
        this.mData = mData;
        mActualData = new ArrayList<Pair<String, Object>>(mData.size());
        for (Map.Entry<String, Object> entry : mData.entrySet()) {
            mActualData.add(new Pair<String, Object>(entry.getKey(), entry.getValue()));
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTextViewId.setText(mActualData.get(position).first);

        String rawData = mData.values().toArray()[position].toString();
        String price = rawData.substring((rawData.indexOf("=") + 1), (rawData.indexOf(",")));
        holder.mTextViewPrice.setText(price);

        //
        total_PRICE = total_PRICE + Integer.parseInt(price);
        Intent intent = new Intent("custom-message");
        intent.putExtra("TOTALITY", total_PRICE);
        LocalBroadcastManager.getInstance(holder.mTextViewId.getContext()).sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return mActualData.size();
    }

    public int removeItem(int position) {
        //working  =   mActualData.remove(position); notifyItemRemoved(position);

        String rawData2 = mData.values().toArray()[position].toString();
        String price2 = rawData2.substring((rawData2.indexOf("=") + 1), (rawData2.indexOf(",")));
        total_PRICE = total_PRICE - Integer.parseInt(price2);

        mActualData.remove(position);
        notifyItemRemoved(position);

        return total_PRICE;
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {
    TextView mTextViewId, mTextViewPrice, mTextTotal;
    LinearLayout linearLayout;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        mTextTotal = itemView.findViewById(R.id.txt_totalPrice);
        mTextViewId = itemView.findViewById(R.id.txt_id);
        mTextViewPrice = itemView.findViewById(R.id.txt_price);
        linearLayout = itemView.findViewById(R.id.linearlayout);
    }
}