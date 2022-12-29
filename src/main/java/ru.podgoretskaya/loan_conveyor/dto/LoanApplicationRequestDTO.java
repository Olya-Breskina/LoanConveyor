package ru.podgoretskaya.loan_conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Данные для прескоринга")
public class LoanApplicationRequestDTO {
    @Schema(description = "Сумма кредита")
    @NonNull
    private BigDecimal amount;

    @Schema(description = "Срок кредита")
    @NonNull
    private Integer term;

    @Schema(description = "Имя")
    @NotBlank
    @Size(min=2, max = 30)
    private String firstName;

    @Schema(description = "Фамилия")
    @NonNull
    @Size(min=2, max = 30)
    private String lastName;

    @Schema(description = "Отчество")
    private String middleName;

    @Schema(description = "Сумма кредита")
    @NotBlank
    private String email;

    @Schema(description = "Email адрес")
    @NonNull
    private LocalDate birthdate;

    @Schema(description = "Серия паспорта")
    @NotBlank
    private String passportSeries;

    @Schema(description = "Номер паспорта")
    @NotBlank
    private String passportNumber;
}