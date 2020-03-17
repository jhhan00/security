package com.example.security.Entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    public Report findByReportId(long id);
}
