package ru.podgoretskaya.loan_conveyor.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@Schema(description = "предварительные данные по кредиту")
public class CreditDTO {
    @Schema(description = "сумма кредита")
    private BigDecimal amount;
    @Schema(description = "срок кредита")
    private Integer term;
    @Schema(description = "месячный платеж")
    private BigDecimal monthlyPayment;
    @Schema(description = "ставка")
    private BigDecimal rate;
    @Schema(description = "ПСК")
    private BigDecimal psk;
    @Schema(description = "страхование кредита")
    private Boolean isInsuranceEnabled;
    @Schema(description = "зарплатный клиент")
    private Boolean isSalaryClient;

    private List<PaymentScheduleElement> paymentSchedule;
}