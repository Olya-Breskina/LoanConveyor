package ru.podgoretskaya.loan_conveyor.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.podgoretskaya.loan_conveyor.service.CalculationService;
import ru.podgoretskaya.loan_conveyor.service.OffersService;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(APIController.class)
public class APIControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    OffersService offersService;
    @MockBean
    CalculationService calculationService;

    @Test
    void getOffersPagesGood() throws Exception {
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
                .andExpect(status().isOk());
    }

    @Test
    void getOffersPagesBedTwo() throws Exception {// нет отчества
        when(offersService.loanOptions(any())).thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getOffersPagesBedOne() throws Exception {// пустой json
        when(offersService.loanOptions(any())).thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/conveyor/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                            {
                                                "firstName":"qwe",
                                                "middleName":"qwe",
                                                "lastName":"qwe",
                                                "birthdate":"1994-12-13",
                                                "passportNumber":"123456",
                                                "email":"a@mail.ru",
                                                "amount":100000,
                                                "term":12
                                            }
                                """))
                .andExpect(status().isBadRequest());
        verify(offersService,times(1)).loanOptions(any());

    }

    @Test
    void testGetOffersPagesGood() throws Exception {
        mockMvc.perform(post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                            {
                                                  "amount": 100000,
                                                          "term": 6,
                                                          "firstName": "qwe",
                                                          "middleName": "qwe",
                                                          "lastName": "qwe",
                                                          "gender": "FEMALE",
                                                          "birthdate": "1994-01-14",
                                                          "passportSeries": "1234",
                                                          "passportNumber": "123456",
                                                          "passportIssueDate": "2015-01-14",
                                                          "passportIssueBranch": "qazwsx",
                                                          "maritalStatus": "MARRIED",
                                                          "dependentAmount": "10",
                                                          "employment": {
                                                              "employmentStatus": "SELF_EMPLOYED",
                                                              "employerINN": "123-456-789",
                                                              "salary": 6000,
                                                              "position": "OWNER",
                                                              "workExperienceTotal": 7,
                                                              "workExperienceCurrent": 6
                                                          },
                                                          "account": "qaz",
                                                          "isInsuranceEnabled": "false",
                                                          "isSalaryClient": "false"
                                            }
                                """))
                .andExpect(status().isOk());
    }
    @Test
    void testGetOffersPagesBedOne() throws Exception {
        when(calculationService.creditDTO(any())).thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void testGetOffersPagesBedTwo() throws Exception {
        when(calculationService.creditDTO(any())).thenThrow(new IllegalArgumentException());
        mockMvc.perform(post("/conveyor/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                            {
                                                  "amount": 100000,
                                                          "term": 6,
                                                          "firstName": "qwe",
                                                          "middleName": "qwe",
                                                          "lastName": "qwe",
                                                          "gender": "FEMALE",
                                                          "birthdate": "1994-01-14",
                                                          "passportSeries": "1234",
                                                          "passportNumber": "123456",
                                                          "passportIssueDate": "2015-01-14",
                                                          "passportIssueBranch": "qazwsx",
                                                          "maritalStatus": "MARRIED",
                                                          "dependentAmount": "10",
                                                          "employment": {
                                                              "employmentStatus": "SELF_EMPLOYED",
                                                              "employerINN": "123-456-789",
                                                              "salary": 6000,
                                                              "position": "OWNER",
                                                              "workExperienceTotal": 7,
                                                              "workExperienceCurrent": 6
                                                          },
                                                          "account": "qaz",
                                                          "isInsuranceEnabled": "false",
                                                          "isSalaryClient": "false"
                                            }
                                """))
                .andExpect(status().isBadRequest());
        verify(calculationService,times(1)).creditDTO(any());
    }
}