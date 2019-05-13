package cn.edu.xidian.ictt.yk.util;

/**
 * Created by hengyk on 19-5-10
 */
public class PathUtil {

    /**
     * 规范化访问路径
     *
     * @param path
     * @return
     */
    public static String fixPath(String path) {

        if (path == null) {
            return "/";
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }

    /**
     * 清理掉多余的/
     *
     * @param path
     * @return
     */
    public static String cleanPath(String path){

        if (path == null) {
            return null;
        }

        return path.replaceAll("[/]+", "/");
    }
}
