package blossom.project.core.jwt;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.Test;

import java.util.Date;

/**
 * @author: ZhangBlossom
 * @date: 2023/11/6 16:25
 * @contact: QQ:4602197553
 * @contact: WX:qczjhczs0114
 * @blog: https://blog.csdn.net/Zhangsama1
 * @github: https://github.com/ZhangBlossom
 * JwtTest类
 */
public class JwtTest {
    @Test
    public void jwt() {
        // 定义一个密钥用于签名 JWT
        String security = "zhangblossom";

        // 创建一个 JWT Token
        String token = Jwts.builder()
                .setSubject("1314520") // 设置JWT的主题，通常为用户的唯一标识
                .setIssuedAt(new Date()) // 设置JWT的签发时间，通常为当前时间
                .signWith(SignatureAlgorithm.HS256, security) // 使用HS256算法签名JWT，使用密钥"security"
                .compact(); // 构建JWT并返回字符串表示

        // 打印生成的JWT Token
        System.out.println(token);

        // 解析JWT Token
        Jwt jwt = Jwts.parser().setSigningKey(security).parse(token);

        // 打印解析后的JWT对象
        System.out.println(jwt);

        // 从JWT中获取主题（Subject）信息
        String subject = ((DefaultClaims) jwt.getBody()).getSubject();

        // 打印JWT中的主题信息
        System.out.println(subject);
    }

}
