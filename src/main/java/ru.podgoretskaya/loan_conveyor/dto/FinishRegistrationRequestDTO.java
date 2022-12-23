package ru.podgoretskaya.loan_conveyor.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FinishRegistrationRequestDTO {
    private Gender gender;
    private MaritalStatus maritalStatus;
    private Integer dependentAmount;
    private LocalDate passportIssueDate;
    private String passportIssueBranch;
    private EmploymentDTO employment;
    private String account;

    public enum Gender {
        MALE, FEMALE, NOT_BINARY
    }

    public enum MaritalStatus {
        MARRIED, DIVORCED, SINGLE, WIDOW_WIDOWER
    }
}