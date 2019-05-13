package cn.edu.xidian.ictt.yk.handler;

import cn.edu.xidian.ictt.yk.http.HttpRequest;
import cn.edu.xidian.ictt.yk.http.HttpResponse;

/**
 * Created by hengyk on 19-5-10
 */
public interface RequestHandler {

    public HttpResponse handle(HttpRequest request);
}
