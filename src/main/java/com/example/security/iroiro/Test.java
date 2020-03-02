package com.example.security.iroiro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class Test {
    @Autowired
    SimpleMessageDAO smd;

    @RequestMapping("/select")
    public List<Map<String, ?>> getMessages() {
        return smd.selectAll();
    }
}
