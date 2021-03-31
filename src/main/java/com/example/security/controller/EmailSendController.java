package com.example.security.controller;

import com.example.security.service.EmailSendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class EmailSendController {

    @Autowired
    EmailSendService emailSendService;

    @PostMapping("/change_state")
    public String requestEmailAndChangeState(HttpServletRequest request) throws MessagingException {
        long id = emailSendService.sendEmailAndChangeState(request);
        return "redirect:/report/detail/" + id;
    }

    @GetMapping("/request")
    public String SendEmail(@RequestParam("UserId") String email, Model model) throws MessagingException {
        log.info("email = " + email);
        String result = emailSendService.sendEmailToNewbie(email);

        model.addAttribute("UserId", email);
        model.addAttribute("sendNumber", result);

        return "signUp/cert";
    }

    @PostMapping("/request_check")
    public String CheckNumber(
            @RequestParam("check_number") String check,
            @RequestParam("UserId") String email,
            @RequestParam("sendNumber")String send,
            Model model
    ) {
        String message = emailSendService.checkNumberAndReturnString(email, send, check);
        boolean success = true;
        if(message.equals("wrong")) success = false;

        model.addAttribute("msg", message);
        model.addAttribute("isSuccess", success);
        model.addAttribute("UserId", email);

        return "signUp/cert_result";
    }
}
