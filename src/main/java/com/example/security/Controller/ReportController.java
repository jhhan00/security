package com.example.security.Controller;

import com.example.security.Entity.*;
import com.example.security.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/report")
public class ReportController {
    @Autowired
    TaskRepository taskRepository;

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
        log.info(key+"_:_"+idx);

        List<Task> tlist = taskRepository.findByReportId(idx);
        int i=0;

        while(keys.hasMoreElements()) {
            key = keys.nextElement();
            log.info(key+"_:_"+request.getParameter(key));
            Task task = new Task();
            if(i != tlist.size()) {
                task = tlist.get(i);
                task.setDone(request.getParameter(key));
                i++;
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                String now = LocalDateTime.now().format(dtf);

                task.setReportId(idx);
                task.setUsername(auth.getName());
                task.setSimpleDate(now);
                task.setReportType("Daily");
                task.setReportKind("Done");
                task.setDone(request.getParameter(key));
//                System.out.println(task);
            }
            taskRepository.save(task);
        }

        return "redirect:/report/detail/"+idx;
    }

    @PostMapping("/modify_yearly")
    public String modifyYearlyAction(HttpServletRequest request, Authentication auth) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();

        long idx = Long.parseLong(request.getParameter(key));

        List<Task> tlist = taskRepository.findByReportId(idx);
        int i=0;
//
        while(keys.hasMoreElements()) {
            key = keys.nextElement();
            log.info(key+"_:_"+request.getParameter(key));
            Task task = new Task();
            if(i != tlist.size()) {
                task = tlist.get(i++);
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                String now = LocalDateTime.now().format(dtf);

                task.setReportId(idx);
                task.setUsername(auth.getName());
                task.setSimpleDate(now);
                task.setReportType("Yearly");
                task.setReportKind("project_goal");
            }
            task.setProgress(request.getParameter(key));
            key = keys.nextElement();
            //
            String commentComment = request.getParameter(key);
            if(commentComment.length() >= 2000) commentComment = commentComment.substring(0,1999);
            task.setComment(commentComment);
            //
            key = keys.nextElement();
            task.setProjectStartDate(request.getParameter(key));
            key = keys.nextElement();
            task.setProjectTargetDate(request.getParameter(key));
            key = keys.nextElement();
            task.setQuarter1(request.getParameter(key));
            key = keys.nextElement();
            task.setQuarter2(request.getParameter(key));
            key = keys.nextElement();
            task.setQuarter3(request.getParameter(key));
            key = keys.nextElement();
            task.setQuarter4(request.getParameter(key));

            System.out.println(task);
            taskRepository.save(task);
        }

        return "redirect:/report/detail/"+idx;
    }

    @PostMapping("/modify_monthly")
    public String modifyMonthlyAction(HttpServletRequest request, Authentication auth) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();

        long idx = Long.parseLong(request.getParameter(key));

        List<Task> tlist = taskRepository.findByReportId(idx);
        int i=0;

        while(keys.hasMoreElements()) {
            key = keys.nextElement();
            log.info(key + "_:_" + request.getParameter(key));
            int loc1 = key.indexOf("done");
            int loc2 = key.indexOf("plan");
            int loc3 = key.indexOf("another");
            Task task = new Task();
            if(loc1 != -1) { // this month result
                if(loc3 == -1) { // 이미 있는 done을 수정
                    task = tlist.get(i++);
                } else { // 새롭게 done 추가
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String now = LocalDateTime.now().format(dtf);

                    task.setReportId(idx);
                    task.setUsername(auth.getName());
                    task.setSimpleDate(now);
                    task.setReportType("Monthly");
                }
                task.setExpectedAchievement(null);
                task.setReportKind("Done");
                task.setDone(request.getParameter(key));
                key = keys.nextElement();
                task.setRealAchievement(request.getParameter(key));
                key = keys.nextElement();
                task.setProjectStartDate(request.getParameter(key));
                key = keys.nextElement();
                task.setProjectTargetDate(request.getParameter(key));
                key = keys.nextElement();
                task.setProgress(request.getParameter(key));
                key = keys.nextElement();
                //
                String commentComment = request.getParameter(key);
                if(commentComment.length() >= 2000) commentComment = commentComment.substring(0,1999);
                //
                task.setComment(commentComment);
                key = keys.nextElement();
                task.setQuarter1(request.getParameter(key));
                key = keys.nextElement();
                task.setQuarter2(request.getParameter(key));
                key = keys.nextElement();
                task.setQuarter3(request.getParameter(key));
                key = keys.nextElement();
                task.setQuarter4(request.getParameter(key));
            } else if(loc2 != -1) { // next month plan
                if(loc3 == -1) { // 이미 있는 plan을 수정
                    task = tlist.get(i++);
                } else { // 새롭게 plan 추가
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String now = LocalDateTime.now().format(dtf);

                    task.setReportId(idx);
                    task.setUsername(auth.getName());
                    task.setSimpleDate(now);
                    task.setReportType("Monthly");
                }
                task.setDone(null); task.setRealAchievement(null); task.setProjectStartDate(null); task.setProjectTargetDate(null);
                task.setQuarter1(null); task.setQuarter2(null); task.setQuarter3(null); task.setQuarter4(null);
                task.setReportKind("Next_Month_plan");
                task.setProgress(request.getParameter(key));
                key = keys.nextElement();
                task.setExpectedAchievement(request.getParameter(key));
                key = keys.nextElement();
                //
                String commentComment = request.getParameter(key);
                if(commentComment.length() >= 2000) commentComment = commentComment.substring(0,1999);
                //
                task.setComment(commentComment);
            }
            System.out.println(task);
            taskRepository.save(task);
        }

        return "redirect:/report/detail/"+idx;
    }

    @PostMapping("/modify_weekly")
    public String modifyWeeklyAction(HttpServletRequest request, Authentication auth) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();

        long idx = Long.parseLong(request.getParameter(key));
        List<Task> tlist = taskRepository.findByReportId(idx);
        int i=0;
        log.info("r_id_:_"+idx);

        while(keys.hasMoreElements()) {
            key = keys.nextElement();
            log.info(key + "_:_" + request.getParameter(key));
            int loc1 = key.indexOf("done");
            int loc2 = key.indexOf("plan");
            int loc3 = key.indexOf("another");
            Task task = new Task();
            if(loc1 != -1) { // this week result or done
                if(loc3 == -1) {
                    task = tlist.get(i++);
                } else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String now = LocalDateTime.now().format(dtf);

                    task.setReportId(idx);
                    task.setUsername(auth.getName());
                    task.setSimpleDate(now);
                    task.setReportType("Weekly");
                }
                task.setReportKind("weekly_result");
                task.setProgress(null); task.setExpectedAchievement(null);
                task.setDone(request.getParameter(key));
                key = keys.nextElement();
                task.setRealAchievement(request.getParameter(key));
                key = keys.nextElement();
                task.setComment(request.getParameter(key));
                System.out.println(task);
            } else if(loc2 != -1) { // next week plan
                if(loc3 == -1) {
                    task = tlist.get(i++);
                } else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String now = LocalDateTime.now().format(dtf);

                    task.setReportId(idx);
                    task.setUsername(auth.getName());
                    task.setSimpleDate(now);
                    task.setReportType("Weekly");
                }
                task.setReportKind("weekly_plan");
                task.setDone(null); task.setRealAchievement(null);
                task.setProgress(request.getParameter(key));
                key = keys.nextElement();
                task.setExpectedAchievement(request.getParameter(key));
                key = keys.nextElement();
                task.setComment(request.getParameter(key));
                System.out.println(task);
            }
            taskRepository.save(task);
        }

        return "redirect:/report/detail/"+idx;
    }
}
