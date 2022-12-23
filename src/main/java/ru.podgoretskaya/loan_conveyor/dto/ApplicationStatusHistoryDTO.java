package ru.podgoretskaya.loan_conveyor.dto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationStatusHistoryDTO {
    private String status;
    private LocalDateTime time;
    private ChangeType changeType;

    public enum ChangeType {
        AUTOMATIC, MANUAL
    }
}