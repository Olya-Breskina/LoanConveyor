package ru.podgoretskaya.loan_conveyor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.podgoretskaya.loan_conveyor.dto.LoanApplicationRequestDTO;
import ru.podgoretskaya.loan_conveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
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
    boolean offersAnswer;
    boolean firstLastMiddleNameAnswer;
    boolean amountAnswer;
    boolean termAnswer;
    boolean birthdateAnswer;
    boolean passportAnswer;
    boolean emailAnswer;

    public boolean FirstLastMiddleNameOffers(LoanApplicationRequestDTO model) {
        Pattern patlatletter = Pattern.compile("^[a-zA-Z]+$");
        int lengthFirstName = model.getFirstName().length();
        Matcher firstNameLatLetter = patlatletter.matcher(model.getFirstName());
        int lengthLastName = model.getLastName().length();
        Matcher lastNameLatLetter = patlatletter.matcher(model.getLastName());
        int lengthMiddleName;
        Matcher middleNameLatLetter;
        //boolean firstLastMiddleNameAnswer;
        if (model.getMiddleName() != null) {
            lengthMiddleName = model.getMiddleName().length();
            middleNameLatLetter = patlatletter.matcher(model.getMiddleName());
            if ((lengthFirstName >= 2) && (lengthFirstName <= 30)
                    && (lengthLastName >= 2) && (lengthLastName <= 30)
                    && (lengthMiddleName >= 2) && (lengthMiddleName <= 30)
                    && (firstNameLatLetter.matches() == true)
                    && (lastNameLatLetter.matches() == true)
                    && (middleNameLatLetter.matches())
            ) {
                firstLastMiddleNameAnswer = true;
            } else firstLastMiddleNameAnswer = false;
        } else {

            if ((lengthFirstName >= 2) && (lengthFirstName <= 30)
                    && (lengthLastName >= 2) && (lengthLastName <= 30)
                    && (firstNameLatLetter.matches() == true)
                    && (lastNameLatLetter.matches() == true)
            ) {
                firstLastMiddleNameAnswer = true;
            } else firstLastMiddleNameAnswer = false;
        }
        return firstLastMiddleNameAnswer;
    }

    public boolean AmountOffers(LoanApplicationRequestDTO model) {
        //boolean amountAnswer;
        int compare = model.getAmount().compareTo(amountMin);
        if (compare >= 0) {
            amountAnswer = true;
        } else amountAnswer = false;
        return amountAnswer;
    }

    public boolean TermOffers(LoanApplicationRequestDTO model) {
        //boolean termAnswer;
        if (model.getTerm() >= 6) {
            termAnswer = true;
        } else termAnswer = false;
        return termAnswer;
    }

    public boolean BirthdateOffers(LoanApplicationRequestDTO model) {
        // boolean birthdateAnswer;
        LocalDate date = LocalDate.now();
        int age = date.compareTo(model.getBirthdate());
        if (age >= 18) {
            birthdateAnswer = true;
        } else birthdateAnswer = false;
        return birthdateAnswer;
    }

    public boolean PassportOffers(LoanApplicationRequestDTO model) {
        // boolean passportAnswer;
        int lengthPassportSeries = model.getPassportSeries().length();
        int lengthPassportNumber = model.getPassportNumber().length();
        if ((lengthPassportSeries == 4) && (lengthPassportNumber == 6)) {
            passportAnswer = true;
        } else passportAnswer = false;
        return passportAnswer;
    }

    public boolean EmailOffers(LoanApplicationRequestDTO model) {
        // boolean emailAnswer;
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern patEmail = Pattern.compile(regex);
        Matcher emailOffers = patEmail.matcher(model.getEmail());
        if (emailOffers.matches() == true) {
            emailAnswer = true;
        } else throw new IllegalArgumentException("неверный emal");
        return emailAnswer;
    }


    public List<LoanOfferDTO> LoanOptions() {
        int percent = initialRate;// начальный % по кредиту
            List<LoanOfferDTO> LoanOfferDTO = new ArrayList<>();
            LoanOfferDTO.add(new LoanOfferDTO());
        return LoanOfferDTO;
    }

}