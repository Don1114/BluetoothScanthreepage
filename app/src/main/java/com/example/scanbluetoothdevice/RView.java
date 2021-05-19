package com.example.scanbluetoothdevice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RView extends RecyclerView.Adapter<RView.ViewHolder> {
    private OnItemClick onItemClick;
    public List<Scanned> arrayList = new ArrayList<>();
    private Bluetooth activity;
    private AppCompatActivity aca;
    private RecyclerView.ViewHolder RH;

    public RView(Bluetooth activity) {

        this.activity = activity;
    }

    public void AppCompatActivity(AppCompatActivity aca){
        this.aca = aca;
    }
    public void OnItemClick(OnItemClick onItemClick){

        this.onItemClick = onItemClick;
    }
    /**清除搜尋到的裝置列表*/
    public void clearDevice(){
        this.arrayList.clear();
        notifyDataSetChanged();
    }
    /**若有不重複的裝置出現，則加入列表中*/
    public void addDevice(List<Scanned> arrayList){
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,tvAddress,tvInfo,tvRssi;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView_DeviceName);
            tvAddress = itemView.findViewById(R.id.textView_Address);
            tvInfo = itemView.findViewById(R.id.textView_ScanRecord);
            tvRssi = itemView.findViewById(R.id.textView_Rssi);
            /**點擊項目時*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.click();
                    //Toast.makeText(view.getContext(),"click " +getAdapterPosition(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scanned_item,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(arrayList.get(position).getDeviceName());
        holder.tvAddress.setText("裝置位址："+arrayList.get(position).getAddress());
        holder.tvInfo.setText("裝置挾帶的資訊：\n"+arrayList.get(position).getDeviceByteInfo());
        holder.tvRssi.setText("訊號強度："+arrayList.get(position).getRssi());
        //holder.itemView.setOnClickListener(v -> {
        //onItemClick.onItemClick(arrayList.get(position));
        //});
    }

    @Override
    public int getItemCount() {

        return arrayList.size();
    }
    interface OnItemClick{
        void onItemClick(Scanned selectedDevice);
    }

}
