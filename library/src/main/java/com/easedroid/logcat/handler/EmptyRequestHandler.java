package com.easedroid.logcat.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by bin.jing on 2017/12/19.
 */

public class EmptyRequestHandler extends BaseRequestHandler {

    public static final String NOT_SUPPORT_REQUEST = "Not support request.";

    @Override
    protected void response(OutputStream out) {
        try {
            byte[] responseText = NOT_SUPPORT_REQUEST.getBytes("UTF-8");
            out.write(responseText);
            out.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
