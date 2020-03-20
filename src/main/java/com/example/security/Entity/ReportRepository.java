package com.example.security.Entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByReportId(long id);
    List<Report> findByReportTypeAndUsername(String rType, String username);
    List<Report> findByUsernameStartsWith(String username);
    List<Report> findByReportType(String type);
    List<Report> findByState(String cond);
}
