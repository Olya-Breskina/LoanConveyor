package ru.podgoretskaya.loan_conveyor.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@AllArgsConstructor
@NoArgsConstructor
public class OffersService {
    @Value("${initialRate}")
    private int initialRate;
    @Value("${enabled}")
    private int enabled;
    @Value("${salaryClient}")
    private int salaryClient;
    @Value("${amountMin}")
    private BigDecimal amountMin;
    @Value("${termMin}")
    private int termMin;
    @Value("${ageMin}")
    private int ageMin;


   private void firstLastMiddleNameOffers(LoanApplicationRequestDTO model) {
       Pattern patlatletter = Pattern.compile("^[a-zA-Z]{2,30}$");
        log.debug("имя " + model.getFirstName());
        Matcher firstNameLatLetter = patlatletter.matcher(model.getFirstName());
        if (!firstNameLatLetter.matches()) {
            log.info("проверьте имя" + model.getFirstName());
            throw new IllegalArgumentException("проверьте ФИО");
        }
        log.debug("фамилия " + model.getLastName());
        Matcher lastNameLatLetter = patlatletter.matcher(model.getLastName());
        if (!lastNameLatLetter.matches()) {
            log.info("проверьте фамилия" + model.getFirstName());
            throw new IllegalArgumentException("проверьте ФИО");
        }
        if (model.getMiddleName() != null) {
            log.debug("отчество " + model.getMiddleName());
            Matcher middleNameLatLetter = patlatletter.matcher(model.getMiddleName());
            if (!middleNameLatLetter.matches()) {
                log.info("проверьте отчество" + model.getMiddleName());
                throw new IllegalArgumentException("проверьте ФИО");
            }
        }
    }

    public void amountOffers(LoanApplicationRequestDTO model) {
        log.debug("запрошенная сумма " + model.getAmount());
        int compare = model.getAmount().compareTo(amountMin);
        if (compare < 0) {
            log.info("увеличите сумму кредита" + model.getAmount());
            throw new IllegalArgumentException("увеличите сумму кредита");
        }
    }

    private void termOffers(LoanApplicationRequestDTO model) {
        log.debug("срок кредита " + model.getTerm());
        int compare = model.getTerm().compareTo(termMin);
        if ((compare < 0)) {
            log.info("увеличите срок кредита" + model.getTerm());
            throw new IllegalArgumentException("увеличите срок кредита");
        }
    }

    private void birthdateOffers(LoanApplicationRequestDTO model) {
        LocalDate date = LocalDate.now();
        log.debug("дата рождения " + model.getBirthdate());
        int age = date.compareTo(model.getBirthdate());
        if (age >= ageMin) {
        } else {
            log.info("проверьте дату рождения" + model.getBirthdate());
            throw new IllegalArgumentException("проверьте дату рождения");
        }
    }

    private void passportOffers(LoanApplicationRequestDTO model) {
        log.debug("серия номер " + model.getPassportSeries() + ", " + model.getPassportNumber());
        int lengthPassportSeries = model.getPassportSeries().length();
        int lengthPassportNumber = model.getPassportNumber().length();
        if ((lengthPassportSeries == 4) && (lengthPassportNumber == 6)) {
        } else {
            log.info("проверьте данные паспорта" + model.getPassportSeries() + ", " + model.getPassportNumber());
            throw new IllegalArgumentException("проверьте данные паспорта");
        }
    }

    private void emailOffers(LoanApplicationRequestDTO model) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern patEmail = Pattern.compile(regex);
        log.debug("email " + model.getEmail());
        Matcher emailOffers = patEmail.matcher(model.getEmail());
        if (emailOffers.matches()) {
        } else {
            log.info("неверный email" + model.getEmail());
            throw new IllegalArgumentException("неверный email");
        }

    }

    private LoanOfferDTO possibleTermsOfTheLoan(Boolean isInsuranceEnabled, Boolean isSalaryClient, LoanApplicationRequestDTO model) {
        Long applicationId = Long.valueOf(1);//id в бд
        BigDecimal requestedAmount = model.getAmount();//сумма кредита
        Integer term = model.getTerm();//срок кредита
        BigDecimal rate;
        BigDecimal monthlyPayment;
        BigDecimal totalAmount;
        BigDecimal monthsOfTheYear = BigDecimal.valueOf(12);
        BigDecimal percent = BigDecimal.valueOf(100);
        if (!isInsuranceEnabled && !isSalaryClient) {
            rate = BigDecimal.valueOf(initialRate + enabled + salaryClient);// ставка
        } else if ((!isInsuranceEnabled) && (isSalaryClient)) {
            rate = BigDecimal.valueOf(initialRate + enabled - salaryClient);
        } else if ((isInsuranceEnabled) && (!isSalaryClient)) {
            rate = BigDecimal.valueOf(initialRate - enabled + salaryClient);
        } else {
            rate = BigDecimal.valueOf(initialRate - enabled - salaryClient);
        }
        BigDecimal monthRate = rate.divide(monthsOfTheYear.multiply(percent), 5, RoundingMode.HALF_UP);// месячный %
        BigDecimal exponentiation = (BigDecimal.ONE.add(monthRate));//(1+monthRate)
        BigDecimal degree = exponentiation.pow(term);
        BigDecimal numerator = requestedAmount.multiply(monthRate.multiply(degree));
        BigDecimal denominator = degree.subtract(BigDecimal.ONE);
        monthlyPayment = numerator.divide(denominator, 2, RoundingMode.HALF_UP); //ежемесячный платеж
        totalAmount = BigDecimal.valueOf(term).multiply(monthlyPayment);//итоговый платеж
        return new LoanOfferDTO(applicationId, requestedAmount, totalAmount, term, monthlyPayment, rate, isInsuranceEnabled, isSalaryClient);
    }

    public List<LoanOfferDTO> loanOptions(LoanApplicationRequestDTO model) {
        List<LoanOfferDTO> loanOfferDTO;
        firstLastMiddleNameOffers(model);
        amountOffers(model);
        termOffers(model);
        birthdateOffers(model);
        passportOffers(model);
        emailOffers(model);
        loanOfferDTO = new ArrayList<>();
        loanOfferDTO.add(possibleTermsOfTheLoan(false, false, model));
        loanOfferDTO.add(possibleTermsOfTheLoan(false, true, model));
        loanOfferDTO.add(possibleTermsOfTheLoan(true, false, model));
        loanOfferDTO.add(possibleTermsOfTheLoan(true, true, model));
        return loanOfferDTO;
    }

}