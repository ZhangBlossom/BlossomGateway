package blossom.project.userservice.service;

import blossom.project.userservice.dao.UserDao;
import blossom.project.userservice.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    public User getUser(long userId) {
        return userDao.findById(userId).get();
    }

    public User login(String phoneNumer, String code) {
        //这里需要校验一下验证码，一般可以把验证码放在redis
        return userDao.findByPhoneNumber(phoneNumer)
            .orElseGet(() -> userDao.save(User.builder()
                    .nickname(phoneNumer)
                .phoneNumber(phoneNumer).build()));
    }
}
