package com.example.security.service;

import com.example.security.Entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportService {
    @Autowired
    ReportRepository reportRepository;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

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
}
