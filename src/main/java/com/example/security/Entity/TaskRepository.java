package com.example.security.Entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByReportId(long id);
    Task findByReportIdAndProgress(long id, String done);
    Task findByIdx(long id);
}
