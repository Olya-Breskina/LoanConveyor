package ru.podgoretskaya.loan_conveyor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
                    "firstName=" + model.getFirstName() + ", " +
                            "middleName=" + model.getMiddleName() + ", " +
                            "lastName=" + model.getLastName() + ", " +
                            "birthdate=" + model.getBirthdate() + ", " +
                            "passportSeries=" + model.getPassportSeries() + ", " +
                            "passportNumber=" + model.getPassportNumber() + ", " +
                            "email=" + model.getEmail() + ", " +
                            "amount=" + model.getAmount() + ", " +
                            "term=" + model.getTerm()
                    , HttpStatus.OK);
        }
        catch (IllegalArgumentException e){
            return new ResponseEntity("ошибка заполнения формы", HttpStatus.BAD_GATEWAY);
        }
    }
}