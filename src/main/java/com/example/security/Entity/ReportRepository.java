package com.example.security.Entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    public Report findByReportId(long id);
    public List<Report> findByReportTypeAndUsername(String rType, String username);
}
