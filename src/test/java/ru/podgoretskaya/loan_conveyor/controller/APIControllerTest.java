package ru.podgoretskaya.loan_conveyor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.podgoretskaya.loan_conveyor.dto.LoanApplicationRequestDTO;
import ru.podgoretskaya.loan_conveyor.dto.LoanOfferDTO;
import ru.podgoretskaya.loan_conveyor.service.CalculationService;
import ru.podgoretskaya.loan_conveyor.service.OffersService;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(APIController.class)
public class APIControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OffersService offersService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getOffersPages() throws Exception {
        LoanApplicationRequestDTO loanApplicationRequestDTO= new LoanApplicationRequestDTO();
        mockMvc.perform(post("/conveyor/offers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                                    {
                                        "firstName":"qwe",
                                        "middleName":"qwe",
                                        "lastName":"qwe",
                                        "birthdate":"1994-12-13",
                                        "passportSeries":"1234",
                                        "passportNumber":"123456",
                                        "email":"a@mail.ru",
                                        "amount":100000,
                                        "term":12
                                    }
                        """))
            .andExpect(status().isBadRequest());
    }


//    @Test
//    void testGetOffersPages() {
//    }
}