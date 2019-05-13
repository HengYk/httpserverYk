package cn.edu.xidian.ictt.yk;

import cn.edu.xidian.ictt.yk.handler.RequestHandler;
import cn.edu.xidian.ictt.yk.http.HttpRequest;
import cn.edu.xidian.ictt.yk.http.HttpResponse;
import cn.edu.xidian.ictt.yk.http.HttpSession;

import java.util.List;

/**
 * 一个子线程
 *
 * Created by hengyk on 19-5-10
 */
public class HengYkRunnable implements Runnable {

    /**
     * 一次请求建立一个会话
     */
    private HttpSession httpSession;

    /**
     * 一个请求可以被多个Handler进行处理
     */
    private List<RequestHandler> handlerList;

    public HengYkRunnable(HttpSession httpSession, List<RequestHandler> handlerList) {
        this.httpSession = httpSession;
        this.handlerList = handlerList;
    }

    @Override
    public void run() {

        // 先建一个空的请求对象
        HttpRequest httpRequest = new HttpRequest();

        // 在会话中接收来自客户端的请求数据
        httpSession.receiveRequestData(httpRequest);

        // favicon.ico很鸡肋，哈哈
        if (httpRequest.getUri().equals("/favicon.ico")) {
            return;
        }

        for (RequestHandler handler: handlerList) {
            // Handler处理请求对象中的数据，并返回一个响应对象
            HttpResponse httpResponse = handler.handle(httpRequest);

            // 在会话中发送来自服务端的响应数据
            httpSession.sendReponseData(httpResponse);

            // 完成后关闭会话
            httpSession.close();
        }
    }
}
