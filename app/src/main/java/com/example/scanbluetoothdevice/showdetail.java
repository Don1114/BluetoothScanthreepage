package com.example.scanbluetoothdevice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class showdetail extends AppCompatActivity {

    private RView rview;
    private RView.ViewHolder RH;
    private Bluetooth bl;
    private Scanned scan;
    private List<Scanned> arrayList;
    private TextView tvName,tvAddress,tvInfo,tvRssi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showdetail);
        name();
    }

    public void backButton(View view){
        Intent it = new Intent(showdetail.this,Bluetooth.class);
        startActivity(it);
        finish();
    }

    public void name(){
        tvName = findViewById(R.id.textView_DeviceName);
        tvAddress = findViewById(R.id.textView_Address);
        tvInfo = findViewById(R.id.textView_ScanRecord);
        tvRssi = findViewById(R.id.textView_Rssi);

        //tvName.setText(rview.arrayList.get(1).getDeviceName());
        //tvAddress.setText("裝置位址："+arrayList.get(RH.getAdapterPosition()).getAddress());
        //tvInfo.setText("裝置挾帶的資訊：\n"+arrayList.get(RH.getAdapterPosition()).getDeviceByteInfo());
        //tvRssi.setText("訊號強度："+arrayList.get(RH.getAdapterPosition()).getRssi());
    }

}