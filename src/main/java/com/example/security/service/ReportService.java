package com.example.security.service;

import com.example.security.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    private String getLocalDateFormat(LocalDateTime ldt) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        return ldt.format(dtf);
    }

    public User authReturn(String username) {
        User user = userRepository.findByUsername(username);
        user.setPassword(null);
        return user;
    }

    public List<Report> getReportList(String role, String username) {
        List<Report> reportList;
        if(role.equals("USER")) {
            reportList = reportRepository.findByUsernameOrderByUpdatedTimeDesc(username);
            List<Report> noticeList = reportRepository.findByReportTypeOrderByWriteDateDesc("Notice");
            reportList.addAll(noticeList);
        } else {
            reportList = reportRepository.findAllByOrderByUpdatedTimeDesc();
        }
        return reportList;
    }

    public List<Report> searchReportList(String role, String username, String type, String find) {
        List<Report> reportList = new ArrayList<>();
        if(role.equals("USER")) { // User인 경우
            switch (type) {
                case "username":
                    reportList = reportRepository.findByUsernameStartsWithOrderByWriteDateDesc(find);
                    break;
                case "reportTitle":
                    reportList = reportRepository.findByUsernameAndReportTitleContainingOrderByWriteDate(username, find);
                    break;
                case "time":
                    List<Report> rl = reportRepository.findByUsernameOrderByUpdatedTimeDesc(username);
                    for(Report r : rl) {
                        if(find.equals(r.getWriteDate().toLocalDate().toString()))
                            reportList.add(r);
                    }
                    break;
                case "type":
                    reportList = reportRepository.findByUsernameAndReportTypeOrderByWriteDateDesc(username, find);
                    break;
                case "state":
                    reportList = reportRepository.findByUsernameAndStateOrderByWriteDateDesc(username, find);
                    break;
            }
        } else { // Admin인 경우
            switch (type) {
                case "username":
                    reportList = reportRepository.findByUsernameStartsWithOrderByWriteDateDesc(find);
                    break;
                case "reportTitle":
                    reportList = reportRepository.findByReportTitleContainingOrderByWriteDateDesc(find);
                    break;
                case "time":
                    List<Report> rl = reportRepository.findAllByOrderByUpdatedTimeDesc();
                    for (Report rp : rl) {
                        if (find.equals(rp.getWriteDate().toLocalDate().toString())) {
                            reportList.add(rp);
                        }
                    }
                    break;
                case "type":
                    reportList = reportRepository.findByReportTypeOrderByWriteDateDesc(find);
                    break;
                case "state":
                    reportList = reportRepository.findByStateOrderByWriteDateDesc(find);
                    break;
            }
        }
        return reportList;
    }

    public Report getReportFromId(long reportId) {
        return reportRepository.findByReportId(reportId);
    }

    public List<Report> getRequestedReportList() {
        return reportRepository.findByStateOrderByUpdatedTimeDesc("Requested");
    }

    public List<Report> getReportByTypeAndUsername(String type, String username) {
        return reportRepository.findByReportTypeAndUsername(type, username);
    }

    public List<Task> getTaskList(long reportId) {
        return taskRepository.findByReportId(reportId);
    }

    public void setReportToRequest(long id) {
        Report report = reportRepository.findByReportId(id);
        report.setState("Requested");
        reportRepository.save(report);
    }

    public void deleteReportAndTask(long id) {
        List<Task> taskList = taskRepository.findByReportId(id);
        for(Task t : taskList) {
            taskRepository.delete(t);
        }
        Report report = reportRepository.findByReportId(id);
        reportRepository.delete(report);
    }

    public int checkRequested(long id) {
        Report report = reportRepository.findByReportId(id);
        if(report.getState().equals("Requested") || report.getState().equals("Approved"))
            return -1;
        return 1;
    }

    public List<Task> getTaskByIdAndReportKind(long id, String kind) {
        return taskRepository.findByReportIdAndReportKind(id, kind);
    }

    public void createDailyReport(HttpServletRequest request, String username) {
        Enumeration<String> keys = request.getParameterNames();
        LocalDateTime now = LocalDateTime.now();
        String nowDate = getLocalDateFormat(now);

        //Report Save
        Report report = new Report();
        report.setUsername(username);
        report.setReportType("Daily");
        report.setSimpleDate(nowDate);
        report.setWriteDate(now);
        report.setUpdatedTime(now);
        report.setState("Waiting");
        report.setReportTitle(nowDate + "_Daily_Report");
        reportRepository.save(report);

        System.out.println(report);
        long r_id = report.getReportId();

        //Task Save
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
//            log.info(key + ": " + request.getParameter(key));
            Task task = new Task();
            task.setReportId(r_id);
            task.setUsername(username);
            task.setSimpleDate(nowDate);
            task.setReportType("Daily");
            task.setReportKind("Done");
            //
            String done = request.getParameter(key);
            if(request.getParameter(key).length() >= 2000) {
                done = done.substring(0,2000);
            }
            task.setDone(done);
            //
            log.info("task=" + task);
            taskRepository.save(task);
        }
    }

    public void createWeeklyReport(HttpServletRequest request, String username) {
        Enumeration<String> keys = request.getParameterNames();
        LocalDateTime now = LocalDateTime.now();
        String nowDate = getLocalDateFormat(now);

        //Report Save
        Report report = new Report();
        report.setUsername(username);
        report.setReportType("Weekly");
        report.setSimpleDate(nowDate);
        report.setWriteDate(now);
        report.setUpdatedTime(now);
        report.setState("Waiting");
        report.setReportTitle(nowDate + "_Weekly_Report");
        reportRepository.save(report);

        System.out.println(report);
        long r_id = report.getReportId();

        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            int loc1 = key.indexOf("done");
            int loc2 = key.indexOf("plan");
//            log.info(key + " : " + request.getParameter(key) + " -- loc1 : " + loc1 + ", loc2 : " + loc2);
            Task task = new Task();
            task.setReportId(r_id);
            task.setUsername(username);
            task.setSimpleDate(nowDate);
            task.setReportType("Weekly");
            if(loc1 == 0) {
                task.setReportKind("weekly_result");
                task.setDone(request.getParameter(key));
                key = keys.nextElement();
                task.setRealAchievement(request.getParameter(key));
            } else if(loc2 == 0) {
                task.setReportKind("weekly_plan");
                task.setProgress(request.getParameter(key));
                key = keys.nextElement();
                task.setExpectedAchievement(request.getParameter(key));
            }
            key = keys.nextElement();
            //
            String comment = request.getParameter(key);
            if(comment.length() >= 2000)
                comment =  comment.substring(0,1999);
            task.setComment(comment);
            //
            log.info("task=" + task);
            taskRepository.save(task);
        }
    }

    public void createMonthlyReport(HttpServletRequest request, String username) {
        Enumeration<String> keys = request.getParameterNames();
        LocalDateTime now = LocalDateTime.now();
        String nowDate = getLocalDateFormat(now);

        //Report Save
        Report report = new Report();
        report.setUsername(username);
        report.setReportType("Monthly");
        report.setSimpleDate(nowDate);
        report.setWriteDate(now);
        report.setUpdatedTime(now);
        report.setState("Waiting");
        report.setReportTitle(nowDate + "_Monthly_Report");
        reportRepository.save(report);

        System.out.println(report);
        long r_id = report.getReportId();

        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
            int loc1 = key.indexOf("done");
            int loc2 = key.indexOf("plan");
//            log.info(key + " : " + request.getParameter(key) + " -- loc1 : " + loc1 + ", loc2 : " + loc2);
            Task task = new Task();
            task.setReportId(r_id);
            task.setUsername(username);
            task.setSimpleDate(nowDate);
            task.setReportType("Monthly");
            if(loc1 == 0) {
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
                String comment = request.getParameter(key);
                if(comment.length() >= 2000) comment = comment.substring(0,1999);
                //
                task.setComment(comment);
                key = keys.nextElement();
                task.setQuarter1(request.getParameter(key));
                key = keys.nextElement();
                task.setQuarter2(request.getParameter(key));
                key = keys.nextElement();
                task.setQuarter3(request.getParameter(key));
                key = keys.nextElement();
                task.setQuarter4(request.getParameter(key));
            } else if(loc2 == 0) {
                task.setReportKind("Next_Month_plan");
                task.setProgress(request.getParameter(key));
                key = keys.nextElement();
                task.setExpectedAchievement(request.getParameter(key));
                key = keys.nextElement();
                //
                String comment = request.getParameter(key);
                if(comment.length() >= 2000) comment = comment.substring(0,1999);
                //
                task.setComment(comment);
            }
            log.info("task=" + task);
            taskRepository.save(task);
        }
    }

    public void createYearlyReport(HttpServletRequest request, String username) {
        Enumeration<String> keys = request.getParameterNames();
        LocalDateTime now = LocalDateTime.now();
        String nowDate = getLocalDateFormat(now);

        //Report Save
        Report report = new Report();
        report.setUsername(username);
        report.setReportType("Yearly");
        report.setSimpleDate(nowDate);
        report.setWriteDate(now);
        report.setUpdatedTime(now);
        report.setState("Waiting");
        report.setReportTitle(nowDate + "_Yearly_Report");
        reportRepository.save(report);

        System.out.println(report);
        long r_id = report.getReportId();

        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
//            log.info(key + " : " + request.getParameter(key));
            Task task = new Task();
            task.setReportId(r_id);
            task.setUsername(username);
            task.setSimpleDate(nowDate);
            task.setReportType("Yearly");
            task.setReportKind("project_goal");
            task.setProgress(request.getParameter(key));
            key = keys.nextElement();
            //
            String comment = request.getParameter(key);
            if(comment.length() >= 2000)
                comment =  comment.substring(0,1999);
            task.setComment(comment);
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

            log.info("task=" + task);
            taskRepository.save(task);
        }
    }

    public void createNotice(HttpServletRequest request, String username) {
        Enumeration<String> keys = request.getParameterNames();
        LocalDateTime now = LocalDateTime.now();
        String nowDate = getLocalDateFormat(now);

        //Report Save
        Report report = new Report();
        report.setUsername(username);
        report.setReportType("Notice");
        report.setSimpleDate(nowDate);
        report.setWriteDate(now);
        report.setUpdatedTime(now);
        report.setState("Approved");
        report.setReportTitle(nowDate + "_Notice");
        reportRepository.save(report);

        System.out.println(report);
        long r_id = report.getReportId();

        while(keys.hasMoreElements()) {
            String key = keys.nextElement();
//            log.info(key + " : " + request.getParameter(key));
            Task task = new Task();
            task.setReportId(r_id);
            task.setUsername(username);
            task.setSimpleDate(nowDate);
            task.setReportType("Notice");
            task.setReportKind("notice");
            task.setProgress(request.getParameter(key));
            log.info("task=" + task);
            taskRepository.save(task);
        }
    }

    public void modifyDailyReport(HttpServletRequest request, String username) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();
        long idx = Long.parseLong(request.getParameter(key));
        List<Task> taskList = taskRepository.findByReportId(idx);
        int ix=0;

        while(keys.hasMoreElements()) {
            key = keys.nextElement();
//            log.info(key+"_:_"+request.getParameter(key));
            Task task = new Task();
            if(ix != taskList.size()) {
                task = taskList.get(ix++);
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                String now = LocalDateTime.now().format(dtf);

                task.setReportId(idx);
                task.setUsername(username);
                task.setSimpleDate(now);
                task.setReportType("Daily");
                task.setReportKind("Done");
            }
            task.setDone(request.getParameter(key));
            log.info("task=" + task);
            taskRepository.save(task);
        }
    }

    public void modifyWeeklyReport(HttpServletRequest request, String username) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();
        long idx = Long.parseLong(request.getParameter(key));
        List<Task> taskList = taskRepository.findByReportId(idx);
        int ix=0;

        while(keys.hasMoreElements()) {
            key = keys.nextElement();
            log.info(key + "_:_" + request.getParameter(key));
            int loc1 = key.indexOf("done");
            int loc2 = key.indexOf("plan");
            int loc3 = key.indexOf("another");
            Task task = new Task();
            if(loc1 != -1) { // this week result or done
                if(loc3 == -1) {
                    task = taskList.get(ix++);
                } else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String now = LocalDateTime.now().format(dtf);

                    task.setReportId(idx);
                    task.setUsername(username);
                    task.setSimpleDate(now);
                    task.setReportType("Weekly");
                }
                task.setReportKind("weekly_result");
                task.setProgress(null);
                task.setExpectedAchievement(null);
                task.setDone(request.getParameter(key));
                key = keys.nextElement();
                task.setRealAchievement(request.getParameter(key));
                key = keys.nextElement();
                task.setComment(request.getParameter(key));
            } else if(loc2 != -1) { // next week plan
                if(loc3 == -1) {
                    task = taskList.get(ix++);
                } else {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String now = LocalDateTime.now().format(dtf);

                    task.setReportId(idx);
                    task.setUsername(username);
                    task.setSimpleDate(now);
                    task.setReportType("Weekly");
                }
                task.setReportKind("weekly_plan");
                task.setDone(null);
                task.setRealAchievement(null);
                task.setProgress(request.getParameter(key));
                key = keys.nextElement();
                task.setExpectedAchievement(request.getParameter(key));
                key = keys.nextElement();
                task.setComment(request.getParameter(key));
            }
            log.info("task=" + task);
            taskRepository.save(task);
        }
    }

    public void modifyMonthlyReport(HttpServletRequest request, String username) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();
        long idx = Long.parseLong(request.getParameter(key));
        List<Task> taskList = taskRepository.findByReportId(idx);
        int ix=0;

        while(keys.hasMoreElements()) {
            key = keys.nextElement();
//            log.info(key + "_:_" + request.getParameter(key));
            int loc1 = key.indexOf("done");
            int loc2 = key.indexOf("plan");
            int loc3 = key.indexOf("another");
            Task task = new Task();
            if(loc1 != -1) { // this month result
                if(loc3 == -1) { // 이미 있는 done을 수정
                    task = taskList.get(ix++);
                } else { // 새롭게 done 추가
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String now = LocalDateTime.now().format(dtf);

                    task.setReportId(idx);
                    task.setUsername(username);
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
                String comment = request.getParameter(key);
                if(comment.length() >= 2000) comment = comment.substring(0,1999);
                //
                task.setComment(comment);
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
                    task = taskList.get(ix++);
                } else { // 새롭게 plan 추가
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String now = LocalDateTime.now().format(dtf);

                    task.setReportId(idx);
                    task.setUsername(username);
                    task.setSimpleDate(now);
                    task.setReportType("Monthly");
                }
                task.setDone(null); task.setRealAchievement(null); task.setProjectStartDate(null);
                task.setProjectTargetDate(null); task.setQuarter1(null); task.setQuarter2(null);
                task.setQuarter3(null); task.setQuarter4(null);
                task.setReportKind("Next_Month_plan");
                task.setProgress(request.getParameter(key));
                key = keys.nextElement();
                task.setExpectedAchievement(request.getParameter(key));
                key = keys.nextElement();
                //
                String comment = request.getParameter(key);
                if(comment.length() >= 2000) comment = comment.substring(0,1999);
                //
                task.setComment(comment);
            }
            log.info("task=" + task);
            taskRepository.save(task);
        }
    }

    public void modifyYearlyReport(HttpServletRequest request, String username) {
        Enumeration<String> keys = request.getParameterNames();
        String key = keys.nextElement();
        long idx = Long.parseLong(request.getParameter(key));
        List<Task> taskList = taskRepository.findByReportId(idx);
        int ix=0;

        while(keys.hasMoreElements()) {
            key = keys.nextElement();
//            log.info(key+"_:_"+request.getParameter(key));
            Task task = new Task();
            if(ix != taskList.size()) {
                task = taskList.get(ix++);
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
                String now = LocalDateTime.now().format(dtf);

                task.setReportId(idx);
                task.setUsername(username);
                task.setSimpleDate(now);
                task.setReportType("Yearly");
                task.setReportKind("project_goal");
            }
            task.setProgress(request.getParameter(key));
            key = keys.nextElement();
            //
            String comment = request.getParameter(key);
            if(comment.length() >= 2000) comment = comment.substring(0,1999);
            task.setComment(comment);
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

            log.info("task=" + task);
            taskRepository.save(task);
        }
    }
}
