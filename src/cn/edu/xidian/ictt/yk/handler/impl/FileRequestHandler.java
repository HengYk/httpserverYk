package cn.edu.xidian.ictt.yk.handler.impl;

import cn.edu.xidian.ictt.yk.handler.RequestHandler;
import cn.edu.xidian.ictt.yk.http.HttpRequest;
import cn.edu.xidian.ictt.yk.http.HttpResponse;
import cn.edu.xidian.ictt.yk.http.HttpStatus;
import cn.edu.xidian.ictt.yk.util.PathUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by hengyk on 19-5-10
 */
public class FileRequestHandler implements RequestHandler {

    private String reqPath;

    public FileRequestHandler(String reqPath) {
        if (reqPath.endsWith("/")) {
            this.reqPath = reqPath.substring(0, reqPath.length() - 1);
        } else {
            this.reqPath = reqPath;
        }
    }

    @Override
    public HttpResponse handle(HttpRequest request) {

        String uri = request.getUri();

        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            uri = uri.replace("%20", " ");
        }

        uri = PathUtil.fixPath(uri);

        if (uri.equals("/")) {
            uri = "/index.html";
        }

        File file = new File(reqPath, uri);
        if (file.exists() && !file.isDirectory()) {
            try {
                String requestPath = file.getCanonicalPath();
                if (requestPath.endsWith("/")) {
                    requestPath = requestPath.substring(0, requestPath.length() - 1);
                }

                if (requestPath.startsWith(HttpResponse.ROOTPATH)) {
                    HttpResponse hr = new HttpResponse();
                    hr.setReadFlag(true);
                    return hr;
                } else {
                    return new HttpResponse(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getPhrase());
                }

            } catch (IOException e) {
                return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getPhrase());
            }
        }

        return new HttpResponse();
    }
}
