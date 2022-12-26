package ru.podgoretskaya.loan_conveyor.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class EmailMessage {
    private String address;
    private Theme theme;
    private Long applicationId;

    public enum Theme {
        FINISH_REGISTRATION, CREATE_DOCUMENTS, SEND_DOCUMENTS, SEND_SES, CREDIT_ISSUED, APPLICATION_DENIED
    }
}