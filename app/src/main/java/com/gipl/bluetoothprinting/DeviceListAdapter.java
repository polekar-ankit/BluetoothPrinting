package com.gipl.bluetoothprinting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by Ankit on 03-Sep-19.
 */
public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.Holder> {

    private ArrayList<BTDevice> deviceArrayList = new ArrayList<>();

    void addItem(BTDevice btDevice) {
        deviceArrayList.add(btDevice);
        notifyDataSetChanged();
    }

    public void setDeviceArrayList(ArrayList<BTDevice> deviceArrayList) {
        this.deviceArrayList.addAll(deviceArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_device_list_tem, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        final BTDevice btDevice = deviceArrayList.get(position);
        holder.tvDeviceName.setText(btDevice.getDeviceName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btDeviceClickListener.onDeviceClick(btDevice);
            }
        });
    }

    public DeviceListAdapter(BtDeviceClickListener btDeviceClickListener) {
        this.btDeviceClickListener = btDeviceClickListener;
    }

    private BtDeviceClickListener btDeviceClickListener;
    public interface BtDeviceClickListener{
        void onDeviceClick(BTDevice btDevice);
    }
    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private TextView tvDeviceName;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
        }
    }
}
