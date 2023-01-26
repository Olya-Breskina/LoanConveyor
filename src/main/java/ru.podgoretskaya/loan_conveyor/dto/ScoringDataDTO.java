package ru.podgoretskaya.loan_conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "данные для скоринга")
public class ScoringDataDTO {
    @Schema(description = "сумма кредита")
    @NonNull
    private BigDecimal amount;
    @Schema(description = "срок кредита")
    @NonNull
    private Integer term;
    @Schema(description = "имя")
    @NotBlank
    private String firstName;
    @Schema(description = "фамилия")
    @NotBlank
    private String lastName;
    @Schema(description = "отчество, если есть")
    private String middleName;
    @Schema(description = "пол")
    @NotBlank
    private Gender gender;//пол
    @Schema(description = "дата рождения")
    @NotBlank
    private LocalDate birthdate;
    @Schema(description = "серия паспорта")
    @NotBlank
    private String passportSeries;
    @Schema(description = "номер паспорта")
    @NotBlank
    private String passportNumber;
    @Schema(description = "дата выдачи")
    @NotBlank
    private LocalDate passportIssueDate;
    @Schema(description = "где выдан")
    @NotBlank
    private String passportIssueBranch;
    @Schema(description = "семейный статус")
    @NotBlank
    private MaritalStatus maritalStatus;
    @Schema(description = "где выдан")
    @NotNull
    private Integer dependentAmount;//зависимая сумма
    @Schema(description = "место работы")
    @NotBlank
    private EmploymentDTO employment;
    @Schema(description = "должность")
    @NotBlank
    private String account;
    @Schema(description = "страхование кредита")
    @NotBlank
    private Boolean isInsuranceEnabled;
    @Schema(description = "зарплатный клиент")
    @NotBlank
    private Boolean isSalaryClient;

    public enum Gender {
        MALE, FEMALE, NOT_BINARY
    }

    public enum MaritalStatus {
        MARRIED, DIVORCED, SINGLE, WIDOW_WIDOWER
    }
}