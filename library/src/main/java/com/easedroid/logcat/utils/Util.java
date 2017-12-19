package com.easedroid.logcat.utils;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by bin.jing on 2017/12/19.
 */

public class Util {

    private static final String TAG = "Util";

    public static void releaseSocket(Socket socket) {
        closeSocketInput(socket);
        closeSocketOutput(socket);
        closeSocket(socket);
    }

    private static void closeSocketInput(Socket socket) {
        try {
            if (!socket.isInputShutdown()) {
                socket.shutdownInput();
            }
        } catch (SocketException e) {
            Log.d(TAG, "Socket is closed by client.");
        } catch (IOException e) {
            Log.e(TAG, "Close socket input stream error \n" + e.getLocalizedMessage());
        }
    }

    private static void closeSocketOutput(Socket socket) {
        try {
            if (!socket.isOutputShutdown()) {
                socket.shutdownOutput();
            }
        } catch (IOException e) {
            Log.w(TAG, "Failed to close socket. client have already closed connection ? \n" + e.getMessage());
        }
    }

    private static void closeSocket(Socket socket) {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing socket \n" + e.getMessage());
        }
    }
}
