package com.easedroid.logcat;

import android.util.Log;

import com.easedroid.logcat.handler.EmptyRequestHandler;
import com.easedroid.logcat.handler.LogcatRequestHandler;
import com.easedroid.logcat.handler.RequestHandler;
import com.easedroid.logcat.utils.Net;
import com.easedroid.logcat.utils.Util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by bin.jing on 2017/12/18.
 */

public class LogcatHelper {

    private final ExecutorService socketProcessor = Executors.newFixedThreadPool(8);
    private static final String PROXY_HOST = Net.getHostIP();
    private static int PROXY_PORT = 55552;
    private static final String TAG = "LogcatHelper";
    private Thread waitConnectionThread;
    private static LogcatHelper helper;
    private ServerSocket serverSocket;
    private static final AtomicBoolean run = new AtomicBoolean(false);


    private LogcatHelper() {
    }

    public static final void init(int port) {
        if (run.getAndSet(true)) {
            return;
        }
        PROXY_PORT = port;
        init();
    }

    public static final void init() {
        if (run.getAndSet(true)) {
            return;
        }
        if (helper == null) {
            helper = new LogcatHelper();
        }
        helper.start();
    }

    private void start() {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress inetAddress = InetAddress.getByName(PROXY_HOST);
                    LogcatHelper.this.serverSocket = new ServerSocket(PROXY_PORT, 8, inetAddress);
                    CountDownLatch startSignal = new CountDownLatch(1);
                    LogcatHelper.this.waitConnectionThread = new Thread(new WaitRequestsRunnable(startSignal));
                    LogcatHelper.this.waitConnectionThread.start();
                    startSignal.await(); // wait for server starts finish
                    Log.d(TAG, String.format("Log to http://%s:%d/%s", PROXY_HOST, PROXY_PORT, Constant.URL_LOGCAT));
                } catch (IOException | InterruptedException e) {
                    socketProcessor.shutdown();
                    throw new IllegalStateException("Error starting local proxy server", e);
                }
            }
        });
    }


    private final class WaitRequestsRunnable implements Runnable {

        private final CountDownLatch startSignal;

        public WaitRequestsRunnable(CountDownLatch startSignal) {
            this.startSignal = startSignal;
        }

        @Override
        public void run() {
            startSignal.countDown();
            waitForRequest();
        }
    }

    private void waitForRequest() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                Log.d(TAG, "Accept a new socket " + socket);
                socketProcessor.submit(new SocketProcessorRunnable(socket));
            }
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    private final class SocketProcessorRunnable implements Runnable {

        private final Socket socket;

        public SocketProcessorRunnable(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            processSocket(socket);
        }
    }

    private void processSocket(Socket socket) {
        try {
            Request request = Request.read(socket.getInputStream());
            RequestHandler clients = getHandler(request.uri);
            clients.processRequest(request, socket);
        } catch (SocketException e) {
            Log.d(TAG, "Socket is closed.");
        } catch (IOException e) {
            Log.e(TAG, "Error processing request \n" + e.getLocalizedMessage());
        } finally {
            Util.releaseSocket(socket);
        }
    }

    private class Constant {
        private static final String URL_LOGCAT = "util/logcat_console";
    }


    private RequestHandler getHandler(String url) {
        if (url.startsWith(Constant.URL_LOGCAT)) {
            return new LogcatRequestHandler();
        }
        return new EmptyRequestHandler();
    }
}
