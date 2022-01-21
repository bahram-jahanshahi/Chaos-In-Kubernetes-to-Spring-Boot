package se.bahram.cloudnative.chaos.chaosservice.domain;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class ChaoticInstanceTest {

    @Test
    void when_url_is_null_throw_illegal_argument_exception() {
        assertThatThrownBy(() -> new ChaoticInstance(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Url is null");
    }

    @Test
    void when_url_is_malformed_throw_exception() {
        assertThatThrownBy(() -> new ChaoticInstance("bbb"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void when_url_is_correct_then_getUrl_equals_to_url() {
        // when
        String url = "https://127.0.0.1:8080";
        ChaoticInstance chaoticInstance = new ChaoticInstance(url);
        // then
        assertThat(chaoticInstance.getUrl()).isEqualTo(url);
    }

    @Test
    void it_should_have_availability_percentage() {
        // when
        String url = "https://127.0.0.1:8080";
        ChaoticInstance chaoticInstance = new ChaoticInstance(url);
        long onHourBeforeTime = new Date().getTime() - (60 * 60 * 1000);
        long oneMinuteDownTime = 60 * 1000;
        chaoticInstance.setBirthDate(new Date(onHourBeforeTime));
        chaoticInstance.setDowntime(oneMinuteDownTime);
        assertThat(chaoticInstance.percentageOfAvailability()).isEqualTo(98.34);
    }
}