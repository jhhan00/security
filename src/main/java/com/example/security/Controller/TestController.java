package com.example.security.Controller;

import com.example.security.Dao.SimpleUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
public class TestController {
    @Autowired
    SimpleUserDao sud;

    @RequestMapping("/")
    public String home(ModelAndView mav) {
        return "Log_Related/home";
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
    public String ChangePassword() {
        return "Log_Related/change_password";
    }

    @GetMapping("/signUp")
    public String SignUpForm(Model model) {
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

        if(isValidate) {
            try {
                int rs = sud.InsertAuthorityInfo(userID);
                if(rs<1) {
                    result += "SignUp_failed. Check your Information and try again. ";
                    isValidate=false;
                } else {
                    result += "SignUp is completed! ";
                }
            } catch (Exception e) {
                e.printStackTrace();
                result += "SignUp_failed. Check your Information and try again. ";
                isValidate=false;
            }
        }

        model.addAttribute("isSuccess",isValidate);
        model.addAttribute("resultMSG",result);

        return "signUp/sign_up_result";
    }
}
