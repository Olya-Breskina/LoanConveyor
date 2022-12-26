package ru.podgoretskaya.loan_conveyor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.podgoretskaya.loan_conveyor.dto.CreditDTO;
import ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.podgoretskaya.loan_conveyor.dto.EmploymentDTO.EmploymentStatus.*;
import static ru.podgoretskaya.loan_conveyor.dto.EmploymentDTO.Position.*;
import static ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO.Gender.*;
import static ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO.MaritalStatus.*;

@Service
public class CalculationService {
    @Value("${initialRate}")
    int rape;
    @Value("${amountMin}")
    private BigDecimal amountMin;
    boolean firstLastMiddleNameAnswer;
    boolean amountAnswer;
    boolean termAnswer;
    boolean birthdateAnswer;
    boolean passportAnswer;
    int age;

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

    public int Rape(ScoringDataDTO model) {

        //Рабочий статус: Безработный → отказ;
        // Самозанятый → ставка увеличивается на 1;
        //работает =ставка
        // Владелец бизнеса → ставка увеличивается на 3
        if (UNEMPLOYED.equals(model.getEmployment())) {
            throw new IllegalArgumentException("отказ");
        } else if (SELF_EMPLOYED.equals(model.getEmployment())) {
            rape = rape + 1;
        } else if (EMPLOYED.equals(model.getEmployment())) {
            rape = rape;
        } else if (BUSINESS_OWNER.equals(model.getEmployment())) {
            rape = rape + 3;
        }
        //Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2;
        // Топ-менеджер → ставка уменьшается на 4
        // владелец ставка-6

        if (WORKER.equals(model.getEmployment())) {
            rape = rape;
        } else if (MID_MANAGER.equals(model.getEmployment())) {
            rape = rape - 2;
        } else if (TOP_MANAGER.equals(model.getEmployment())) {
            rape = rape - 4;
        } else if (OWNER.equals(model.getEmployment())) {
            rape = rape - 6;
        }
        //Семейное положение: Замужем/женат → ставка уменьшается на 3;
        // Разведен → ставка увеличивается на 1
        // одинок = ставка
        //вдова =ставка
        if (MARRIED.equals(model.getMaritalStatus())) {
            rape = rape - 3;
        } else if (DIVORCED.equals(model.getMaritalStatus())) {
            rape = rape + 1;
        } else if (SINGLE.equals(model.getMaritalStatus())) {
            rape = rape;
        } else if (WIDOW_WIDOWER.equals(model.getMaritalStatus())) {
            rape = rape;
        }
        // Возраст менее 20 или более 60 лет → отказ
        //Пол: Женщина, возраст от 35 до 60 лет → ставка уменьшается на 3; 20-35 ставка +1 
        // Мужчина, возраст от 30 до 55 лет → ставка  уменьшается на 3; 20-30 или 55-60 ставка+1
        //Не бинарный → ставка увеличивается на 3
        if ((age < 20) && (age > 60)) {
            throw new IllegalArgumentException("отказ");
        }
        if ((MALE.equals(model.getGender())) && (age >= 30) && (age <= 55)) {
            rape = rape;
        } else if ((MALE.equals(model.getGender())) && (age >= 20) && (age < 30) && (age > 55) && (age <= 60)) {
            rape = rape + 1;
        } else if ((FEMALE.equals(model.getGender())) && (age >= 35) && (age <= 60)) {
            rape = rape - 3;
        } else if ((FEMALE.equals(model.getGender())) && (age >= 20) && (age < 35)) {
            rape = rape + 1;
        } else if (NOT_BINARY.equals(model.getGender())) {
            rape = rape + 3;
        }
        //Сумма займа больше, чем 20 зарплат → отказ
        BigDecimal twentySalaries = model.getEmployment().getSalary().multiply(BigDecimal.valueOf(20));
        if (twentySalaries.compareTo(model.getAmount()) < 0) {
            throw new IllegalArgumentException("отказ");
        }
        //Стаж работы: Общий стаж менее 12 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ
        if ((model.getEmployment().getWorkExperienceCurrent()<3)&&((model.getEmployment().getWorkExperienceTotal()<12))){
            throw new IllegalArgumentException("отказ");
        }
        return rape;
    }
}
