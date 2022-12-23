package ru.podgoretskaya.loan_conveyor.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class EmploymentDTO {
    private EmploymentStatus employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Position position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;
    public enum EmploymentStatus{
        UNEMPLOYED, SELF_EMPLOYED, EMPLOYED, BUSINESS_OWNER
    }
    public enum Position{
        WORKER, MID_MANAGER, TOP_MANAGER, OWNER
    }
}