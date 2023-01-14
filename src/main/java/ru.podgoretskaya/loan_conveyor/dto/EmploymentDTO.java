package ru.podgoretskaya.loan_conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Schema(description = "данные о работе")
public class EmploymentDTO {
    @Schema(description = "статус занятости")
    private EmploymentStatus employmentStatus;
    @Schema(description = "ИНН")
    private String employerINN;
    @Schema(description = "зарплата")
    private BigDecimal salary;
    @Schema(description = "должность")
    private Position position;
    @Schema(description = "общий стаж работы")
    private Integer workExperienceTotal;
    @Schema(description = "стаж на этом месте работы")
    private Integer workExperienceCurrent;
    public enum EmploymentStatus{
        UNEMPLOYED, SELF_EMPLOYED, EMPLOYED, BUSINESS_OWNER
    }
    public enum Position{
        WORKER, MID_MANAGER, TOP_MANAGER, OWNER
    }
}