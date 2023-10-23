package blossom.gateway.common.constant;

import java.util.regex.Pattern;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 16:06
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * BasicConstant类
 */
public class BasicConstant {
    public static final String HTTP_PREFIX_SEPARATOR = "http://";
    public static final String HTTPS_PREFIX_SEPARATOR = "https://";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String HTTP_FORWARD_SEPARATOR = "https://";
    public static final Pattern PARAM_PATTERN = Pattern.compile("\\{(.*?)\\}");
    public static final String ENABLE = "Y";
    public static final String DISABLE = "N";
    public static final String BAR_SEPARATOR = "-";
    public static final String COLON_SEPARATOR = ":";
    public static final String DOT_SEPARATOR = ".";
}
