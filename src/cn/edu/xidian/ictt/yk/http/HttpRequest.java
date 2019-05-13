package cn.edu.xidian.ictt.yk.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Created by hengyk on 19-5-9
 */
public class HttpRequest {

    private List<String> rawData;

    /**
     * 方法
     */
    private String method;

    /**
     * URI
     */
    private String uri;

    /**
     * URI携带的参数(Get请求)
     */
    private Map<String, String> para;

    /**
     * 协议版本
     */
    private String version;

    /**
     * 请求头部信息
     */
    private Map<String, String> headers;

    /**
     * 请求实体部分携带的参数（Post请求）
     */
    private Map<String, Object> postData;

    /**
     * 请求中携带的Cookie
     */
    private Map<String, HttpCookie> cookies;

    public HttpRequest() {
    }

    public String getUri() {
        return uri;
    }

    public void setRawData(List<String> rawData) {
        this.rawData = rawData;
        processRawData();
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setCookies(Map<String, HttpCookie> cookies) {
        this.cookies = cookies;
    }

    /**
     * 对来自客户端的请求数据进行精加工
     */
    public void processRawData() {

        StringBuilder sb = new StringBuilder();
        for (String str : rawData) {
            sb.append(str);
        }
        String raw = sb.toString();

        // 第一步，请求的方法是什么
        StringTokenizer tokenizer = new StringTokenizer(raw);
        this.method = tokenizer.nextToken().toUpperCase();

        // 第二步，请求的URI是什么
        this.uri = tokenizer.nextToken();
        int idx = this.uri.indexOf("?");
        if (idx != -1) {
            String queryString = this.uri.substring(idx + 1);
            this.para = splitPara(queryString);
            this.uri = uri.substring(0, idx);
        }

        // 第三步，请求的协议是什么
        this.version = tokenizer.nextToken();

//        System.out.println(this.method + " " + this.uri + " " + this.version + "\r\n");

        // 第四步，首部字段有哪些
        String[] lines = raw.split("\r\n");
        Map<String, String> headersTemp = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String[] keyVal = lines[i].split(":", 2);
            headersTemp.put(keyVal[0], keyVal[1]);
        }
        setHeaders(headersTemp);

//        for (Map.Entry<String, String> header: headers.entrySet()) {
//            System.out.println(header.getKey() + ": " + header.getValue() + "\r\n");
//        }

        // 第五步，如果首部信息包含Cookie，则保存
        if (headers.containsKey(HttpHeaders.COOKIE)) {
            Map<String, HttpCookie> cookiesTemp = new HashMap<>();
            StringTokenizer tok = new StringTokenizer(headers.get(HttpHeaders.COOKIE), ";");
            while (tok.hasMoreTokens()) {
                String token = tok.nextToken();
                int eqIdx = token.indexOf('=');
                if (eqIdx == -1) {
                    continue;
                }
                String key = token.substring(0, eqIdx);
                String value = token.substring(eqIdx + 1);
                cookiesTemp.put(key, new HttpCookie(key, value));
            }
            setCookies(cookiesTemp);

//            for (Map.Entry<String, HttpCookie> cookieEntry: cookies.entrySet()) {
//                System.out.println(cookieEntry.getKey() + ": " + cookieEntry.getValue().getValue());
//            }
        }
    }

    /**
     * 提取URL中携带的参数
     *
     * @param queryString
     * @return
     */
    public Map<String, String> splitPara(String queryString) {

        Map<String, String> map = new HashMap<>();

        String[] splits = "&".split(queryString);
        for (String str : splits) {
            int idx = str.indexOf("=");
            try {
                if (idx != -1) {
                    map.put(URLDecoder.decode(str.substring(0, idx), "UTF-8"),
                            URLDecoder.decode(str.substring(idx + 1), "UTF-8"));
                } else {
                    map.put(URLDecoder.decode(str, "UTF-8"), "true");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return map;
    }
}
