package cn.edu.xidian.ictt.yk.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hengyk on 19-5-9
 */
public class HttpResponse {

    private int length = 0;

    private boolean readFlag = false;

    /**
     * 默认根路径不可变
     */
    public static final String ROOTPATH = "/home/hengyk/IdeaProjects/httpserverYk/";

    /**
     * 文件名部分可适当扩展
     */
    private String fileName = "index.html";

    /**
     * 协议版本
     */
    private String version = "HTTP/1.1";

    /**
     * 状态
     */
    private HttpStatus status = HttpStatus.OK;

    /**
     * 状态码
     */
    private int statusCode = 200;

    /**
     * 状态描述
     */
    private String statusPhrase = HttpStatus.OK.getPhrase();

    /**
     * 响应头信息
     */
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse() {
        addDefaultHeaders();
    }

    public HttpResponse(HttpStatus status, String statusPhrase) {
        this.status = status;
        this.statusPhrase = statusPhrase;
        addDefaultHeaders();
    }

    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }

    public boolean isReadFlag() {
        return readFlag;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusPhrase() {
        return statusPhrase;
    }

    public void setStatusPhrase(String statusPhrase) {
        this.statusPhrase = statusPhrase;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * 增加文件的访问权限
     *
     * @return
     */
    public String readFilePub() {

        if (readFlag) {
            return readFilePri(fileName);
        }

        return null;
    }

    /**
     * 从服务器中读取文件内容准备输出到客户端中
     *
     * @param fileName
     * @return
     */
    private String readFilePri(String fileName) {

        File file = new File(HttpResponse.ROOTPATH, fileName);
        this.length = (int) file.length();
        headers.put(HttpHeaders.CONTENT_LENGTH, this.length + "");
        if (file.exists() && file.canRead()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 添加默认首部字段
     */
    public void addDefaultHeaders() {
        headers.put(HttpHeaders.DATE, new Date().toString());
        headers.put(HttpHeaders.SERVER, "hengyk");
        headers.put(HttpHeaders.CONTENT_LENGTH, length + "");
        headers.put(HttpHeaders.CONNECTION, "close");
        headers.put(HttpHeaders.CONTENT_TYPE, "text/plain; charset=utf-8");
    }

    /**
     * 辅助函数：输出指定的Content-Type
     *
     * @param name
     * @return
     */
    public static String guessContentTypeFromName(String name) {
        if (name.endsWith(".html")||name.endsWith(".htm")) {
            return "text/html";
        }else if (name.endsWith(".txt")||name.endsWith(".java")) {
            return "text/plain";
        }else if (name.endsWith(".gif")) {
            return "image/gif";
        }else if (name.endsWith(".class")) {
            return "application/octet-stream";
        }else if (name.endsWith(".jpg")||name.endsWith(".jpeg")) {
            return "image/jpeg";
        }else {
            return "text/plain";
        }
    }
}
