package com.example.security.Controller;

import com.example.security.Dao.SimpleUserDao;
import com.example.security.Entity.Report;
import com.example.security.Entity.ReportRepository;
import com.example.security.Extra.GenerateCertNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Enumeration;

@Slf4j
@Controller
public class EmailSendController {
    @Autowired
    SimpleUserDao sud;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ReportRepository reportRepository;

    private static String certNumber = "";

    @PostMapping("/change_state")
    public String requestEmailAndChangeState(HttpServletRequest request) throws MessagingException {
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
        return "redirect:/report/detail/" + id;
    }

    @GetMapping("/request")
    public String SendEmail(@RequestParam("UserId") String id, Model model) throws MessagingException {
        System.out.println(id);
        GenerateCertNumber ge = new GenerateCertNumber();
        certNumber = ge.executeGenerate();
        System.out.println(certNumber);

        MimeMessage message = mailSender.createMimeMessage();
        message.setSubject("회원가입 인증");
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(id));
        message.setText("인증번호는 " + certNumber + " 입니다.");
        message.setSentDate(new Date());
        mailSender.send(message);

        model.addAttribute("UserId",id);

        return "signUp/cert";
    }

    @PostMapping("/request_check")
    public String CheckNumber(@RequestParam("check_number") String check, @RequestParam("UserId") String id, Model model) {
        System.out.println("certNumber : "+certNumber);
        System.out.println("check      : "+check);
        System.out.println(id);
        String msg = "";
        boolean success = true;
        if(check.equals(certNumber)) {
            System.out.println("Equal Numbers");

            //submit to Database
            try {
                int rst = sud.UpdateEnabled(id);
                if(rst < 1) {
                    msg += "Error in Database..";
                    success = false;
                } else {
                    msg += "Complete!";
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg = "Somthing Wrong... Ask to Administrator";
                success = false;
            }
        } else {
            System.out.println("Error");
            msg += "Try Again...";
            success = false;
        }
        model.addAttribute("msg",msg);
        model.addAttribute("isSuccess",success);
        model.addAttribute("UserId",id);

        return "signUp/cert_result";
    }
}
