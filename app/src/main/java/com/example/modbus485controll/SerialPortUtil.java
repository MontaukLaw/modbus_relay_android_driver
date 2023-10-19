package com.example.modbus485controll;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 串口操作
 *
 * @author guoxiao
 */
public class SerialPortUtil {
    private String TAG = SerialPortUtil.class.getSimpleName();
    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private String path = "/dev/ttysWK0";
    private int baudrate = 9600; // 2400;//115200;//9600;
    private static SerialPortUtil portUtil;
    private OnDataReceiveListener onDataReceiveListener = null;
    private boolean isStop = false;

    public interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(
            OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    public static SerialPortUtil getInstance() {
        if (null == portUtil) {
            portUtil = new SerialPortUtil();
            portUtil.onCreate();
        }
        return portUtil;
    }

    /**
     * 初始化串口信�?
     */
    private void onCreate() {
        try {
            mSerialPort = new SerialPort(new File(path), baudrate);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            mReadThread = new ReadThread();
            isStop = false;
            mReadThread.start();
        } catch (Exception e) {
            Log.d(TAG, "Exception: ");
            e.printStackTrace();
        }
    }

    /**
     * 发�?指令到串�?
     *
     * @param cmd
     * @return
     */
    public boolean sendCmds(String cmd) {
        boolean result = true;
        byte[] mBuffer = cmd.getBytes();
        try {
            if (mOutputStream != null) {
                mOutputStream.write(send_data2);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public boolean sendCmds(byte[] mBuffer) {
        boolean result = true;
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    byte[] send_data = {0x01, 0x04, 0x00, 0x13, 0x00, 0x02, (byte) 0x80, 0x0e};
    //01 04 00 11 00 02 21 CE

    byte[] send_data2 = {0x01, 0x04, 0x00, 0x11, 0x00, 0x02, 0x21, (byte) 0xce};

    public boolean sendBuffer(byte[] mBuffer) {
        boolean result = true;
        String tail = "";
        byte[] tailBuffer = tail.getBytes();
        byte[] mBufferTemp = new byte[mBuffer.length + tailBuffer.length];
        System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
        System.arraycopy(tailBuffer, 0, mBufferTemp, mBuffer.length, tailBuffer.length);
        try {
            if (mOutputStream != null) {
                mOutputStream.write(send_data);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();

            //byte [] buffer= new byte[1024];
            //	int ch;
            //int bytes;

            while (true) {//!isInterrupted()) {
                int size;
                try {
                    if (mInputStream == null)
                        return;
					/*bytes=0;
					while((ch=mInputStream.read())!='\n'){
					
						
						Log.d("liz","bb="+ch+"bytes=="+bytes);
						
						 if(ch!=-1){
							 buffer[bytes]=(byte) ch;
							 bytes++;
							 
							 Log.d("liz","bb="+ch+"bytes=="+bytes);
						 }
						 
					}
					buffer[bytes]='\n';
					bytes++;
					
					
					Log.d("liz","length is:"+bytes+",data is:"+new String(buffer, 0, bytes));*/


                    if (mInputStream.available() > 0 == false) {
                        continue;
                    } else {
                        Thread.sleep(100);
                        byte[] buffer = new byte[512];
                        size = mInputStream.read(buffer);
                        if (size > 0) {


                            int i;
                            for (i = 0; i < size; i++) {

                                System.out.println("rd" + i + "=" + Integer.toHexString(0xff & buffer[i]));

                            }

                            String str = bytesToHexString(buffer, size);
                            //	String str = new String(buffer, 0, size);
                            Log.d("liz", "length is:" + size + ",data is:" + str);
                            if (null != onDataReceiveListener) {
                                onDataReceiveListener.onDataReceive(buffer, size);
                            }

                        }
                    }

                    //Thread.sleep(200);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
        }
    }

    public static String bytesToHexString(byte[] bytes, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
