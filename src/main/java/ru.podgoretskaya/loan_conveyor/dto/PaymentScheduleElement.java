package ru.podgoretskaya.loan_conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@ToString
@Schema(description = "данные по кредиту")
public class PaymentScheduleElement {
    @Schema(description = "№ платежа")
    private Integer number;
    @Schema(description = "дата платежа")
    private LocalDate date;
    @Schema(description = "итоговый платеж")
    private BigDecimal totalPayment;
    @Schema(description = "оплата %")
    private BigDecimal interestPayment;
    @Schema(description = "выплата основного долга")
    private BigDecimal debtPayment;
    @Schema(description = "остаток задолжности по кредиту")
    private BigDecimal remainingDebt;

}