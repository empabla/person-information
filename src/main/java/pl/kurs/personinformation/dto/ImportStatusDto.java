package pl.kurs.personinformation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ImportStatusDto {

    private String status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private long processedRows;

}
