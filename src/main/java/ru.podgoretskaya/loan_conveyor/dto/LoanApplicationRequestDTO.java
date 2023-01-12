package ru.podgoretskaya.loan_conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@Schema(description = "данные для прескоринга")
public class LoanApplicationRequestDTO {
   @Schema(description = "сумма кредита")
   @NonNull
    private BigDecimal amount;
    @Schema(description = "срок кредита")
    @NonNull
    @Size
 @Min(6)
    private Integer term;
    @Schema(description = "имя")
    @NotBlank
    @Size(min = 2, max = 30)
    private String firstName;
    @Schema(description = "фамилия")
    @NotBlank
    @Size(min = 2, max = 30)
    private String lastName;
    @Schema(description = "отчество, если есть")
    private String middleName;
    @Schema(description = "электронный адрес")
    @NotBlank
    private String email;
    @Schema(description = "дата рождения")
    @NotBlank
    private LocalDate birthdate;
    @Schema(description = "серия паспорта")
    @NotBlank
    @Size(min = 4, max = 4)
    private String passportSeries;
    @Schema(description = "номер паспорта")
    @NotBlank
    @Size(min = 6, max = 6)
    private String passportNumber;
}