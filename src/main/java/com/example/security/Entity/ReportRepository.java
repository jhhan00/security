package com.example.security.Entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByReportId(long id);
    List<Report> findByReportTypeAndUsername(String rType, String username);
    List<Report> findByUsernameStartsWithOrderByWriteDateDesc(String username);
    List<Report> findByReportTitleContainingOrderByWriteDateDesc(String title);
    List<Report> findAllByOrderByUpdatedTimeDesc();
    List<Report> findByReportTypeOrderByWriteDateDesc(String type);
    List<Report> findByStateOrderByWriteDateDesc(String state);
}
