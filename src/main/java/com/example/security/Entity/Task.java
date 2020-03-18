package com.example.security.Entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@Entity
@Table(name="taskRtable")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idx;

    private Long reportId;
    private String username;
    private String simpleDate;
    private String reportType;
    private String reportKind;
    private String progress;
    private String done;
    private String achievement;
}
