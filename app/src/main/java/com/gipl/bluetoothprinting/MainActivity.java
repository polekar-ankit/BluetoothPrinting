package com.gipl.bluetoothprinting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import java.io.Console;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static int MESSAGE_LENGTH_SCRIPT_80 = 64;

    private static final int REQUEST_ENABLE_BT = 321;
    private static final int REQUEST_CONNET_DEVICE = 322;
    private static final String TAG = "Main Activity";
    BluetoothSocket bluetoothSocket;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private Button btnBluetooth;
    private TextView tvDeviceName;
    private MutableLiveData<BluetoothSocket> bluetoothSocketMutableLiveData = new MutableLiveData<>();
    private UUID applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Button btnPrint;
    private byte FontStyleVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnBluetooth = findViewById(R.id.btn_blue);
        tvDeviceName = findViewById(R.id.tv_device_name);
        btnPrint = findViewById(R.id.btn_print);

        btnBluetooth.setOnClickListener(view -> {
            btnPrint.setEnabled(false);
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null) {//support bluetooth
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    Intent intent = new Intent(MainActivity.this, BluetoothDeviceList.class);
                    startActivityForResult(intent, REQUEST_CONNET_DEVICE);
                }
            } else {//not support
                Toast.makeText(MainActivity.this, "Does not suppport Bluetooth", Toast.LENGTH_SHORT).show();
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OutputStream outputStream = bluetoothSocket.getOutputStream();
                            SetFontStyle(false,outputStream);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(getStringWithWhiteSpaces("Gadre Marin Pvt Ltd"));
                           stringBuilder.append(getStringWithWhiteSpaces("Plot FP-1, MIDC, Mirjole Block, Ratnagiri"));

//                            stringBuilder.append("          Plot FP-1, MIDC, Mirjole Block, Ratnagiri             ");
                            stringBuilder.append(getStringWithWhiteSpaces("Maharashtra 415639"));
                            stringBuilder.append(getStringWithWhiteSpaces("GSTIN No : 1293371981732"));
                            String billNo ="Bill No:201";
                            String date = "Date:20/09/19";
                            stringBuilder.append(billNo);
                            stringBuilder.append(String.format("%1$"+(MESSAGE_LENGTH_SCRIPT_80-billNo.length())+"s",date));
                            stringBuilder.append(getHorizontalLine());

                            stringBuilder.append(tableRowWhiteSPace("Product Name","Quantity","Amount"));
                            stringBuilder.append(getHorizontalLine());
                            for (int i = 0; i < 5; i++) {
                                String porName = "Product Name " + (i + 1);
                                String qtn = String.valueOf(((i + 1) * 10));
                                String amt = String.valueOf(((i + 1) * 24));
                                stringBuilder.append(tableRowWhiteSPace(porName,qtn,amt));
                            }
                            stringBuilder.append(getHorizontalLine());
                            String total = "Total : 400";
                            stringBuilder.append(String.format("%1$" + MESSAGE_LENGTH_SCRIPT_80 + "s", total));
                           outputStream.write(stringBuilder.toString().getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private String tableRowWhiteSPace(String cell1,String cell2,String cell3){
        int numberOfColumn = 3;
        int cellCharForEach = (MESSAGE_LENGTH_SCRIPT_80/numberOfColumn);
        int cellCharForFirst= cellCharForEach+16;
        int cellCharForOher2 = cellCharForEach-8;
        return String.format("%s%s%s%s%s%s",
                cell1,
                String.format("%1$" + (cellCharForFirst - cell1.length()) + "s", ""),
                cell2,
                String.format("%1$" + (cellCharForOher2 - cell2.length()) + "s", ""),
                cell3,
                String.format("%1$" + ((cellCharForOher2 - cell3.length())+1) + "s", ""));
    }
    private String getHorizontalLine(){
        StringBuilder lineBuilder = new StringBuilder();
        for (int i=0;i<MESSAGE_LENGTH_SCRIPT_80;i++){
            lineBuilder.append("-");
        }
        return lineBuilder.toString();
    }

    private String getStringWithWhiteSpaces(String strVal){
        int startWhite = getStartBlankSpace(strVal.length());
        int endWhiteSpace = MESSAGE_LENGTH_SCRIPT_80 -( startWhite+strVal.length());

        return String.format("%1$"+startWhite+"s","")+strVal+String.format("%1$"+endWhiteSpace+"s","");
    }
    int getStartBlankSpace(int strlen){
        return ((MESSAGE_LENGTH_SCRIPT_80/2)-(strlen/2))-1;
    }

    final void SetFontStyle(boolean isBold, OutputStream outputStream) throws IOException {
        isBold = true;
        //   as per client's suggestion
        FontStyleVal = (byte) (FontStyleVal & 252);
        if (isBold) {
            FontStyleVal = (byte) (FontStyleVal | 2);
            this.FontStyleVal = ((byte) ((this.FontStyleVal | 8)));
        } else {
            FontStyleVal = ((byte) ((this.FontStyleVal | 2)));
            this.FontStyleVal = ((byte) ((this.FontStyleVal & 247)));
        }

        byte[] command = new byte[]{0x1B, 0x21,
                FontStyleVal};
        //  With this font style we can print max 64 characters in a line
        outputStream.write(command, 0, command.length);


    }

    private int getBlankSpaces(int len) {
        return 20 - len;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ENABLE_BT) {
                //start BT device list activity
                Intent intent = new Intent(this, BluetoothDeviceList.class);
                startActivityForResult(intent, REQUEST_CONNET_DEVICE);
            } else {
                BTDevice btDevice = null;
                if (data != null) {
                    btDevice = data.getParcelableExtra(BluetoothDeviceList.KEY_BT_DEVICE);
                    if (btDevice != null) {
                        Toast.makeText(MainActivity.this, "Bluetooth " + btDevice.getDeviceName() + " found", Toast.LENGTH_SHORT).show();
                        tvDeviceName.setText(String.format("Device Found : %s", btDevice.getDeviceName()));
                        bluetoothDevice = bluetoothAdapter.getRemoteDevice(btDevice.getMacAddress());
                        bluetoothSocketMutableLiveData.observe(this, this::processSocket);
                        new Thread(this::connectToSocket).start();
                    }
                }

            }
        }
    }

    private void processSocket(BluetoothSocket bluetoothSocket) {
        if (bluetoothSocket != null) {
            this.bluetoothSocket = bluetoothSocket;
            btnPrint.setEnabled(true);
        } else {
            btnPrint.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeSocket(bluetoothSocket);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeSocket(bluetoothSocket);
    }

    private void closeSocket(BluetoothSocket nOpenSocket) {
        try {
            nOpenSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException ex) {
            Log.d(TAG, "CouldNotCloseSocket");
        }
    }

    private void connectToSocket() {
        try {
            BluetoothSocket bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            bluetoothAdapter.cancelDiscovery();
            bluetoothSocket.connect();
            bluetoothSocketMutableLiveData.postValue(bluetoothSocket);
        } catch (IOException e) {
            bluetoothSocketMutableLiveData.postValue(null);
        }
    }
}
