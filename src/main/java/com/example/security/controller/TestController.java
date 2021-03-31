package com.example.security.controller;

import com.example.security.Dao.SimpleUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class TestController {
    @Autowired
    SimpleUserDao sud;

    @RequestMapping("/")
    public String home() {
        return "Log_Related/login-form";
    }

    @ResponseBody
    @RequestMapping("/adminOnly")
    public String adminOnly() {
        return "SecretPage";
    }

    @RequestMapping("/login")
    public String loginForm() {
        return "Log_Related/login-form";
    }

    @GetMapping("/changePW")
    public String ChangePassword(Authentication auth, Model model) {
        String username = auth.getName();
        model.addAttribute("name",username);

        return "Log_Related/change_password";
    }

    @PostMapping("/changePW")
    public String ChangePasswordFunction(Authentication authentication,  Model model, @RequestParam Map<String, String> params) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        String now_pw = params.get("password_now");
        String new_pw = params.get("password_new");
        String check_pw= params.get("password_new_check");
        System.out.println(now_pw + " " + new_pw + " " + check_pw);
        String DB_pw = sud.GetPassword(authentication.getName());

        //back-end validation
        String msg = "";
        boolean isSuccess = true;

        if(!passwordEncoder.matches(now_pw, DB_pw)) { // now_password and DB_password is not equal
            isSuccess = false;
            msg += "Current_Password is not correct. ";
        } if(now_pw.equals(new_pw)) { // new_password and now_password is equal
            isSuccess = false;
            msg += "New_Password should not be equal to Current_Password. ";
        } if(!new_pw.equals(check_pw)) { // new_password and new_password_check is not equal
            isSuccess = false;
            msg += "New_Password and New_Password_Check is not equal. ";
        } if(new_pw.length() < 4) { // new_password is too short
            isSuccess = false;
            msg += "New_Password is too short. ";
        }

        // submit to database
        if(isSuccess) {
            String encrypted = passwordEncoder.encode(new_pw);
            try {
                int rs = sud.UpdatePassword(authentication.getName(), encrypted);
                if(rs < 1) {
                    msg += "Password change is failed. Try again. ";
                    isSuccess = false;
                } else {
                    msg += "Password change is success! ";
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg += "Password change is failed. Try again. ";
                isSuccess = false;
            }
        }
        model.addAttribute("isSuccess", isSuccess);
        model.addAttribute("resultMSG", msg);

        return "Log_Related/change_password_result";
    }

    @GetMapping("/signUp")
    public String SignUpForm() {
        return "signUp/sign_up";
    }

    @PostMapping("/signUp")
    public String SignUpFunction(Model model, @RequestParam Map<String, String> params) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(params); // 값들이 잘 넘어오는지 확인

        String userID = params.get("user_id");
        String userPW = params.get("user_password");
        String checkPW = params.get("user_password_check");
        String realName = params.get("user_realname");
        int loc = userID.indexOf("@pharmcadd.com");
        System.out.println(loc);

        //back-end validation
        String result = "";
        boolean isValidate = true;
        if(loc < 0) { // @pharmcadd.com으로 끝나는지 확인
            result += "user_id should be end with '@pharmcadd.com'. ";
            isValidate = false;
        } if(userPW.length() < 4) {
            result += "user_password should be at least 4 length. ";
            isValidate = false;
        } if(realName.length() <= 0) {
            result += "realname should be entered. ";
            isValidate = false;
        } if(!userPW.equals(checkPW)) {
            result += "password and password_check should be same. ";
            isValidate = false;
        }

        //submit to database
        if(isValidate) {
            params.remove("_csrf");
            params.put("user_password", passwordEncoder.encode(userPW));
            try {
                int rs = sud.InsertUserInfo(params);
                if(rs<1) {
                    result += "SignUp_failed. Duplicate Information or Other Problems. ";
                    isValidate=false;
                } else {
                    result += "SignUp is completed! ";
                }
            } catch(Exception e) {
                e.printStackTrace();
                result += "SignUp_failed. Duplicate Information or Other Problems. ";
                isValidate = false;
            }
        }

        model.addAttribute("isSuccess",isValidate);
        model.addAttribute("resultMSG",result);
        model.addAttribute("ID",userID);

        return "signUp/sign_up_result";
    }
}
