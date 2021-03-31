package com.example.security.service;

import com.example.security.entity.User;
import com.example.security.entity.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class StartService {
    @Autowired
    UserRepository userRepository;

    public String signUp(Map<String, String> params) {
//        log.info("params = " + params.toString());
        String userID = params.get("user_id");
        String userPW = params.get("user_password");
        String checkPW = params.get("user_password_check");
        String realName = params.get("user_realname");
        int loc = userID.indexOf("@pharmcadd.com");

        //back-end validation
        String result = "";
        boolean isValidate = true;
        if(loc < 0) { // @pharmcadd.com으로 끝나는지 확인
            result += "user_id should be end with '@pharmcadd.com'. ";
            isValidate = false;
        } if(userPW.length() < 4) { // 비밀번호가 너무 짧지 않은지 확인
            result += "user_password should be at least 4 length. ";
            isValidate = false;
        } if(realName.length() <= 0) { // 실제 이름이 입력되어야 한다.
            result += "real name should be entered. ";
            isValidate = false;
        } if(!userPW.equals(checkPW)) { // 비밀번호와 비밀번호 확인이 같아야 한다.
            result += "password and password_check should be same. ";
            isValidate = false;
        }

        if(!isValidate) return result;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(userPW);
        User newUser = new User();
        newUser.setUsername(userID);
        newUser.setPassword(encryptedPassword);
        newUser.setRealName(realName);
        newUser.setEnabled(0);
        newUser.setRole("USER");
        userRepository.save(newUser);

        return "success";
    }


}
