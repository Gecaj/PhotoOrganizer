package com.cislo.photos;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jcislo on 3/16/17.
 */

public class TimeExtractor {
    private static final Map<Integer, String> INT_TO_MONTH = new HashMap<>();
    static {
        INT_TO_MONTH.put(1, "Styczeń");
        INT_TO_MONTH.put(2, "Luty");
        INT_TO_MONTH.put(3, "Marzec");
        INT_TO_MONTH.put(4, "Kwiecień");
        INT_TO_MONTH.put(5, "Maj");
        INT_TO_MONTH.put(6, "Czerwiec");
        INT_TO_MONTH.put(7, "Lipiec");
        INT_TO_MONTH.put(8, "Sierpień");
        INT_TO_MONTH.put(9, "Wrzesień");
        INT_TO_MONTH.put(10, "Październik");
        INT_TO_MONTH.put(11, "Listopad");
        INT_TO_MONTH.put(12, "Grudzień");
    }

    public String readCreationMonth(LocalDateTime creationTime) {
        int month = creationTime.get(ChronoField.MONTH_OF_YEAR);
        return INT_TO_MONTH.get(month);
    }

    public String readCreationYear(LocalDateTime creationTime) {
        return String.valueOf(creationTime.get(ChronoField.YEAR));
    }
}
