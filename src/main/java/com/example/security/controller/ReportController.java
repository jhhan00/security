package com.example.security.controller;

import com.example.security.entity.*;
import com.example.security.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired
    ReportService reportService;

    private int LoginOrNot(Authentication authentication) {
        // log.info("auth : " + authentication);
        if(authentication == null)  return -1;
        else return 1;
    }

    // report - 본인 것만 보기 , 단 관리자는 전체 인원 보기
    @GetMapping
    public String reportList(Model model, Authentication auth) {
        int loginOrNot = LoginOrNot(auth);
        if(loginOrNot == -1) return "redirect:/";

        // System.out.println(auth.getName() + " in report list");
        User user = reportService.authReturn(auth.getName());
        List<Report> reportList = reportService.getReportList(user.getRole(), auth.getName());

        model.addAttribute("list", reportList);
        model.addAttribute("user", user);

        return "report/report_list";
    }

    @GetMapping("/search") // report 검색해서 보기
    public String reportSearch(Authentication auth, Model model, HttpServletRequest request) {
        int loginOrNot = LoginOrNot(auth);
        if(loginOrNot == -1) return "redirect:/";

        User user = reportService.authReturn(auth.getName());

        String type = request.getParameter("search1");
        String find = request.getParameter("searching2");
        log.info(type + " " + find);
        List<Report> reportList = reportService.searchReportList(user.getRole(), auth.getName(), type, find);

        model.addAttribute("list", reportList);
        model.addAttribute("user", user);

        return "report/report_list";
    }

    @GetMapping("/requested_only")
    public String OnlyRequestedReport(Authentication auth, Model model) {
        int loginOrNot = LoginOrNot(auth);
        if(loginOrNot == -1) return "redirect:/";

        User user = reportService.authReturn(auth.getName());
        List<Report> reportList = reportService.getRequestedReportList();

        model.addAttribute("list", reportList);
        model.addAttribute("user", user);

        return "report/report_list";
    }

    @GetMapping("/detail/{reportId}") // report 상세보기
    public String reportView(
            @PathVariable("reportId") long reportId,
            Model model,
            Authentication auth
    ) {
        int loginOrNot = LoginOrNot(auth);
        if(loginOrNot == -1) return "redirect:/";

        User user = reportService.authReturn(auth.getName());
        List<Task> taskList = reportService.getTaskList(reportId);
        Report report = reportService.getReportFromId(reportId);

        model.addAttribute("list", taskList);
        model.addAttribute("info", report);
        model.addAttribute("user", user);

        return "report/report_detail";
    }

    @GetMapping("/request")
    public String reviewReport(@RequestParam("rId")String id) {
        reportService.setReportToRequest(Long.parseLong(id));

        return "redirect:/report/detail/" + id;
    }

    @PostMapping("/delete")
    public String deleteReport(@RequestParam("reportID")String id) {
        reportService.deleteReportAndTask(Long.parseLong(id));

        return "redirect:/report";
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

    @GetMapping("/modify/{type}")
    public String modifyByType(
            @PathVariable("type") String type,
           @RequestParam("reportID")String id,
           Model model,
           Authentication auth
    ) {
        int loginOrNot = LoginOrNot(auth);
        if(loginOrNot == -1) return "redirect:/";

        long r_id = Long.parseLong(id);
        if(reportService.checkRequested(r_id) == -1) return "redirect:/report/detail/" + r_id;

        User user = reportService.authReturn(auth.getName());
        List<Task> taskList = reportService.getTaskList(r_id);

        model.addAttribute("reportID", id);
        model.addAttribute("list", taskList);
        model.addAttribute("user", user);

        if(type.equals("daily")) return "report/modify_daily";
        else if(type.equals("weekly")) return "report/modify_weekly";
        else if(type.equals("monthly")) return "report/modify_monthly";
        else return "report/modify_yearly";
    }

    @PostMapping("/modify_daily")
    public String modifyDailyAction(HttpServletRequest request, Authentication auth) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();
        long idx = Long.parseLong(request.getParameter(key));

        reportService.modifyDailyReport(request, auth.getName());

        return "redirect:/report/detail/"+idx;
    }

    @PostMapping("/modify_yearly")
    public String modifyYearlyAction(HttpServletRequest request, Authentication auth) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();
        long idx = Long.parseLong(request.getParameter(key));

        reportService.modifyYearlyReport(request, auth.getName());

        return "redirect:/report/detail/"+idx;
    }

    @PostMapping("/modify_monthly")
    public String modifyMonthlyAction(HttpServletRequest request, Authentication auth) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();
        long idx = Long.parseLong(request.getParameter(key));

        reportService.modifyMonthlyReport(request, auth.getName());

        return "redirect:/report/detail/"+idx;
    }

    @PostMapping("/modify_weekly")
    public String modifyWeeklyAction(HttpServletRequest request, Authentication auth) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();
        long idx = Long.parseLong(request.getParameter(key));

        reportService.modifyWeeklyReport(request, auth.getName());

        return "redirect:/report/detail/"+idx;
    }
}
