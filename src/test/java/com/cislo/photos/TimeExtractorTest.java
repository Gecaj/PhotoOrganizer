package com.cislo.photos;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


/**
 * Created by jcislo on 3/16/17.
 */
public class TimeExtractorTest {

    private TimeExtractor timeExtractor = new TimeExtractor();

    @Test
    public void shouldReadMonthCorrectly() {
        assertExtractsMonthToExpected(Month.JANUARY, "Styczeń");
        assertExtractsMonthToExpected(Month.FEBRUARY, "Luty");
        assertExtractsMonthToExpected(Month.MARCH, "Marzec");
        assertExtractsMonthToExpected(Month.APRIL, "Kwiecień");
        assertExtractsMonthToExpected(Month.MAY, "Maj");
        assertExtractsMonthToExpected(Month.JUNE, "Czerwiec");
        assertExtractsMonthToExpected(Month.JULY, "Lipiec");
        assertExtractsMonthToExpected(Month.AUGUST, "Sierpień");
        assertExtractsMonthToExpected(Month.SEPTEMBER, "Wrzesień");
        assertExtractsMonthToExpected(Month.OCTOBER, "Październik");
        assertExtractsMonthToExpected(Month.NOVEMBER, "Listopad");
        assertExtractsMonthToExpected(Month.DECEMBER, "Grudzień");
    }

    @Test
    public void shouldReadYearCorrectly() {
        LocalDateTime creationTime = LocalDateTime.of(2017, Month.JANUARY, 15, 0, 0);
        assertThat(timeExtractor.readCreationYear(creationTime)).isEqualTo("2017");
    }

    private void assertExtractsMonthToExpected(Month month, String expected) {
        LocalDateTime creationTime = LocalDateTime.of(2017, month, 15, 0, 0);
        assertThat(timeExtractor.readCreationMonth(creationTime)).isEqualTo(expected);
    }
}
