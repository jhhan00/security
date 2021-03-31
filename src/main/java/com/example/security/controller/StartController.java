package com.example.security.controller;

import com.example.security.service.StartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class StartController {

    @Autowired
    StartService startService;

    @RequestMapping("/")
    public String home() {
        return "Log_Related/login-form";
    }

    @RequestMapping("/login")
    public String loginForm() {
        return "Log_Related/login-form";
    }

    @GetMapping("/changePW")
    public String ChangePassword(Authentication auth, Model model) {
        String username = auth.getName();
        model.addAttribute("name", username);

        return "Log_Related/change_password";
    }

    @PostMapping("/changePW")
    public String ChangePasswordFunction(Authentication auth,  Model model, @RequestParam Map<String, String> params) {
        String message = startService.changePassword(params, auth.getName());
        boolean isSuccess = message.equals("success");

        model.addAttribute("isSuccess", isSuccess);
        model.addAttribute("resultMSG", message);

        return "Log_Related/change_password_result";
    }

    @GetMapping("/signUp")
    public String SignUpForm() {
        return "signUp/sign_up";
    }

    @PostMapping("/signUp")
    public String SignUpFunction(Model model, @RequestParam Map<String, String> params) {
        String userID = params.get("user_id");
        String result = startService.signUp(params);
        boolean isValidate = result.equals("success");

        model.addAttribute("isSuccess", isValidate);
        model.addAttribute("resultMSG", result);
        model.addAttribute("ID", userID);

        return "signUp/sign_up_result";
    }
}
