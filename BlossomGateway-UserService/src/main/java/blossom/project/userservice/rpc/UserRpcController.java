package blossom.project.userservice.rpc;

import blossom.project.userservice.dto.UserInfo;
import blossom.project.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class UserRpcController {
    private final UserService userService;

    @GetMapping("/user/rpc/users/{userId}/info")
    public UserInfo getUserInfo(@PathVariable("userId") long userId) {
        var user = userService.getUser(userId);
        return UserInfo.builder()
            .id(user.getId())
            .phoneNumber(user.getPhoneNumber())
            .nickname(user.getNickname())
            .build();
    }
}
