package blossom.project.common.utils;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/24 9:45
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * SystemUtil类
 */
public class SystemUtil {
    public static boolean isLinux() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("linux");
    }

}
