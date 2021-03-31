package com.example.security.service;

import com.example.security.entity.Report;
import com.example.security.entity.ReportRepository;
import com.example.security.entity.User;
import com.example.security.entity.UserRepository;
import com.example.security.extra.GenerateCertNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;

@Slf4j
@Service
public class EmailSendService {
    @Autowired
    JavaMailSender mailSender;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    UserRepository userRepository;

    public long sendEmailAndChangeState(HttpServletRequest request) throws MessagingException {
        Enumeration<String> line = request.getParameterNames();
        long id = -1;

        while(line.hasMoreElements()){
            String tmp = line.nextElement();
            log.info(tmp + " _ " + request.getParameter(tmp));
            if(tmp.equals("report_id"))
                id = Long.parseLong(request.getParameter(tmp));
            else {
                String word = "";
                if(tmp.equals("Approve")) word = "Approved";
                else if(tmp.equals("Reject")) word = "Rejected";

                Report report = reportRepository.findByReportId(id);
                MimeMessage message = mailSender.createMimeMessage();
                message.setSubject(report.getReportTitle() + " Result : " + word);
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(report.getUsername()));
                message.setText("Your " + report.getReportTitle() + " is " + word + ".\nCheck Your Report.");
                message.setSentDate(new Date());
                mailSender.send(message);

                report.setState(word);
                reportRepository.save(report);
            }
        }

        return id;
    }

    public String sendEmailToNewbie(String id) throws MessagingException {
        GenerateCertNumber ge = new GenerateCertNumber();
        String randomNum = ge.executeGenerate();
        System.out.println(randomNum);

        MimeMessage message = mailSender.createMimeMessage();
        message.setSubject("회원가입 인증");
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(id));
        message.setText("인증번호는 " + randomNum + " 입니다.");
        message.setSentDate(new Date());
        mailSender.send(message);

        return randomNum;
    }

    public String checkNumberAndReturnString(String email, String send, String check) {
        send = send.trim();
        check = check.trim();
        log.info("email      : " + email);
        log.info("sendNumber : " + send);
        log.info("check      : " + check);

        String msg;
        if(check.equals(send)) {
            User user = userRepository.findByUsername(email);
            user.setEnabled(1);
            userRepository.save(user);
            msg = "success";
        } else {
            msg = "wrong";
        }
        return msg;
    }
}
