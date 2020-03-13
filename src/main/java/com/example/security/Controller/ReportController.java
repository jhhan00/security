package com.example.security.Controller;

import com.example.security.Dao.ReportDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/report")
public class ReportController {
    @Autowired
    ReportDao rd;

    @GetMapping
    public String reportList(Model model, Authentication auth) {
        List<Map<String, Object>> list = rd.getReportList();
        model.addAttribute("list", list);

        System.out.println(auth.getName() + " in list");
        Map<String, String> authority = rd.getUserInfo(auth.getName());
        model.addAttribute("authority", authority);

        return "report/report_list";
    }

    @GetMapping("/detail/{reportNo}")
    public String reportView(@PathVariable("reportNo") int reportno, Model model, Authentication auth) {
        System.out.println(reportno + ". " + auth.getName() + " in detail");

        List<Map<String, Object>> list = rd.getReportTask(reportno);
        model.addAttribute("list",list);
        Map<String, String> authority = rd.getUserInfo(auth.getName());
        model.addAttribute("authority", authority);
        Map<String, Object> info = rd.getReportInfo(reportno);
        model.addAttribute("info",info);

        return "report/report_detail";
    }

    @GetMapping("/create/daily")
    public String reportCreate(Authentication auth, Model model) {
        Map<String, String> authority = rd.getUserInfo(auth.getName());
        model.addAttribute("authority", authority);

        return "report/create_daily";
    }

    @PostMapping("/create/daily")
    public String reportCreateAction(@RequestParam Map<String, String> params) {
        System.out.println(params);
        String [] done = {};
        String [] etc = {};
        for(int i=0; i<2; i++){
            done[i] = params.get("Done" + i);
            etc[i] = params.get("Etc" + i);
        }
        return "redirect:/report";
    }
}
