package cn.edu.xidian.ictt.yk.http;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hengyk on 19-5-9
 */
public class HttpSession {

    private Socket socket;

    public HttpSession() {
    }

    public HttpSession(Socket socket) {
        this.socket = socket;
    }

    /**
     * 接收来自客户端的数据（原始的粗数据）,接收到的数据在HttpRequest中处理
     *
     * @param request
     */
    public void receiveRequestData(HttpRequest request) {

        List<String> rawData = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line = null;
            while ((line = reader.readLine()) != null) {
                // 关键条件
                if (line.isEmpty()) {
                    break;
                }
                rawData.add(line + "\r\n");
            }
            rawData.add("\r\n");

            /*
            Reader reader = new InputStreamReader(socket.getInputStream());
            StringBuilder sb = new StringBuilder();
            int c;
            int tempC = -1;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
                if (tempC == '\r' && c == '\n') {
                    rawData.add(sb.toString());
                    String temp = sb.toString().substring(0, sb.length() - 2);
                    if (temp.equals("")) {
                        break;
                    }
                    sb.delete(0, sb.length());
                }
                tempC = c;
            }*/

//            rawData.add("GET / HTTP/1.1\r\n" +
//                    "Host: localhost:8080\r\n" +
//                    "Connection: keep-alive\r\n" +
//                    "Cache-Control: max-age=0\r\n" +
//                    "Upgrade-Insecure-Requests: 1\r\n" +
//                    "User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36\r\n" +
//                    "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3\r\n" +
//                    "Accept-Encoding: gzip, deflate, br\r\n" +
//                    "Accept-Language: en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7\r\n" +
//                    "\r\n");
            request.setRawData(rawData);

            // System.out.println(rawData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送来自服务器的数据，待发送的数据在HttpResponse中处理
     *
     * @param response
     */
    public void sendReponseData(HttpResponse response) {

        OutputStream writer = null;

        try {
            writer = new BufferedOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Socket error !!!");
        }

        String body = response.readFilePub();

        if (body != null) {
            try {
                // 输出样例如下：
                // HTTP/1.0 200 OK\r\n
                writer.write((response.getVersion() + " " + response.getStatusCode() + " " + response.getStatusPhrase() + "\r\n").getBytes());

                response.getHeaders().put(HttpHeaders.CONTENT_TYPE,
                        HttpResponse.guessContentTypeFromName(response.getFileName().substring(
                                response.getFileName().lastIndexOf(".") + 1)) + "; charset=utf-8");

                // Date: Wed May 08 22:32:58 CST 2019\r\n
                // Server: hengyk\r\n
                // Content-Length: 18\r\n
                // Connection: close\r\n
                // Content-Type: text/html; charset=utf-8\r\n
                for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                    writer.write((header.getKey() + ": " + header.getValue() + "\r\n").getBytes());
                }

                // \r\n
                writer.write(("\r\n").getBytes());

                // 实体内容body
                if (response.isReadFlag()) {
                    writer.write(body.getBytes());
                }

                writer.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {

                body = "<HTML>" + "<HEAD>" + "<TITLE>File Not Found</TITLE>" + "</HEAD>" +
                        "<BODY>" + "<H1>HTTP Error 404: File Not Found</H1>" + "</BODY>" +
                        "</HTML>\r\n";

                response.setStatus(HttpStatus.NOT_FOUND);
                response.setStatusCode(response.getStatus().getCode());
                response.setStatusPhrase(response.getStatus().getPhrase());

                Date date = new Date();
                response.getHeaders().put(HttpHeaders.DATE, date.toString());
                response.getHeaders().put(HttpHeaders.CONTENT_TYPE, "text/html");
                response.getHeaders().put(HttpHeaders.CONTENT_LENGTH, body.length() + "");

                writer.write((response.getVersion() + " " + response.getStatusCode() + " " + response.getStatusPhrase() + "\r\n").getBytes());

                for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                    writer.write((header.getKey() + ": " + header.getValue() + "\r\n").getBytes());
                }

                writer.write(("\r\n").getBytes());

                writer.write(body.getBytes());

                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭会话
     */
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
