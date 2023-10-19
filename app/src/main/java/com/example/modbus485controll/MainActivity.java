package com.example.modbus485controll;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.modbus485controll.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    SerialPortUtil mSerialPortUtil;
    // Used to load the 'modbus485controll' library on application startup.
    static {
        System.loadLibrary("modbus485controll");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //单模式
        mSerialPortUtil = SerialPortUtil.getInstance();
        mSerialPortUtil.setOnDataReceiveListener(new SerialPortUtil.OnDataReceiveListener() {

            @Override
            public void onDataReceive(byte[] buffer, int size) {
                // TODO Auto-generated method stub
                Log.d("[gx]", " DataReceive1233:" + new String(buffer, 0, size));
                //tv_content
            }
        });

        Button button = findViewById(R.id.btn1);
        button.setOnClickListener(v -> {
            // 01 10 00 00 00 01 02 00 01 67 90
            byte[] cmd = {0x01, (byte) 0x10, 0, 0, 0, 0x01, 2, 0, 1, (byte) 0x67, (byte) 0x90};
            mSerialPortUtil.sendCmds(cmd);

        });

        Button buttonAllOff = findViewById(R.id.btnAllOff);
        buttonAllOff.setOnClickListener(v -> {
            // 01 10 00 00 00 01 02 0000 A6 50
            byte[] cmd = {0x01, (byte) 0x10, 0, 0, 0, 0x01, 2, 0, 0, (byte) 0xa6, (byte) 0x50};
            mSerialPortUtil.sendCmds(cmd);

        });
    }

    /**
     * A native method that is implemented by the 'modbus485controll' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}