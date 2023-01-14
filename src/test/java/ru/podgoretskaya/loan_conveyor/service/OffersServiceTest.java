package ru.podgoretskaya.loan_conveyor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import ru.podgoretskaya.loan_conveyor.dto.LoanApplicationRequestDTO;
import ru.podgoretskaya.loan_conveyor.dto.LoanOfferDTO;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class OffersServiceTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Spy
    OffersService offersService = new OffersService(20, 3, 1, BigDecimal.valueOf(100000), 6, 18);

    @BeforeEach
    void beforeAll() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void loanOptions() throws IOException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = objectMapper.readValue(new File("src/test/resources/offersService/test.json"), LoanApplicationRequestDTO.class);
        List<LoanOfferDTO> loanOfferDTOS = offersService.loanOptions(loanApplicationRequestDTO);
        assertNotNull(loanOfferDTOS);
        assertEquals(4, loanOfferDTOS.size());
    }
    @Test
    void loanOptionsWithoutMiddleName() throws IOException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = objectMapper.readValue(new File("src/test/resources/offersService/testNotMiddleName.json"), LoanApplicationRequestDTO.class);
        List<LoanOfferDTO> loanOfferDTOS = offersService.loanOptions(loanApplicationRequestDTO);
        assertNotNull(loanOfferDTOS);
        assertEquals(4, loanOfferDTOS.size());
    }
    @Test()
    void loanOptionsShortFirstName() throws IOException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = objectMapper.readValue(new File("src/test/resources/offersService/testNotFirstName.json"), LoanApplicationRequestDTO.class);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> offersService.loanOptions(loanApplicationRequestDTO));
        assertEquals("проверьте ФИО", illegalArgumentException.getMessage());
    }
    @Test
    void loanOptionsLongLastName() throws IOException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = objectMapper.readValue(new File("src/test/resources/offersService/testNotLastName.json"), LoanApplicationRequestDTO.class);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> offersService.loanOptions(loanApplicationRequestDTO));
        assertEquals("проверьте ФИО", illegalArgumentException.getMessage());
    }
    @Test
    void loanOptionsBirthdate() throws IOException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = objectMapper.readValue(new File("src/test/resources/offersService/testYounger18.json"), LoanApplicationRequestDTO.class);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> offersService.loanOptions(loanApplicationRequestDTO));
        assertEquals("проверьте дату рождения", illegalArgumentException.getMessage());
    }
    @Test
    void loanOptionsPassportSeries() throws IOException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = objectMapper.readValue(new File("src/test/resources/offersService/testPassportSeries.json"), LoanApplicationRequestDTO.class);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> offersService.loanOptions(loanApplicationRequestDTO));
        assertEquals("проверьте данные паспорта", illegalArgumentException.getMessage());
    }
    @Test
    void loanOptionsEmail() throws IOException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = objectMapper.readValue(new File("src/test/resources/offersService/testEmail.json"), LoanApplicationRequestDTO.class);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> offersService.loanOptions(loanApplicationRequestDTO));
        assertEquals("неверный email", illegalArgumentException.getMessage());
    }
    @Test
    void loanOptionsTermSmale() throws IOException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = objectMapper.readValue(new File("src/test/resources/offersService/testTermSmale.json"), LoanApplicationRequestDTO.class);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> offersService.loanOptions(loanApplicationRequestDTO));
        assertEquals("увеличите срок кредита", illegalArgumentException.getMessage());
    }
    @Test
    void loanOptionsAmoutSmale() throws IOException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = objectMapper.readValue(new File("src/test/resources/offersService/testAmoutSmale.json"), LoanApplicationRequestDTO.class);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> offersService.loanOptions(loanApplicationRequestDTO));
        assertEquals("увеличите сумму кредита", illegalArgumentException.getMessage());
    }
    @Test
    void loanOptionsPassportNumber() throws IOException {
        LoanApplicationRequestDTO loanApplicationRequestDTO = objectMapper.readValue(new File("src/test/resources/offersService/testPassportNumber.json"), LoanApplicationRequestDTO.class);
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> offersService.loanOptions(loanApplicationRequestDTO));
        assertEquals("проверьте данные паспорта", illegalArgumentException.getMessage());
    }
}