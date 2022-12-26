package ru.podgoretskaya.loan_conveyor.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor

public class LoanOfferDTO {
    private Long applicationId;//id в бд
    private BigDecimal requestedAmount;//сумма кредита
    private BigDecimal totalAmount;//итоговый платеж
    private Integer term;//срок кредита
    private BigDecimal monthlyPayment;//ежемесячный платеж
    private BigDecimal rate;// ставка
    private Boolean isInsuranceEnabled;// страховка
    private Boolean isSalaryClient;//зп клиент

}