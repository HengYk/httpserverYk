package cn.edu.xidian.ictt.yk;

import cn.edu.xidian.ictt.yk.handler.impl.FileRequestHandler;

/**
 * Created by hengyk on 19-5-9
 */
public class Test {

    public static void main(String[] args) {
        HengYkServer hys = new HengYkServer();
        hys.bind(8080);

        // 简单文件请求服务
        hys.addHandler(new FileRequestHandler("/home/hengyk/IdeaProjects/httpserverYk"));
        hys.startup();
    }
}
