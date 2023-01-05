package ru.podgoretskaya.loan_conveyor.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.podgoretskaya.loan_conveyor.dto.CreditDTO;
import ru.podgoretskaya.loan_conveyor.dto.LoanApplicationRequestDTO;
import ru.podgoretskaya.loan_conveyor.dto.LoanOfferDTO;
import ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO;
import ru.podgoretskaya.loan_conveyor.service.CalculationService;
import ru.podgoretskaya.loan_conveyor.service.OffersService;

import java.util.List;

@Controller
@Slf4j
@SuppressWarnings("unused")
@Tag(name="Реализация кредитного конвейера", description = "Методы для реализации прескоринга и скоринга")
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
    @Operation(summary = "прескоринг")
    public ResponseEntity<List<LoanOfferDTO>> getOffersPages(@Parameter @RequestBody LoanApplicationRequestDTO model) {
        log.info("вызов /conveyor/offers. Параметры: \"" + model.toString());
        try {
            return new ResponseEntity<>(offersService.loanOptions(model), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.info("ошибка заполнения формы");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/conveyor/calculation")
    @Operation(summary = "скоринг")
    public ResponseEntity<CreditDTO> getOffersPages( @Parameter @RequestBody ScoringDataDTO model) {
        log.info("вызов /conveyor/calculation. Параметры: \"" + model.toString());
        try {
            return new ResponseEntity(calculationService.creditDTO(model), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.info("ошибка заполнения формы");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }
}