package com.example.security.Controller;

import com.example.security.Entity.*;
import com.example.security.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/report")
public class ReportCreateController {

    @Autowired
    ReportService reportService;

    private int LoginOrNot(Authentication authentication) {
        // log.info("auth : " + authentication);
        if(authentication == null)  return -1;
        else return 1;
    }

    @GetMapping("/create/daily")
    public String createDaily(Model model, Authentication auth, HttpServletRequest request) {
        int loginOrNot = LoginOrNot(auth);
        if(loginOrNot == -1) return "redirect:/";

        User user = reportService.authReturn(auth.getName());
        List<Map<String, Object>> list = new ArrayList<>();

        model.addAttribute("task", list);
        model.addAttribute("user", user);
        model.addAttribute("oldUrl", request.getHeader("referer"));

        return "report/create_daily";
    }

    @PostMapping("/create/daily")
    public String createDailyAction(Authentication auth, HttpServletRequest request) {
        reportService.createDailyReport(request, auth.getName());

        return "redirect:/report";
    }

    @GetMapping("/create/weekly")
    public String createWeekly(Authentication auth, Model model, HttpServletRequest request) {
        int loginOrNot = LoginOrNot(auth);
        if(loginOrNot == -1) return "redirect:/";

        User user = reportService.authReturn(auth.getName());
        List<Report> reportList = reportService.getReportByTypeAndUsername("Weekly", auth.getName());

        long idx = -1;
        boolean isNull = true; // 아무 것도 없으면 처음 주간 리포트 쓰는 경우
        if(reportList.size() != 0) {
            idx = reportList.get(reportList.size()-1).getReportId();
            isNull = false; // 있다면 주간 리포트를 한 번 이상 쓴 경우
        }
        List<Task> taskList = new ArrayList<>();
        if(idx != -1) taskList = reportService.getTaskList(idx);

        model.addAttribute("task", taskList);
        model.addAttribute("user", user);
        model.addAttribute("Valid", isNull);
        model.addAttribute("oldUrl", request.getHeader("referer"));

        return "report/create_weekly";
    }

    @PostMapping("/create/weekly")
    public String createWeeklyAction(Authentication auth, HttpServletRequest request) {
        reportService.createWeeklyReport(request, auth.getName());

        return "redirect:/report";
    }

    @GetMapping("/create/monthly")
    public String createMonthly(Authentication auth, Model model, HttpServletRequest request) {
        int loginOrNot = LoginOrNot(auth);
        if(loginOrNot == -1) return "redirect:/";

        User user = reportService.authReturn(auth.getName());
        List<Report> reportList = reportService.getReportByTypeAndUsername("Monthly", auth.getName());

        long idx = -1;
        boolean isNull = true;
        if(reportList.size() != 0) {
            idx = reportList.get(reportList.size()-1).getReportId();
            isNull = false;
        }
        List<Task> taskList = new ArrayList<>();
        if(idx != -1) taskList = reportService.getTaskByIdAndReportKind(idx, "Next_Month_plan");

        model.addAttribute("user", user);
        model.addAttribute("task", taskList);
        model.addAttribute("boolValue", isNull);
        model.addAttribute("oldUrl", request.getHeader("referer"));

        return "report/create_monthly";
    }

    @PostMapping("/create/monthly")
    public String createMonthlyAction(Authentication auth, HttpServletRequest request) {
        reportService.createMonthlyReport(request, auth.getName());

        return "redirect:/report";
    }

    @GetMapping("/create/yearly")
    public String createProjectGoal(Authentication auth, Model model, HttpServletRequest request) {
        int loginOrNot = LoginOrNot(auth);
        if(loginOrNot == -1) return "redirect:/";

        User user = reportService.authReturn(auth.getName());
        List<Task> taskList = new ArrayList<>();

        model.addAttribute("user", user);
        model.addAttribute("task", taskList);
        model.addAttribute("oldUrl", request.getHeader("referer"));

        return "report/create_yearly";
    }

    @PostMapping("/create/yearly")
    public String createProjectGoalAction(Authentication auth, HttpServletRequest request) {
        reportService.createYearlyReport(request, auth.getName());

        return "redirect:/report";
    }

    @GetMapping("/create/notice")
    public String createNotice(Authentication auth, Model model, HttpServletRequest request) {
        int loginOrNot = LoginOrNot(auth);
        if(loginOrNot == -1) return "redirect:/";

        User user = reportService.authReturn(auth.getName());
        List<Task> taskList = new ArrayList<>();

        model.addAttribute("user", user);
        model.addAttribute("task", taskList);
        model.addAttribute("oldUrl", request.getHeader("referer"));

        return "/report/create_notice";
    }

    @PostMapping("/create/notice")
    public String createNoticeAction(Authentication auth, HttpServletRequest request) {
        reportService.createNotice(request, auth.getName());

        return "redirect:/report";
    }
}
