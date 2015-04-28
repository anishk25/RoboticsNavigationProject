package com.arduino.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by anish_khattar25 on 4/28/15.
 */
public class BluetoothManager {
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private final UUID device_UUID;
    private InputStream mmInStream;
    private OutputStream mmOutStream;

    private ConnectThread connectThread;
    private MessageThread messageThread;
    public BluetoothManager(BluetoothDevice device, BluetoothAdapter adapter, UUID device_UUID){
        this.mmDevice = device;
        this.mBluetoothAdapter = adapter;
        this.device_UUID = device_UUID;
    }


    public void connectToDevice(){
        connectThread = new ConnectThread();
        connectThread.start();
        messageThread = new MessageThread();
        messageThread.start();

    }

    public void sendMessage(byte[] bytes) {
         messageThread.write(bytes);
    }



   private class ConnectThread extends Thread {

        public ConnectThread() {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = mmDevice.createRfcommSocketToServiceRecord(device_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            messageThread.setSocket(mmSocket);
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }



    private class MessageThread extends Thread {
        public MessageThread(){
        }

        public void setSocket(BluetoothSocket socket){
            if(mmInStream == null || mmOutStream == null) {
                InputStream tmpIn = null;
                OutputStream tmpOut = null;
                mmSocket = socket;
                try {
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();
                } catch (IOException e) {
                }

                mmInStream = tmpIn;
                mmOutStream = tmpOut;
            }
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    if(mmInStream != null) {
                        bytes = mmInStream.read(buffer);
                    }
                    // Send the obtained bytes to the UI activity;
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                if(mmOutStream != null) {
                    mmOutStream.write(bytes);
                }
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
