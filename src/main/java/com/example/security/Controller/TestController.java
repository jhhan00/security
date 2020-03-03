package com.example.security.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {
    @RequestMapping("/")
    public String home(ModelAndView mav) {
        return "home";
    }

    @ResponseBody
    @RequestMapping("/adminOnly")
    public String adminOnly() {
        return "SecretPage";
    }

    @RequestMapping("/login")
    public String loginForm() {
        return "login-form";
    }
}
