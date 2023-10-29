package blossom.project.common.constant;

import java.util.regex.Pattern;

/**
 * @author: ZhangBlossom
 * @date: 2023/10/23 19:57
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * Test类
 */
public interface BasicConst {

    String DEFAULT_CHARSET = "UTF-8";

    String PATH_SEPARATOR = "/";

    String PATH_PATTERN = "/**";

    String QUESTION_SEPARATOR = "?";

    String ASTERISK_SEPARATOR = "*";

    String AND_SEPARATOR = "&";

    String EQUAL_SEPARATOR = "=";

    String BLANK_SEPARATOR_1 = "";
    
    String BLANK_SEPARATOR_2 = " ";
    
    String COMMA_SEPARATOR = ",";

    String SEMICOLON_SEPARATOR = ";";

    String DOLLAR_SEPARATOR = "$";

    String PIPELINE_SEPARATOR = "|";
    
    String BAR_SEPARATOR = "-";

    String COLON_SEPARATOR = ":";
    
    String DIT_SEPARATOR = ".";

    String HTTP_PREFIX_SEPARATOR = "http://";

    String HTTPS_PREFIX_SEPARATOR = "https://";

    String HTTP_FORWARD_SEPARATOR = "X-Forwarded-For";

    Pattern PARAM_PATTERN = Pattern.compile("\\{(.*?)\\}");

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String ENABLE = "Y";

    String DISABLE = "N";

}