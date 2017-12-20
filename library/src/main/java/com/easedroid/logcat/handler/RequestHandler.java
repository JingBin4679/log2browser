package com.easedroid.logcat.handler;

import com.easedroid.logcat.Request;
import com.easedroid.logcat.utils.Util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by bin.jing on 2017/12/18.
 */

public abstract class RequestHandler {

    public void processRequest(Request request, Socket socket) throws IOException {
        OutputStream out = new BufferedOutputStream(socket.getOutputStream());
        StringBuilder responseHeaders = newResponseHeaders(request);
        out.write(endHeader(responseHeaders).getBytes("UTF-8"));
        response(out);
        Util.releaseSocket(socket);
    }

    protected abstract void response(OutputStream out);

    protected StringBuilder newResponseHeaders(Request request) throws IOException {
        return new StringBuilder()
                .append("HTTP/1.1 200 OK\n")
                .append("Content-Type: text/plain\n");
    }

    private String endHeader(StringBuilder headers) {
        return headers.append("\n") //end headers
                .toString();
    }
}
