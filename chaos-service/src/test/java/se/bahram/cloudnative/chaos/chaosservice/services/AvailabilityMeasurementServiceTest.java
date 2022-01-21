package se.bahram.cloudnative.chaos.chaosservice.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import se.bahram.cloudnative.chaos.chaosservice.domain.ChaoticInstance;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class AvailabilityMeasurementServiceTest {

    private final String DEFAULT_URL = "http://192.168.1.14";

    @Mock
    RestTemplate restTemplate =  new RestTemplate();

    AvailabilityMeasurementService availabilityMeasurementService;

    @BeforeEach
    void setup() {
        availabilityMeasurementService = new AvailabilityMeasurementService(restTemplate);
    }

    @Test
    void when_ping_with_null_then_throw_null_pointer_exception() {
        // when, then
        assertThatThrownBy(() -> availabilityMeasurementService.ping(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void when_ping_instance_then_get_instance_by_url_should_not_return_empty() {
        // given
        mock_that_chaotic_instance_is_available();
        // when
        availabilityMeasurementService.ping(new ChaoticInstance(DEFAULT_URL));
        // then
        assertThat(availabilityMeasurementService.getInstanceByUrl(DEFAULT_URL).isPresent()).isEqualTo(true);
    }

    @Test
    void given_instance_is_available_when_ping_instance_then_instance_birth_time_should_not_be_null() {
        // given
        mock_that_chaotic_instance_is_available();
        // when
        availabilityMeasurementService.ping(new ChaoticInstance(DEFAULT_URL));
        // then
        ChaoticInstance instanceByUrl = availabilityMeasurementService.getInstanceByUrl(DEFAULT_URL).get();
        assertThat(instanceByUrl.getBirthDate()).isNotNull();
    }

    @Test
    void given_instance_is_not_available_from_the_beginning_when_ping_two_times_then_down_time_should_be_null() {
        // given
        ChaoticInstance chaoticInstance = new ChaoticInstance(DEFAULT_URL);
        mock_that_chaotic_instance_is_not_available();
        // when
        availabilityMeasurementService.ping(chaoticInstance);
        availabilityMeasurementService.ping(chaoticInstance);
        // then
        ChaoticInstance instanceByUrl = availabilityMeasurementService.getInstanceByUrl(DEFAULT_URL).get();
        assertThat(instanceByUrl.getDowntime()).isNull();
    }

    @Test
    void given_instance_is_available_from_the_beginning_when_it_is_not_available_and_ping_two_times_then_down_time_should_not_be_null() {
        // given
        ChaoticInstance chaoticInstance = new ChaoticInstance(DEFAULT_URL);
        mock_that_chaotic_instance_is_available();
        // when
        availabilityMeasurementService.ping(chaoticInstance);
        // when
        mock_that_chaotic_instance_is_not_available();
        availabilityMeasurementService.ping(chaoticInstance);
        availabilityMeasurementService.ping(chaoticInstance);
        // then
        ChaoticInstance instanceByUrl = availabilityMeasurementService.getInstanceByUrl(DEFAULT_URL).get();
        assertThat(instanceByUrl.getDowntime()).isNotNull();
        // when
        mock_that_chaotic_instance_is_available();
        availabilityMeasurementService.ping(chaoticInstance);
        // then
        instanceByUrl = availabilityMeasurementService.getInstanceByUrl(DEFAULT_URL).get();
        assertThat(instanceByUrl.getDowntimeStart()).isNull();
    }

    @Test
    void given_instance_is_available_when_ping_two_times_then_down_time_start_should_be_null() {
        // given
        ChaoticInstance chaoticInstance = new ChaoticInstance(DEFAULT_URL);
        mock_that_chaotic_instance_is_available();
        // when
        availabilityMeasurementService.ping(chaoticInstance);
        availabilityMeasurementService.ping(chaoticInstance);
        // then
        ChaoticInstance instanceByUrl = availabilityMeasurementService.getInstanceByUrl(DEFAULT_URL).get();
        assertThat(instanceByUrl.getDowntimeStart()).isNull();
    }

    private void mock_that_chaotic_instance_is_available() {
        Mockito.reset(restTemplate);
        lenient().when(restTemplate.exchange(DEFAULT_URL, HttpMethod.GET, null, String.class, Map.of()))
                .thenReturn(ResponseEntity.ok("Pong"));
    }

    private void mock_that_chaotic_instance_is_not_available() {
        Mockito.reset(restTemplate);
        lenient().when(restTemplate.exchange(DEFAULT_URL, HttpMethod.GET, null, String.class, Map.of())).thenThrow(RuntimeException.class);
    }
}
