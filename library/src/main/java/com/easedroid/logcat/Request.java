package com.easedroid.logcat;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.easedroid.logcat.Preconditions.checkNotNull;

/**
 * Created by bin.jing on 2017/12/18.
 */

public class Request {

    private static final Pattern URL_PATTERN = Pattern.compile("GET /(.*) HTTP");

    public final String uri;

    public Request(String request) {
        checkNotNull(request);
        this.uri = findUri(request);
    }

    public static Request read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder stringRequest = new StringBuilder();
        String line;
        while (!TextUtils.isEmpty(line = reader.readLine())) { // until new line (headers ending)
            stringRequest.append(line).append('\n');
        }
        return new Request(stringRequest.toString());
    }

    private String findUri(String request) {
        Matcher matcher = URL_PATTERN.matcher(request);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid request `" + request + "`: url not found!");
    }

    @Override
    public String toString() {
        return "Request{" +
                "uri='" + uri + '\'' +
                '}';
    }
}
