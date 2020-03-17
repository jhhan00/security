package com.example.security.Controller;

import com.example.security.Dao.ReportDao;
import com.example.security.Entity.Report;
import com.example.security.Entity.ReportRepository;
import com.example.security.Entity.Task;
import com.example.security.Entity.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/report")
public class ReportController {
    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ReportDao rd;

    @Autowired
    TaskRepository taskRepository;

    @GetMapping // report 전체보기
    public String reportList(Model model, Authentication auth) {
        List<Report> list = reportRepository.findAll();
        model.addAttribute("list",list);

        System.out.println(auth.getName() + " in report list");
        Map<String, String> authority = rd.getUserInfo(auth.getName());
        model.addAttribute("authority", authority);

        return "report/report_list";
    }

    @GetMapping("/detail/{reportId}") // report 상세보기
    public String reportView(@PathVariable("reportId") long reportid, Model model, Authentication auth) {
        System.out.println(reportid + ". " + auth.getName() + " in detail");

        Map<String, String> authority = rd.getUserInfo(auth.getName());
        model.addAttribute("authority",authority);
        List<Task> list = this.taskRepository.findByReportId(reportid);
        System.out.println(list);
        model.addAttribute("list",list);
        Report rp = this.reportRepository.findByReportId(reportid);
        System.out.println(rp);
        model.addAttribute("info",rp);

        return "report/report_detail";
    }

    @GetMapping("/create/daily")
    public String createDaily(Model model, Authentication auth) {
        Map<String, String> authority = rd.getUserInfo(auth.getName());
        model.addAttribute("authority", authority);

        List<Map<String, Object>> list = null;
        model.addAttribute("task",list);

        return "report/create_daily";
    }

    @PostMapping("/create/daily")
    public String createDailyAction(Authentication auth, HttpServletRequest request) {
        Enumeration<String> keys = request.getParameterNames();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        String nowDate = now.format(dtf);

        Report report = new Report();
        report.setUsername(auth.getName());
        report.setReportType("Daily");
        report.setSimpleDate(nowDate);
        report.setWriteDate(now);
        report.setUpdatedTime(now);
        System.out.println(report);
        reportRepository.save(report);

        System.out.println(report.getReportId());
        long r_id = report.getReportId();

        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            log.info(key + ": " + request.getParameter(key));
            Task task = new Task();
            task.setReportId(r_id);
            task.setUsername(auth.getName());
            task.setSimpleDate(nowDate);
            task.setReportType("Daily");
            task.setReportKind("Done");
            task.setDone(request.getParameter(key));
            System.out.println(task);
            taskRepository.save(task);
        }

        return "redirect:/report";
    }

    @GetMapping("/create/weekly")
    public String createWeekly(Authentication auth, Model model) {
        Map<String, String> authority = rd.getUserInfo(auth.getName());
        model.addAttribute("authority", authority);

        List<Map<String, Object>> list = null;
        model.addAttribute("task", list);

        return "report/create_weekly";
    }

    @PostMapping("/create/weekly")
    public String createWeeklyAction(Authentication auth, @RequestParam("A")String a) {
        log.info(a);
        return "redirect:/report";
    }
}
