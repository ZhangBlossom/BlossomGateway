package blossom.project.userservice.controller;

import blossom.project.client.api.ApiInvoker;
import blossom.project.client.api.ApiProtocol;
import blossom.project.client.api.ApiService;

import blossom.project.userservice.dto.UserInfo;
import blossom.project.userservice.model.User;
import blossom.project.userservice.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@RestController
@ApiService(serviceId = "backend-user-server", protocol = ApiProtocol.HTTP, patternPath = "/user/**")
public class UserController {
    private static final String SECRETKEY = "zhangblossom";//一般不会直接写代码里，可以用一些安全机制来保护
    private static final String COOKIE_NAME = "blossomgateway-jwt";
    private final UserService userService;

    @ApiInvoker(path = "/login")
    @PostMapping("/login")
    public UserInfo login(@RequestParam("phoneNumber") String phoneNumer,
                          @RequestParam("code") String code,
                          HttpServletResponse response) {
        //User user = userService.login(phoneNumer, code);
        //var jwt = Jwts.builder()
        //    .setSubject(String.valueOf(user.getId()))
        //    .setIssuedAt(new Date())
        //    .signWith(SignatureAlgorithm.HS256, SECRETKEY).compact();
        //response.addCookie(new Cookie(COOKIE_NAME, jwt));
        //return UserInfo.builder()
        //    .id(user.getId())
        //    .nickname(user.getNickname())
        //    .phoneNumber(user.getPhoneNumber()).build();

        var jwt = Jwts.builder()
                .setSubject(String.valueOf(phoneNumer+code))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRETKEY).compact();
        response.addCookie(new Cookie(COOKIE_NAME, jwt));
        return UserInfo.builder()
                .id(Integer.parseInt(phoneNumer+code))
                .nickname("zhangblossom")
                .phoneNumber(phoneNumer).build();
    }

    @GetMapping("/private/user-info")
    public UserInfo getUserInfo(@RequestHeader("userId") String userId) {
        //log.info("userId :{}", userId);
        //var user = userService.getUser(Long.parseLong(userId));
        //return UserInfo.builder()
        //    .id(user.getId())
        //    .nickname(user.getNickname())
        //    .phoneNumber(user.getPhoneNumber()).build();
        log.info("userId :{}", userId);
        return UserInfo.builder()
                .id(Integer.parseInt(userId))
                .nickname("zhangblossom")
                .phoneNumber("1234").build();
    }
}
