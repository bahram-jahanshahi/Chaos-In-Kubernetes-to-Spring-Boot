package se.bahram.cloudnative.chaos.chaosservice.services;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import se.bahram.cloudnative.chaos.chaosservice.domain.ChaoticInstance;

import java.net.SocketTimeoutException;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.*;


public class AvailabilityMeasurementService {

    final RestTemplate restTemplate;

    Map<String, ChaoticInstance> chaoticInstanceMap = new HashMap<>();

    public AvailabilityMeasurementService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.restTemplate.setRequestFactory(getSimpleClientHttpRequestFactory());
    }

    public void ping(ChaoticInstance chaoticInstance) {


        notNull(chaoticInstance);
        if (!chaoticInstanceMap.containsKey(chaoticInstance.getUrl())) {
            chaoticInstanceMap.put(chaoticInstance.getUrl(), chaoticInstance);
        }

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(chaoticInstance.getUrl(), HttpMethod.GET, null, String.class, Map.of());
            if (responseEntity.getStatusCode().equals(HttpStatus.OK) && responseEntity.getBody().equals("Pong") && Objects.isNull(chaoticInstance.getBirthDate())) {
                chaoticInstance.setBirthDate(new Date());
            }
            if (responseEntity.getStatusCode().equals(HttpStatus.OK) && responseEntity.getBody().equals("Pong")) {
                chaoticInstance.setDowntimeStart(null);
            }
        } catch (RuntimeException runtimeException) {
            Optional<ChaoticInstance> instanceByUrl = getInstanceByUrl(chaoticInstance.getUrl());
            if (instanceByUrl.isPresent() && Objects.nonNull(instanceByUrl.get().getBirthDate())) {
                if (Objects.isNull( instanceByUrl.get().getDowntimeStart() )) {
                    instanceByUrl.get().setDowntimeStart(new Date());
                } else {
                    Long previousDownTimeStart = instanceByUrl.get().getDowntimeStart().getTime();
                    Long now = System.currentTimeMillis();
                    Long currentDownTime = now - previousDownTimeStart;
                    Long totalDownTime = Objects.nonNull(instanceByUrl.get().getDowntime()) ? instanceByUrl.get().getDowntime() + currentDownTime : currentDownTime;
                    instanceByUrl.get().setDowntime(totalDownTime);
                    instanceByUrl.get().setDowntimeStart(new Date(now));
                }
            }
        }
    }

    public Optional<ChaoticInstance> getInstanceByUrl(String url) {
        return chaoticInstanceMap.containsKey(url) ? Optional.of(chaoticInstanceMap.get(url)) : Optional.empty();
    }

    public List<ChaoticInstance> getAllChaoticInstances() {
        return chaoticInstanceMap.values().stream().collect(Collectors.toList());
    }

    private SimpleClientHttpRequestFactory getSimpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory =
                new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(10_000);
        simpleClientHttpRequestFactory.setReadTimeout(10_000);
        return simpleClientHttpRequestFactory;
    }
}
