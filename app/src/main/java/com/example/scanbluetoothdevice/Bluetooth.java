package com.example.scanbluetoothdevice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
//import android.bluetooth.le.BluetoothLeScanner;
//import android.bluetooth.le.ScanCallback;
//import android.bluetooth.le.ScanRecord;
//import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
//import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class Bluetooth extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName()+"My";
    BluetoothAdapter mBA = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 102;
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean isScanning = false;
    private ArrayList<Scanned> findDevice = new ArrayList<>();
    private RView mAdapter;
    private RecyclerView rView;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        /**初始化recyclerview*/
        initView();
        /**初始藍牙掃描及掃描開關之相關功能*/
        bluetoothScan();

    }
    private void initView(){
        rView = (RecyclerView) findViewById(R.id.recyclerView_ScannedList);
        //建立預設的線性LayoutManager
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        rView.setLayoutManager(layoutManager);
        //如果可以確定每個item的高度是固定的，設定這個選項可以提高效能
        rView.setHasFixedSize(true);
    }
    /**初始藍牙掃描及掃描開關之相關功能*/
    private void bluetoothScan() {
        /**啟用藍牙適配器*/
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBA = bluetoothManager.getAdapter();
//        if(mBA!=null){
//            BluetoothLeScanner mBluetoothLeScanner =mBA.getBluetoothLeScanner();
//        }

        /**開始掃描*/
        mBA.startLeScan(mLeScanCallback);
//        mBluetoothLeScanner.startScan(startScanCallback);
        isScanning = true;
        /**設置Recyclerview列表*/
        RecyclerView rView =  findViewById(R.id.recyclerView_ScannedList);
        layoutManager = new LinearLayoutManager(this);
        rView.setLayoutManager(layoutManager);
        rView.setHasFixedSize(true);
        rView.setItemAnimator(new DefaultItemAnimator());
        rView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RView(this);
        rView.setAdapter(mAdapter);
        /**製作停止/開始掃描的按鈕*/

        final Button ScanBtn = findViewById(R.id.button_Scan);
        ScanBtn.setOnClickListener((v)-> {
            if (isScanning) {
                /**關閉掃描*/
                isScanning = false;
                ScanBtn.setText("開始掃描");
                mBA.stopLeScan(mLeScanCallback);
                //mBluetoothLeScanner.stopScan(startScanCallback);
            }else{
                /**開啟掃描*/
                isScanning = true;
                ScanBtn.setText("停止掃描");
                mBA.startLeScan(mLeScanCallback);
//                mBluetoothLeScanner.startScan(startScanCallback);
                mAdapter.clearDevice();
            }
        });
    }
    @Override

    protected void onStart() {
        super.onStart();
        final Button btScan = findViewById(R.id.button_Scan);
        isScanning = true;
        btScan.setText("停止掃描");
        findDevice.clear();
        mBA.startLeScan(mLeScanCallback);
        mAdapter.clearDevice();
    }

//    private final ScanCallback startScanCallback = new ScanCallback() {
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            BluetoothDevice device = result.getDevice();
//            ScanRecord mScanRecord = result.getScanRecord();
//            String address = device.getAddress();
//            byte[] content = mScanRecord.getBytes();
//            int mRssi = result.getRssi();
//        }
//    };

    @Override
    protected void onStop() {
        super.onStop();
        final Button btScan = findViewById(R.id.button_Scan);
        /**關閉掃描*/
        isScanning = false;
        btScan.setText("開始掃描");
        mBA.stopLeScan(mLeScanCallback);
    }

    /**顯示掃描到物件*/
    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            new Thread(()->{
                //if (device.getName()!= null){
                /**將搜尋到的裝置加入陣列*/
                findDevice.add(new Scanned(device.getName()
                        , String.valueOf(rssi)
                        , byteArrayToHexStr(scanRecord)
                        , device.getAddress()));
                /**將陣列中重複Address的裝置濾除，並使之成為最新數據*/
                ArrayList newList = getSingle(findDevice);
                runOnUiThread(()->{
                    /**將陣列送到RecyclerView列表中*/
                    mAdapter.addDevice(newList);
                });
                //}
            }).start();
        }
    };
    /**濾除重複的藍牙裝置(以Address判定)*/
    private ArrayList getSingle(ArrayList list) {
        ArrayList tempList = new ArrayList<>();
        try {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (!tempList.contains(obj)) {
                    tempList.add(obj);
                } else {
                    tempList.set(getIndex(tempList, obj), obj);
                }
            }
            return tempList;
        } catch (ConcurrentModificationException e) {
            return tempList;
        }
    }
    /**
     * 以Address篩選陣列->抓出該值在陣列的哪處
     */
    private int getIndex(ArrayList temp, Object obj) {
        for (int i = 0; i < temp.size(); i++) {
            if (temp.get(i).toString().contains(obj.toString())) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Byte轉16進字串工具
     */
    public static String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }

        StringBuilder hex = new StringBuilder(byteArray.length * 2);
        for (byte aData : byteArray) {
            hex.append(String.format("%02X", aData));
        }
        String gethex = hex.toString();
        return gethex;
    }

    public void click(){
        Intent it = new Intent(Bluetooth.this,showdetail.class);
        startActivity(it);
        finish();
    }
}