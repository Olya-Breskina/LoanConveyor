package ru.podgoretskaya.loan_conveyor.service;

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
public class OffersService {
    @Value("${initialRate}")
    private int initialRate;
    @Value("${enabled}")
    private int enabled;
    @Value("${salaryClient}")
    private int salaryClient;
    @Value("${amountMin}")
    private BigDecimal amountMin;

    public void firstLastMiddleNameOffers(LoanApplicationRequestDTO model) {
        log.info("проверка ФИО");
        Pattern patlatletter = Pattern.compile("^[a-zA-Z]+$");
        log.debug("имя " + model.getFirstName());
        int lengthFirstName = model.getFirstName().length();
        Matcher firstNameLatLetter = patlatletter.matcher(model.getFirstName());
        log.debug("фамилия " + model.getLastName());
        int lengthLastName = model.getLastName().length();
        Matcher lastNameLatLetter = patlatletter.matcher(model.getLastName());
        int lengthMiddleName;
        Matcher middleNameLatLetter;
        log.debug("отчество " + model.getMiddleName());
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
            }
        } else {
            if ((lengthFirstName >= 2) && (lengthFirstName <= 30)
                    && (lengthLastName >= 2) && (lengthLastName <= 30)
                    && (firstNameLatLetter.matches())
                    && (lastNameLatLetter.matches())
            ) {
            } else {
                log.info("проверьте ФИО");
                throw new IllegalArgumentException("проверьте ФИО");
            }
        }
    }

    public void amountOffers(LoanApplicationRequestDTO model) {
        log.info("вход в метод amountOffers");
        log.debug("запрошенная сумма " + model.getAmount());
        int compare = model.getAmount().compareTo(amountMin);
        if (compare >= 0) {
        } else {
            log.info("увеличите сумму кредита");
            throw new IllegalArgumentException("увеличите сумму кредита");
        }
    }

    public void termOffers(LoanApplicationRequestDTO model) {
        log.info("вход в метод termOffers");
        log.debug("срок кредита " + model.getTerm());
        if (model.getTerm() >= 6) {
        } else {
            log.info("увеличите срок кредита");
            throw new IllegalArgumentException("увеличите срок кредита");
        }
    }

    public void birthdateOffers(LoanApplicationRequestDTO model) {
        log.info("вход в метод birthdateOffers");
        LocalDate date = LocalDate.now();
        log.debug("дата рождения " + model.getBirthdate());
        int age = date.compareTo(model.getBirthdate());
        if (age >= 18) {
        } else {
            log.info("проверьте дату рождения");
            throw new IllegalArgumentException("проверьте дату рождения");
        }
    }

    public void passportOffers(LoanApplicationRequestDTO model) {
        log.info("вход в метод passportOffers");
        log.debug("серия номер " + model.getPassportSeries() + ", " + model.getPassportNumber());
        int lengthPassportSeries = model.getPassportSeries().length();
        int lengthPassportNumber = model.getPassportNumber().length();
        if ((lengthPassportSeries == 4) && (lengthPassportNumber == 6)) {
        } else {
            log.info("проверьте данные паспорта");
            throw new IllegalArgumentException("проверьте данные паспорта");
        }
    }

    public void emailOffers(LoanApplicationRequestDTO model) {
        log.info("вход в метод emailOffers");
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern patEmail = Pattern.compile(regex);
        log.debug("email " + model.getEmail());
        Matcher emailOffers = patEmail.matcher(model.getEmail());
        if (emailOffers.matches() == true) {
        } else {
            log.info("неверный email");
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
        log.info("вход в метод loanOptions");
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