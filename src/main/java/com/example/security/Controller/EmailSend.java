package com.example.security.Controller;

import com.example.security.Extra.GenerateCertNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailSend {
    @Autowired
    JavaMailSender mailSender;

    private String certNumber = "";

    public String getCertNumber() {
        return certNumber;
    }

    public void setCertNumber(String certNumber) {
        this.certNumber = certNumber;
    }
}
