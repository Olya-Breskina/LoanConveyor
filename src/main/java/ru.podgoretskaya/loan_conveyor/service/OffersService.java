package ru.podgoretskaya.loan_conveyor.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.podgoretskaya.loan_conveyor.dto.LoanApplicationRequestDTO;
import ru.podgoretskaya.loan_conveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OffersService {
    @Value("${initialRate}")
    private int initialRate;
    @Value("${enabled}")
    private int enabled;
    @Value("${salaryClient}")
    private int salaryClient;
    @Value("${amountMin}")
    private BigDecimal amountMin;

    public boolean FirstLastMiddleNameOffers(LoanApplicationRequestDTO model) {
        boolean firstLastMiddleNameAnswer;
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

    public boolean AmountOffers(LoanApplicationRequestDTO model) {
        boolean amountAnswer;
        int compare = model.getAmount().compareTo(amountMin);
        if (compare >= 0) {
            amountAnswer = true;
        } else throw new IllegalArgumentException("увеличите сумму кредита");
        return amountAnswer;
    }

    public boolean TermOffers(LoanApplicationRequestDTO model) {
        boolean termAnswer;
        if (model.getTerm() >= 6) {
            termAnswer = true;
        } else throw new IllegalArgumentException("увеличите срок кредита");
        return termAnswer;
    }

    public boolean BirthdateOffers(LoanApplicationRequestDTO model) {
        boolean birthdateAnswer;
        LocalDate date = LocalDate.now();
        int age = date.compareTo(model.getBirthdate());
        if (age >= 18) {
            birthdateAnswer = true;
        } else throw new IllegalArgumentException("проверьте дату рождения");
        return birthdateAnswer;
    }

    public boolean PassportOffers(LoanApplicationRequestDTO model) {
        boolean passportAnswer;
        int lengthPassportSeries = model.getPassportSeries().length();
        int lengthPassportNumber = model.getPassportNumber().length();
        if ((lengthPassportSeries == 4) && (lengthPassportNumber == 6)) {
            passportAnswer = true;
        } else throw new IllegalArgumentException("проверьте данные паспорта");
        return passportAnswer;
    }


    public boolean EmailOffers(LoanApplicationRequestDTO model) {
        boolean emailAnswer;
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern patEmail = Pattern.compile(regex);
        Matcher emailOffers = patEmail.matcher(model.getEmail());
        if (emailOffers.matches() == true) {
            emailAnswer = true;
        } else throw new IllegalArgumentException("неверный emal");
        return emailAnswer;
    }

    private LoanOfferDTO possibleTermsOfTheLoan(Boolean isInsuranceEnabled, Boolean isSalaryClient, LoanApplicationRequestDTO model) {
        Long applicationId = Long.valueOf(1);//id в бд
        BigDecimal requestedAmount = model.getAmount();//сумма кредита
        Integer term = model.getTerm();//срок кредита
        BigDecimal rate;
        BigDecimal monthlyPayment;
        BigDecimal totalAmount;
        if (!isInsuranceEnabled && !isSalaryClient) {
            rate = BigDecimal.valueOf(initialRate + enabled + salaryClient);// ставка
        } else if ((!isInsuranceEnabled) && (isSalaryClient)) {
            rate = BigDecimal.valueOf(initialRate + enabled - salaryClient);
        } else if ((isInsuranceEnabled) && (!isSalaryClient)) {
            rate = BigDecimal.valueOf(initialRate - enabled + salaryClient);
        } else {
            rate = BigDecimal.valueOf(initialRate - enabled - salaryClient);
        }
        BigDecimal monthRate = rate.divide(BigDecimal.valueOf(1200), 5, RoundingMode.HALF_UP);// месячный %
        BigDecimal exponentiation = (BigDecimal.valueOf(1).add(monthRate));//(1+monthRate)
        BigDecimal degree = exponentiation.pow(term);
        BigDecimal numerator = requestedAmount.multiply(monthRate.multiply(degree));
        BigDecimal denominator = degree.subtract(BigDecimal.valueOf(1));
        monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP); //ежемесячный платеж
        totalAmount = BigDecimal.valueOf(term).multiply(monthlyPayment);//итоговый платеж

        return new LoanOfferDTO(applicationId, requestedAmount, totalAmount, term, monthlyPayment, rate, isInsuranceEnabled, isSalaryClient);
    }

    public List<LoanOfferDTO> LoanOptions(LoanApplicationRequestDTO model) {
        List<LoanOfferDTO> loanOfferDTO = new ArrayList<>();
        loanOfferDTO.add(possibleTermsOfTheLoan(false, false, model));
        loanOfferDTO.add(possibleTermsOfTheLoan(false, true, model));
        loanOfferDTO.add(possibleTermsOfTheLoan(true, false, model));
        loanOfferDTO.add(possibleTermsOfTheLoan(true, true, model));
        return loanOfferDTO;
    }


}
