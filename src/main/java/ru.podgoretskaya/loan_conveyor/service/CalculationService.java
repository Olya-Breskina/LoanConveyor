package ru.podgoretskaya.loan_conveyor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.podgoretskaya.loan_conveyor.dto.LoanOfferDTO;
import ru.podgoretskaya.loan_conveyor.dto.PaymentScheduleElement;
import ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.podgoretskaya.loan_conveyor.dto.EmploymentDTO.EmploymentStatus.*;
import static ru.podgoretskaya.loan_conveyor.dto.EmploymentDTO.Position.*;
import static ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO.Gender.*;
import static ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO.MaritalStatus.*;

@Service
public class CalculationService {
    // @Value("${initialRate}")
    int rate = 20;
    @Value("${amountMin}")
    private BigDecimal amountMin;
    @Value("${enabled}")
    private int enabled;
    @Value("${salaryClient}")
    private int salaryClient;
    @Value("${creditInsurance}")
    private BigDecimal creditInsurance;
    boolean firstLastMiddleNameAnswer;
    boolean amountAnswer;
    boolean termAnswer;
    boolean birthdateAnswer;
    boolean passportAnswer;
    int age;
    BigDecimal monthlyPayment;//ежемесячный платеж
    BigDecimal psk;// полной стоимости кредита

    public boolean FirstLastMiddleNameCalculation(ScoringDataDTO model) {
        Pattern patlatletter = Pattern.compile("^[a-zA-Z]+$");
        int lengthFirstName = model.getFirstName().length();
        Matcher firstNameLatLetter = patlatletter.matcher(model.getFirstName());
        int lengthLastName = model.getLastName().length();
        Matcher lastNameLatLetter = patlatletter.matcher(model.getLastName());
        int lengthMiddleName;
        Matcher middleNameLatLetter;
        if (model.getMiddleName() != null) {
            lengthMiddleName = model.getMiddleName().length();
            middleNameLatLetter = patlatletter.matcher(model.getMiddleName());
            if ((lengthFirstName >= 2) && (lengthFirstName <= 30)
                    && (lengthLastName >= 2) && (lengthLastName <= 30)
                    && (lengthMiddleName >= 2) && (lengthMiddleName <= 30)
                    && (firstNameLatLetter.matches())
                    && (lastNameLatLetter.matches())
                    && (middleNameLatLetter.matches())
            ) {
                firstLastMiddleNameAnswer = true;
            } else firstLastMiddleNameAnswer = false;
        } else {

            if ((lengthFirstName >= 2) && (lengthFirstName <= 30)
                    && (lengthLastName >= 2) && (lengthLastName <= 30)
                    && (firstNameLatLetter.matches())
                    && (lastNameLatLetter.matches())
            ) {
                firstLastMiddleNameAnswer = true;
            } else throw new IllegalArgumentException("проверьте ФИО");
        }
        return firstLastMiddleNameAnswer;
    }

    public boolean AmountCalculation(ScoringDataDTO model) {
        int compare = model.getAmount().compareTo(amountMin);
        if (compare >= 0) {
            amountAnswer = true;
        } else throw new IllegalArgumentException("увеличите сумму кредита");
        return amountAnswer;
    }

    public boolean TermCalculation(ScoringDataDTO model) {
        if (model.getTerm() >= 6) {
            termAnswer = true;
        } else throw new IllegalArgumentException("увеличите срок кредита");
        return termAnswer;
    }

    public boolean BirthdateCalculation(ScoringDataDTO model) {
        LocalDate date = LocalDate.now();
        age = date.compareTo(model.getBirthdate());
        if (age >= 18) {
            birthdateAnswer = true;
        } else throw new IllegalArgumentException("проверьте дату рождения");
        return birthdateAnswer;
    }

    public boolean PassportCalculation(ScoringDataDTO model) {
        int lengthPassportSeries = model.getPassportSeries().length();
        int lengthPassportNumber = model.getPassportNumber().length();
        if ((lengthPassportSeries == 4) && (lengthPassportNumber == 6)) {
            passportAnswer = true;
        } else throw new IllegalArgumentException("проверьте данные паспорта");
        return passportAnswer;
    }

    public int Rate(ScoringDataDTO model) {

        //Рабочий статус: Безработный → отказ;
        // Самозанятый → ставка увеличивается на 1;
        //работает =ставка
        // Владелец бизнеса → ставка увеличивается на 3

        switch (model.getEmployment().getEmploymentStatus()){
            case UNEMPLOYED:throw new IllegalArgumentException("отказ");
            case SELF_EMPLOYED: rate = rate + 1;break;
            case EMPLOYED: rate = rate;break;
            case BUSINESS_OWNER:rate = rate + 3;break;
            default:throw new IllegalArgumentException("укажите рабочий статус");
        }
        //Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2;
        // Топ-менеджер → ставка уменьшается на 4
        // владелец ставка-6

        switch (model.getEmployment().getPosition()){
            case WORKER:rate = rate; break;
            case MID_MANAGER:rate = rate-2; break;
            case TOP_MANAGER:rate = rate-4; break;
            case OWNER:rate = rate-6; break;
            default:throw new IllegalArgumentException("укажите должность");
        }
        //Семейное положение: Замужем/женат → ставка уменьшается на 3;
        // Разведен → ставка увеличивается на 1
        // одинок = ставка
        //вдова =ставка
        if (MARRIED.equals(model.getMaritalStatus())) {
            rate = rate - 3;
        } else if (DIVORCED.equals(model.getMaritalStatus())) {
            rate = rate + 1;
        } else if (SINGLE.equals(model.getMaritalStatus())) {
            rate = rate;
        } else if (WIDOW_WIDOWER.equals(model.getMaritalStatus())) {
            rate = rate;
        }
        // Возраст менее 20 или более 60 лет → отказ
        //Пол: Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3; 20-35 ставка +1 
        // Мужчина, возраст от 30 до 55 лет → ставка  уменьшается на 3; 20-30 или 55-60 ставка+1
        //Не бинарный → ставка увеличивается на 3
        if ((age < 20) || (age > 60)) {
            throw new IllegalArgumentException("отказ");
        }
        if ((MALE.equals(model.getGender())) && (age >= 30) && (age <= 55)) {
            rate = rate - 3;
        } else if ((MALE.equals(model.getGender())) && (age >= 20) && (age < 30) || (age > 55) && (age <= 60)) {
            rate = rate + 1;
        } else if ((FEMALE.equals(model.getGender())) && (age >= 35) && (age <= 60)) {
            rate = rate - 3;
        } else if ((FEMALE.equals(model.getGender())) && (age >= 20) && (age < 35)) {
            rate = rate + 1;
        } else if (NOT_BINARY.equals(model.getGender())) {
            rate = rate + 3;
        }
        //зарплатный клиент да-1%, нет+1%
        if (model.getIsSalaryClient()) {
            rate = rate - 1;
        } else {
            rate = rate + 1;
        }
        //страховка  да-3%, нет+3%
        if ((model.getIsInsuranceEnabled())) {
            rate = rate - 3;
        } else {
            rate = rate + 3;
        }
        //Сумма займа больше, чем 20 зарплат → отказ
      /* BigDecimal twentySalaries = model.getEmployment().getSalary().multiply(BigDecimal.valueOf(20));
        if (twentySalaries.compareTo(model.getAmount()) < 0) {
            throw new IllegalArgumentException("отказ");
        }
        //Стаж работы: Общий стаж менее 12 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ
        if ((model.getEmployment().getWorkExperienceCurrent()<3)&&((model.getEmployment().getWorkExperienceTotal()<12))){
            throw new IllegalArgumentException("отказ");
        }*/
        return rate;
    }

    public BigDecimal MonthlyPaymentAmount(ScoringDataDTO model) {
        BigDecimal requestedAmount = model.getAmount();//сумма кредита
        Integer term = model.getTerm();//срок кредита
        BigDecimal monthRate = BigDecimal.valueOf(rate).divide(BigDecimal.valueOf(1200), 5, RoundingMode.HALF_UP);// месячный %
        BigDecimal exponentiation = (BigDecimal.valueOf(1).add(monthRate));//(1+monthRate)
        BigDecimal degree = exponentiation.pow(term);
        BigDecimal numerator = requestedAmount.multiply(monthRate.multiply(degree));
        BigDecimal denominator = degree.subtract(BigDecimal.valueOf(1));
        monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP); //ежемесячный платеж

        return monthlyPayment;
    }

    public BigDecimal Psk(ScoringDataDTO model) {
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
        return psk;
    }
  public List<PaymentScheduleElement> PaymentScheduleElement(ScoringDataDTO model){
        List<PaymentScheduleElement> paymentScheduleElement = new ArrayList<>();
      Integer number=1;
      LocalDate date=LocalDate.now();//дата
      BigDecimal totalPayment=monthlyPayment;//всего к оплате
      BigDecimal interestPayment;//выплата %
      BigDecimal debtPayment;//выплата долга
      BigDecimal remainingDebt=model.getAmount();//остаток
        for ( number=1;  number<=model.getTerm(); number++){
            date=date.plusMonths(1);
            interestPayment=remainingDebt.multiply(BigDecimal.valueOf(rate).divide(BigDecimal.valueOf(1200), 2, RoundingMode.HALF_UP));
            debtPayment=totalPayment.subtract(interestPayment).divide(BigDecimal.valueOf(1), 2, RoundingMode.HALF_UP);
            remainingDebt=remainingDebt.subtract(debtPayment).divide(BigDecimal.valueOf(1), 2, RoundingMode.HALF_UP);
            paymentScheduleElement.add(new PaymentScheduleElement(number, date,totalPayment,interestPayment,debtPayment,remainingDebt));
        }
        return paymentScheduleElement;
    }
}
