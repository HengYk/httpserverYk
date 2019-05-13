package cn.edu.xidian.ictt.yk;

import cn.edu.xidian.ictt.yk.handler.RequestHandler;
import cn.edu.xidian.ictt.yk.http.HttpSession;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hengyk on 19-5-10
 */
public class HengYkServer {

    /**
     *
     */
    private ServerSocket serverSocket;

    /**
     *
     */
    private ExecutorService es;

    /**
     * 判断服务器是否在运行
     */
    private boolean isRunning = false;

    /**
     * 处理器列表
     */
    private List<RequestHandler> handlerList;

    /**
     * 添加Handler
     */
    public void addHandler(RequestHandler handler) {
        if (handlerList == null) {
            this.handlerList = new ArrayList<>();
        }
        handlerList.add(handler);
    }

    /**
     * 端口绑定
     *
     * @param port
     */
    public void bind(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行多线程任务
     *
     * @param runnable
     */
    public void execute(Runnable runnable) {
        if (this.es == null) {
            this.es = Executors.newCachedThreadPool();
        }
        this.es.execute(runnable);
    }

    /**
     * 启动服务
     */
    public void startup() {
        isRunning = true;

        while (isRunning) {
            try {
                Socket socket = serverSocket.accept();
                HttpSession httpSession = new HttpSession(socket);
                // this.execute(new HengYkRunnable(httpSession, handlerList));
                new Thread(new HengYkRunnable(httpSession, handlerList)).start();
            } catch (IOException e) {
                shutdown();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 停止服务
     */
    public void shutdown() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
