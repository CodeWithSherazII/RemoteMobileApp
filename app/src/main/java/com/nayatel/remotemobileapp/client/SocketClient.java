package com.nayatel.remotemobileapp.client;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
    private Socket socket;
    private PrintWriter output;

    public boolean connectToServer(String ipAddress, int port) {
        try {
            socket = new Socket(ipAddress, port);
            Log.d("remote", "IP Address: " + ipAddress);
            output = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendCommand(String command) {
        if (output != null) {
            Log.d("remote" , "output: " + output);
            output.println(command);
        }
    }

    public void closeConnection() {
        try {
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

