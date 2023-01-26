package ru.podgoretskaya.loan_conveyor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import ru.podgoretskaya.loan_conveyor.dto.PaymentScheduleElement;
import ru.podgoretskaya.loan_conveyor.dto.ScoringDataDTO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class CalculationServiceTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Spy
    CalculationService calculationService = new CalculationService(BigDecimal.valueOf(20), BigDecimal.valueOf(100000), BigDecimal.valueOf(1000));

    @BeforeEach
    void beforeAll() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void rate() throws IOException {
        ScoringDataDTO scoringDataDTO = objectMapper.readValue(new File("src/test/resources/calculationService/test.json"), ScoringDataDTO.class);
        BigDecimal rate = calculationService.rate(scoringDataDTO);
        assertNotNull(rate);
        assertEquals(BigDecimal.valueOf(17), rate);
    }

    @Test
    void rateNew() throws IOException {
        ScoringDataDTO scoringDataDTO = objectMapper.readValue(new File("src/test/resources/calculationService/testNew.json"), ScoringDataDTO.class);
        BigDecimal rate = calculationService.rate(scoringDataDTO);
        assertNotNull(rate);
        assertEquals(BigDecimal.valueOf(19), rate);
    }

    @Test
    void rateNewAgeSmale() throws IOException {
        ScoringDataDTO scoringDataDTO = objectMapper.readValue(new File("src/test/resources/calculationService/NewAgeTest.json"), ScoringDataDTO.class);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                calculationService.rate(scoringDataDTO));
        assertEquals("отказ", illegalArgumentException.getMessage());
    }

    @Test
    void rateWorkExperienceCurrentSmale() throws IOException {
        ScoringDataDTO scoringDataDTO = objectMapper.readValue(new File("src/test/resources/calculationService/NewWorkExperienceCurrentTest.json"), ScoringDataDTO.class);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () ->
                calculationService.rate(scoringDataDTO));
        assertEquals("отказ", illegalArgumentException.getMessage());
    }

    @Test
    void monthlyPaymentAmount() throws IOException {
        ScoringDataDTO scoringDataDTO = objectMapper.readValue(new File("src/test/resources/calculationService/test.json"), ScoringDataDTO.class);
        BigDecimal bigDecimal = calculationService.monthlyPaymentAmount(scoringDataDTO, BigDecimal.valueOf(17));
        assertNotNull(bigDecimal);
        assertEquals(BigDecimal.valueOf(17254.84), bigDecimal);
    }

    @Test
    void psk() throws IOException {
        ScoringDataDTO scoringDataDTO = objectMapper.readValue(new File("src/test/resources/calculationService/test.json"), ScoringDataDTO.class);
        BigDecimal psk = calculationService.psk(scoringDataDTO, BigDecimal.valueOf(17254.84));
        assertNotNull(psk);
        double pskOld = psk.doubleValue();
        assertEquals(Double.valueOf(8.0), pskOld);
    }

    @Test
    void paymentScheduleElement() throws IOException {
        ScoringDataDTO scoringDataDTO = objectMapper.readValue(new File("src/test/resources/calculationService/test.json"), ScoringDataDTO.class);
        List<PaymentScheduleElement> paymentScheduleElements = calculationService.paymentScheduleElement(scoringDataDTO, BigDecimal.valueOf(17), BigDecimal.valueOf(17254.84));
        assertNotNull(paymentScheduleElements);
        assertEquals(6, paymentScheduleElements.size());
    }

    @Test
    void creditDTO() throws IOException {
        ScoringDataDTO scoringDataDTO = objectMapper.readValue(new File("src/test/resources/calculationService/test.json"), ScoringDataDTO.class);
        calculationService.creditDTO(scoringDataDTO);
        assertNotNull(scoringDataDTO);
    }
}