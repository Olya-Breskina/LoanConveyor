package ru.podgoretskaya.loan_conveyor.controller;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.podgoretskaya.loan_conveyor.dto.EmploymentDTO;
import ru.podgoretskaya.loan_conveyor.dto.LoanApplicationRequestDTO;
import ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO;
import ru.podgoretskaya.loan_conveyor.service.CalculationService;
import ru.podgoretskaya.loan_conveyor.service.OffersService;

@Controller

@SuppressWarnings("unused")
public class APIController {
    LoanApplicationRequestDTO operationOffersModel = new LoanApplicationRequestDTO();
    ScoringDataDTO operationCalculationModel = new ScoringDataDTO();
    private OffersService offersService;
    private CalculationService calculationService;

    @Autowired
    public APIController(OffersService offersService, CalculationService calculationService) {
        this.offersService = offersService;
        this.calculationService = calculationService;
    }

    @PostMapping(value = "/conveyor/offers")
    public ResponseEntity<String> getOffersPages(@RequestBody LoanApplicationRequestDTO model) {
        try {
            return new ResponseEntity(
                    offersService.FirstLastMiddleNameOffers(model) + ", " +
                            offersService.BirthdateOffers(model) + ", " +
                            offersService.PassportOffers(model) + ", " +
                            offersService.EmailOffers(model) + ", " +
                            offersService.EmailOffers(model) + ", " +
                            offersService.AmountOffers(model) + ", " +
                            offersService.TermOffers(model) + ", \n" +
                            offersService.LoanOptions(model), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity("ошибка заполнения формы", HttpStatus.BAD_GATEWAY);
        }
    }

    @PostMapping(value = "/conveyor/calculation")
    public ResponseEntity<String> getOffersPages(@RequestBody ScoringDataDTO model) {
        try {
            return new ResponseEntity(
                    "firstName=" + calculationService.FirstLastMiddleNameCalculation(model)
                            + ", " + "birthdate=" + calculationService.BirthdateCalculation(model)
                            + ", " + "passportNumber=" + calculationService.PassportCalculation(model)
                            + ", " + "amount=" + calculationService.AmountCalculation(model)
                            + ", " + "term=" + calculationService.TermCalculation(model)
                            + ", " + "rate=" + calculationService.Rate(model)
                            + ", " + "MonthlyPaymentAmount= " + calculationService.MonthlyPaymentAmount(model)
                            + ", " + "PSK= " + calculationService.Psk(model)
                            + ", \n" + calculationService.PaymentScheduleElement(model)
                    , HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity("ошибка заполнения формы", HttpStatus.BAD_GATEWAY);
        }
    }
}