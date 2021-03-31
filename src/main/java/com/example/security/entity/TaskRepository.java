package com.example.security.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByReportId(long id);
    Task findByReportIdAndProgress(long id, String done);
    List<Task> findByReportIdAndReportKind(long id, String kind);
}
