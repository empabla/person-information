package pl.kurs.personinformation.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ImportStatus {

    private boolean inProgress;

    private boolean isCompleted;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private long processedRows;

}
