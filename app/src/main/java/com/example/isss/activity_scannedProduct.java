package com.example.isss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.multidex.MultiDex;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class activity_scannedProduct extends AppCompatActivity {

    public LinkedHashMap<String, Object> DATA = new LinkedHashMap<String, Object>();
    public LinkedHashMap<String, String> DATA_2 = new LinkedHashMap<String, String>();
    RecyclerView recyclerView_scannedProducts;
    RecyclerView.LayoutManager layoutManager;
    Button btn_checkout;
    TextView txt_tagId, txt_totalPrice;
    int totalPrice;
    //
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            totalPrice = intent.getIntExtra("TOTALITY", -2);
            txt_totalPrice.setText(String.valueOf(totalPrice));

            PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("TOTALITY", totalPrice).apply();
        }
    };
    LinearLayout data0_layout, layout_searchResult;
    ListView listView_searchResult;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseReference;
    MyAdapter adapter;

    ArrayList<String> singleList = new ArrayList<>();
    private MyBroadcastReceiver myReceiver;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseReference = firebaseDatabase.getReference("Locations");

        String[] items = {};
        ArrayAdapter<String> adapterResult = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.LTGRAY);
                return view;
            }
        };
        listView_searchResult.setAdapter(adapterResult);

        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                listView_searchResult.setVisibility(View.VISIBLE);
                adapterResult.clear();
                adapterResult.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                listView_searchResult.setVisibility(View.INVISIBLE);
                adapterResult.clear();
                adapterResult.notifyDataSetChanged();
                return true;
            }
        });

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search Product for Location");
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                if (s.length() == 0) {
                    listView_searchResult.setVisibility(View.INVISIBLE);
                    adapterResult.clear();
                    adapterResult.notifyDataSetChanged();
                    singleList.clear();
                } else {
                    listView_searchResult.setVisibility(View.VISIBLE);
                    firebaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator(); //left right
                            while (items.hasNext()) {
                                DataSnapshot item1 = items.next();
                                Iterator<DataSnapshot> items2 = item1.getChildren().iterator(); //aisle
                                while (items2.hasNext()) {
                                    DataSnapshot item2 = items2.next();
                                    Iterator<DataSnapshot> items3 = item2.getChildren().iterator(); //bottom centre right
                                    while (items3.hasNext()) {
                                        DataSnapshot item3 = items3.next();
                                        Iterator<DataSnapshot> items4 = item3.getChildren().iterator(); //partitions
                                        while (items4.hasNext()) {
                                            DataSnapshot item5 = items4.next();
                                            Iterator<DataSnapshot> items6 = item5.getChildren().iterator(); //partitions
                                            while (items6.hasNext()) {
                                                DataSnapshot item7 = items6.next();
                                                Iterator<DataSnapshot> items8 = item7.getChildren().iterator(); //last
                                                while (items8.hasNext()) {
                                                    DataSnapshot item9 = items8.next();
                                                    if (item9.getValue().toString().toLowerCase().contains(s)) {

                                                        singleList.clear();
                                                        adapterResult.clear();
                                                        adapterResult.notifyDataSetChanged();

                                                        adapterResult.add(item9.getValue().toString());
                                                        adapterResult.notifyDataSetChanged();

                                                        singleList.add("Entrance");
                                                        singleList.add(item1.getKey());
                                                        singleList.add(item2.getKey());
                                                        singleList.add(item3.getKey());
                                                        singleList.add(item5.getKey());
                                                        singleList.add(item7.getKey());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    adapterResult.getFilter().filter(s);
                }

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                layout_searchResult.setVisibility(View.VISIBLE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_product);

        MultiDex.install(getApplicationContext());

        recyclerView_scannedProducts = findViewById(R.id.recycler_sacnnedproducts);
        btn_checkout = findViewById(R.id.btn_checkout);
        txt_tagId = findViewById(R.id.txt_tagId);
        txt_totalPrice = findViewById(R.id.txt_totalPrice);
        data0_layout = findViewById(R.id.layout_data0);
        layout_searchResult = findViewById(R.id.linearlayout_topsheet_searchRresult);
        listView_searchResult = findViewById(R.id.list_searchResult);
        listView_searchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int in, long l) {
                dialog_location dialogLocation = new dialog_location();
                dialogLocation.show(getSupportFragmentManager(), "location");
            }
        });

        //
        if (isNetwork(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Internet Is Not Connected", Toast.LENGTH_SHORT).show();

            Intent intentWireless = new Intent(getApplicationContext(), activity_wirelessNET.class);
            startActivity(intentWireless);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseReference = firebaseDatabase.getReference("Locations");

        //
        txt_tagId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                hashMapper(charSequence.toString());
            }
        });

        //
        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewDialog alert = new ViewDialog();
                alert.alertView_checkout(activity_scannedProduct.this, "Confirmation Alert!", "Are you sure, you want to Checkout?", DATA, DATA_2, txt_totalPrice.getText().toString());
            }
        });

        //
        recyclerView_scannedProducts.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView_scannedProducts, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                view.setBackgroundColor(Color.parseColor("#0F4927"));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackgroundColor(Color.LTGRAY);
                    }
                }, 3000);

                TextView clickeditemkey = view.findViewById(R.id.txt_id);

                ViewDialog alert = new ViewDialog();
                alert.showDialog_product(activity_scannedProduct.this, position, clickeditemkey.getText(), DATA, DATA_2, recyclerView_scannedProducts);
            }

            @Override
            public void onLongClick(View view, int position) {

                int priceUpdate;
                priceUpdate = adapter.removeItem(position);
                txt_totalPrice.setText(String.valueOf(priceUpdate));

                //working
                int i = 0;
                Iterator<Map.Entry<String, Object>> iterator = DATA.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    if (i == position) {
                        iterator.remove();

                        vibrate_sound vibrateSound = new vibrate_sound();
                        vibrateSound.vibrate(getApplicationContext());
                        vibrateSound.sound(getApplicationContext(), 4);
                        //
                        if (DATA.size() == 0) {
                            data0_layout.setVisibility(View.VISIBLE);
                        } else if (DATA.size() != 0) {
                            data0_layout.setVisibility(View.GONE);
                        }
                    }
                    i++;
                }

            }

        }));

        layoutManager = new LinearLayoutManager(this);
        recyclerView_scannedProducts.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerView_scannedProducts.setLayoutManager(layoutManager);

        //
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));
    }

    //
    public ArrayList view_location() {
        return singleList;
    }

    //
    public void hashMapper(String id) {
        System.out.println(id);
        firebaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //DATA = (Map<String, Object>) dataSnapshot.getValue();
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator(); //left right
                while (items.hasNext()) {
                    DataSnapshot item = items.next();
                    Iterator<DataSnapshot> items2 = item.getChildren().iterator(); //aisle
                    while (items2.hasNext()) {
                        DataSnapshot item2 = items2.next();
                        Iterator<DataSnapshot> items3 = item2.getChildren().iterator(); //bottom centre right
                        while (items3.hasNext()) {
                            DataSnapshot item3 = items3.next();
                            Iterator<DataSnapshot> items4 = item3.getChildren().iterator(); //partitions
                            while (items4.hasNext()) {
                                DataSnapshot item5 = items4.next();
                                Iterator<DataSnapshot> items6 = item5.getChildren().iterator(); //partitions
                                while (items6.hasNext()) {
                                    DataSnapshot item7 = items6.next();
                                    if (item7.getKey().equals(id)) {

                                        System.out.println("..." + item7.getKey() + "   " + item7.getValue());

                                        DATA.put(item7.getKey(), item7.getValue());

                                        adapter = new MyAdapter(DATA);
                                        recyclerView_scannedProducts.setAdapter(adapter);
                                        adapter.notifyItemInserted(recyclerView_scannedProducts.getAdapter().getItemCount());
                                        recyclerView_scannedProducts.smoothScrollToPosition(recyclerView_scannedProducts.getAdapter().getItemCount());

                                        vibrate_sound vibrateSound = new vibrate_sound();
                                        vibrateSound.vibrate(getApplicationContext());
                                        vibrateSound.sound(getApplicationContext(), 3);

                                        //
                                        if (DATA.size() == 0) {
                                            data0_layout.setVisibility(View.VISIBLE);
                                        } else if (DATA.size() != 0) {
                                            data0_layout.setVisibility(View.GONE);
                                        }

                                        Iterator<DataSnapshot> items8 = item7.getChildren().iterator(); //last
                                        while (items8.hasNext()) {
                                            DataSnapshot item9 = items8.next();
                                            DATA_2.put(item9.getKey(), item9.getValue().toString());
                                        }

                                        Log.d("data", "found");
                                    } else {
                                        //Log.d("data", "no match :" + item7.getKey());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        myReceiver = new MyBroadcastReceiver();
        final IntentFilter intentFilter = new IntentFilter("Action1");
//        Toast.makeText(this, "Product onResume", Toast.LENGTH_SHORT).show();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (myReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
//        Toast.makeText(this, "Product onPause", Toast.LENGTH_SHORT).show();
        myReceiver = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("TOTALITY", 0).apply();
    }

    //
    public boolean isNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    //
    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            String yourTagValue = b.getString("tagID");
            txt_tagId.setText(yourTagValue);
        }

    }
}

class ViewDialog {
    TextView txt_title, txt_price, txt_desc, txt_manu;

    public void showDialog_product(Activity activity, int position,
                                   CharSequence text, LinkedHashMap<String, Object> DATA, LinkedHashMap<String, String> DATA_2, RecyclerView list_sacnnedproducts) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_dialog_product);

        txt_title = (TextView) dialog.findViewById(R.id.txt_product_title);
        txt_manu = dialog.findViewById(R.id.txt_product_manufactuurer);
        txt_desc = dialog.findViewById(R.id.txt_product_description);
        txt_price = dialog.findViewById(R.id.txt_product_price);

        Button dialogButton_close = (Button) dialog.findViewById(R.id.btn_close);
        dialogButton_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView[] textViews = {txt_price, txt_desc, txt_title, txt_manu};
        String[] b = new String[0];
        for (Map.Entry<String, Object> entry : DATA.entrySet()) {
            if (entry.getKey().equals(text)) {

                String[] a = DATA.values().toArray()[position].toString().split(",");
                for (int i = 0; i < a.length; i++) {
                    b = a[i].split("=");
                    textViews[i].setText(b[1]);
                }

            } else {
                Log.d("key", "error" + entry.getKey() + " ?? " + entry.getValue() + " ::::: " + text);
            }
        }

        String str = txt_manu.getText().toString();
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == '}') {
            str = str.substring(0, str.length() - 1);
            txt_manu.setText(str);
        }

        dialog.show();
    }

    public void alertView_checkout(Activity activity, String title,
                                   String message, LinkedHashMap<String, Object> data, LinkedHashMap<String, String> data_2, String totalPRICE) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle(title);
        dialog.setIcon(R.drawable.ic_baseline_info_24);
        dialog.setMessage(message);
        dialog.setButton(Dialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setButton(Dialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                vibrate_sound vibrateSound = new vibrate_sound();
                vibrateSound.vibrate(activity.getApplicationContext());
                vibrateSound.sound(activity.getApplicationContext(), 1);

                Intent intentQR = new Intent(activity, activity_qrcode.class);
                ArrayList<String> finalData = new ArrayList<>(data.size());

                int b = 0;
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    String prod = entry.getKey();
                    finalData.add(b, prod);
                    b++;
                }

                String[] stockArr = new String[finalData.size()];
                stockArr = finalData.toArray(stockArr);

                intentQR.putExtra("final2", stockArr);
                PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt("TOTALITY", Integer.parseInt(totalPRICE)).apply();

                activity.startActivity(intentQR);
            }
        });

        dialog.show();
    }
}
