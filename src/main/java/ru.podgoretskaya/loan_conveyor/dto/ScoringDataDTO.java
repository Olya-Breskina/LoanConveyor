package ru.podgoretskaya.loan_conveyor.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
public class ScoringDataDTO {
    private BigDecimal amount;//количество
    private Integer term;//срок кредита
    private String firstName;
    private String lastName;
    private String middleName;
    private Gender gender;//пол
    private LocalDate birthdate;
    private String passportSeries;
    private String passportNumber;
    private LocalDate passportIssueDate;// дата выдачи
    private String passportIssueBranch;// где выдан
    private MaritalStatus maritalStatus;//семейный статус
    private Integer dependentAmount;//зависимая сумма
    private EmploymentDTO employment;//работа
    private String account;// должность
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;

    public enum Gender {
        MALE, FEMALE, NOT_BINARY
    }

    public enum MaritalStatus {
        MARRIED, DIVORCED, SINGLE, WIDOW_WIDOWER
    }
}