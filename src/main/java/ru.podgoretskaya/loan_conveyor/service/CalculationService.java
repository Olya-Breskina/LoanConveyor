package ru.podgoretskaya.loan_conveyor.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.podgoretskaya.loan_conveyor.dto.PaymentScheduleElement;
import ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO.Gender.*;

@Slf4j
@Service
public class CalculationService {
    @Value("${initialRate}")
    private BigDecimal initiRate;
    @Value("${amountMin}")
    private BigDecimal amountMin;
    @Value("${creditInsurance}")
    private BigDecimal creditInsurance;
    BigDecimal monthlyPayment;//ежемесячный платеж
    BigDecimal psk;// полной стоимости кредита
    BigDecimal monthsOfTheYear = BigDecimal.valueOf(12);
    BigDecimal percent = BigDecimal.valueOf(100);
    public BigDecimal rate(ScoringDataDTO model) {
        BigDecimal rate=initiRate;
        /*Рабочий статус: Безработный → отказ;
        Самозанятый → ставка увеличивается на 1;
        работает =ставка
         Владелец бизнеса → ставка увеличивается на 3*/
        log.debug("Рабочий статус "+model.getEmployment().getEmploymentStatus());
        switch (model.getEmployment().getEmploymentStatus()){
            case UNEMPLOYED:throw new IllegalArgumentException("отказ");
            case SELF_EMPLOYED: rate = rate.add(BigDecimal.valueOf(1));break;
            case EMPLOYED:  break;
            case BUSINESS_OWNER:rate = rate.add(BigDecimal.valueOf(3));break;
            default:throw new IllegalArgumentException("укажите рабочий статус");
        }
        /*Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2;
        Топ-менеджер → ставка уменьшается на 4
        владелец ставка-6*/
        log.debug("Позиция на работе "+model.getEmployment().getPosition());
       switch (model.getEmployment().getPosition()){
            case WORKER: break;
            case MID_MANAGER:rate = rate.subtract(BigDecimal.valueOf(2)); break;
            case TOP_MANAGER:rate = rate.subtract(BigDecimal.valueOf(4)); break;
            case OWNER:rate = rate.subtract(BigDecimal.valueOf(6)); break;
            default:throw new IllegalArgumentException("укажите должность");
        }
        /*Семейное положение: Замужем/женат → ставка уменьшается на 3;
        Разведен → ставка увеличивается на 1
        одинок = ставка
        вдова =ставка*/
        log.debug("Семейное положение "+model.getMaritalStatus());
        switch (model.getMaritalStatus()){
            case MARRIED:rate = rate.subtract(BigDecimal.valueOf(3));break;
            case DIVORCED: rate = rate.add(BigDecimal.valueOf(1));break;
            case SINGLE:break;
            case WIDOW_WIDOWER:break;
            default:throw new IllegalArgumentException("укажите семейный статус");
        }

        /*Возраст менее 20 или более 60 лет → отказ
        Пол: Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3; 20-35 ставка +1
        Мужчина, возраст от 30 до 55 лет → ставка  уменьшается на 3; 20-30 или 55-60 ставка+1
        Не бинарный → ставка увеличивается на 3*/
        LocalDate date = LocalDate.now();
        int age = date.compareTo(model.getBirthdate());
        log.debug ("возрат "+age);
        if ((age < 20) || (age > 60)) {
            throw new IllegalArgumentException("отказ");
        }
        if ((MALE.equals(model.getGender())) && (age >= 30) && (age <= 55)) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        } else if ((MALE.equals(model.getGender())) && (age >= 20) && (age < 30) || (age > 55) && (age <= 60)) {
            rate = rate.add(BigDecimal.valueOf(1));
        } else if ((FEMALE.equals(model.getGender())) && (age >= 35) && (age <= 60)) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        } else if ((FEMALE.equals(model.getGender())) && (age >= 20) && (age < 35)) {
            rate = rate.add(BigDecimal.valueOf(1));
        } else if (NOT_BINARY.equals(model.getGender())) {
            rate = rate.add(BigDecimal.valueOf(3));
        }
        //зарплатный клиент да-1%, нет+1%
        if (model.getIsSalaryClient()) {
            rate = rate.subtract(BigDecimal.valueOf(1));
        } else {
            rate = rate.add(BigDecimal.valueOf(1));
        }
        //страховка  да-3%, нет+3%
        if ((model.getIsInsuranceEnabled())) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        } else {
            rate = rate.add(BigDecimal.valueOf(3));
        }
        //Сумма займа больше, чем 20 зарплат → отказ
        log.debug("Сумма займа "+model.getAmount());
       BigDecimal twentySalaries = model.getEmployment().getSalary().multiply(BigDecimal.valueOf(20));
        if (twentySalaries.compareTo(model.getAmount()) < 0) {
            throw new IllegalArgumentException("отказ");
        }
        //Стаж работы: Общий стаж менее 12 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ
        log.debug("Стаж работы " +model.getEmployment().getWorkExperienceCurrent()+", "+ model.getEmployment().getWorkExperienceTotal());
        if ((model.getEmployment().getWorkExperienceCurrent()<3)&&((model.getEmployment().getWorkExperienceTotal()<12))){
            throw new IllegalArgumentException("отказ");
        }
        log.info("ставка "+rate);
        return rate;
    }

    public BigDecimal monthlyPaymentAmount(ScoringDataDTO model, BigDecimal rate ) {
        BigDecimal requestedAmount = model.getAmount();//сумма кредита
        Integer term = model.getTerm();//срок кредита
        BigDecimal monthRate = rate.divide(monthsOfTheYear.multiply(percent), 2, RoundingMode.HALF_UP);// месячный %
        BigDecimal exponentiation = (BigDecimal.ONE.add(monthRate));//(1+monthRate)
        BigDecimal degree = exponentiation.pow(term);
        BigDecimal numerator = requestedAmount.multiply(monthRate.multiply(degree));
        BigDecimal denominator = degree.subtract(BigDecimal.ONE);
        monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP); //ежемесячный платеж
        log.info("размер ежемесячного платежа "+monthlyPayment);
        return monthlyPayment;
    }

    public BigDecimal psk(ScoringDataDTO model) {
        BigDecimal requestedAmount = model.getAmount();
        Integer term = model.getTerm();
        boolean isInsuranceEnabled = model.getIsInsuranceEnabled();
        BigDecimal totalAmountOfAllPayments;
        if (isInsuranceEnabled) {
            totalAmountOfAllPayments = monthlyPayment.multiply(BigDecimal.valueOf(term)).add(creditInsurance);
        } else {
            totalAmountOfAllPayments = monthlyPayment.multiply(BigDecimal.valueOf(term));
        }

        BigDecimal numerator = totalAmountOfAllPayments.divide(requestedAmount, 2, RoundingMode.HALF_UP)
                .subtract(BigDecimal.valueOf(1));
        BigDecimal termInYears = BigDecimal.valueOf(term).divide(BigDecimal.valueOf(12));//срок кредита в годах
        psk = numerator.divide(termInYears).multiply(BigDecimal.valueOf(100));
        log.info("пск "+psk);
        return psk;
    }

    public List<PaymentScheduleElement> paymentScheduleElement(ScoringDataDTO model, BigDecimal rate) {
        List<PaymentScheduleElement> paymentScheduleElement = new ArrayList<>();
        Integer number = 1;
        LocalDate date = LocalDate.now();//дата
        BigDecimal totalPayment = monthlyPayment;//всего к оплате
        BigDecimal interestPayment;//выплата %
        BigDecimal debtPayment;//выплата долга
        BigDecimal remainingDebt = model.getAmount();//остаток
        for (number = 1; number <= model.getTerm(); number++) {
            date = date.plusMonths(1);
            interestPayment = remainingDebt.multiply(rate.divide(monthsOfTheYear.multiply(percent), 2, RoundingMode.HALF_UP));
            debtPayment = totalPayment.subtract(interestPayment).divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);
            remainingDebt = remainingDebt.subtract(debtPayment).divide(BigDecimal.ONE, 2, RoundingMode.HALF_UP);
            paymentScheduleElement.add(new PaymentScheduleElement(number, date, totalPayment, interestPayment, debtPayment, remainingDebt));
        }
        return paymentScheduleElement;
    }
}
