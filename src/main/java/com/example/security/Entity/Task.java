package com.example.security.Entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="taskRtable")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    private Long reportId;
    private String username;
    private String simpleDate;
    private String reportType;
    private String reportKind;
    private String progress;
    private String done;
    private String expectedAchievement;
    private String realAchievement;
    private String comment;
}
