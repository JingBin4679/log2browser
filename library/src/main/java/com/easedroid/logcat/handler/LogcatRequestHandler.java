package com.easedroid.logcat.handler;

import com.easedroid.logcat.Request;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by bin.jing on 2017/12/19.
 */

public class LogcatRequestHandler extends BaseRequestHandler {

    @Override
    protected void response(OutputStream out) {
        Process exec = null;
        try {
            exec = Runtime.getRuntime().exec("logcat -v time");
            final InputStream inputStream = exec.getInputStream();
            writeStream(out, inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            if (exec != null) {
                exec.destroy();
            }
        }
    }


    private void writeStream(OutputStream out, InputStream inputStream) throws IOException {
        if (inputStream == null) return;
        byte[] buffer = new byte[8192];
        int endOffset = 2;
        int read = -1;
        while ((read = inputStream.read(buffer)) > 0) {
            String dataLength = String.format("%X\r\n", read);
            final byte[] dataLengthBytes = dataLength.getBytes();
            int offset = dataLengthBytes.length;

            final byte[] data = new byte[read + offset + endOffset];

            System.arraycopy(dataLengthBytes, 0, data, 0, offset);
            System.arraycopy(buffer, 0, data, offset, read);
            int byeIndex = offset + read;
            data[byeIndex++] = '\r';
            data[byeIndex++] = '\n';
            out.write(data);
            out.flush();
        }
        if (read <= 0) {
            out.write("0\r\n\r\n".getBytes());
            out.flush();
        }
    }

    @Override
    protected StringBuilder newResponseHeaders(Request request) throws IOException {
        return super.newResponseHeaders(request)
                .append(String.format("Transfer-Encoding: chunked\n"));
    }
}
