package se.bahram.cloudnative.chaos.chaosservice.domain;

import org.apache.commons.validator.routines.UrlValidator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;

import static org.apache.commons.lang3.Validate.*;

public class ChaoticInstance {

    private final String url;

    String[] schemes = {"http", "https"};
    UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_ALL_SCHEMES);
    private Date birthDate;
    private Date downtimeStart;
    private Long downtime;

    public ChaoticInstance(String url) {
        notNull(url, "Url is null");
        if (!urlValidator.isValid(url)) {
            throw new IllegalArgumentException();
        }
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Date getBirthDate() {
        return this.birthDate;
    }

    public void setBirthDate(Date date) {
        this.birthDate = date;
    }

    public Long getDowntime() {
        return this.downtime;
    }

    public void setDowntimeStart(Date date) {
        this.downtimeStart = date;
    }

    public Date getDowntimeStart() {
        return this.downtimeStart;
    }

    public void setDowntime(Long downTime) {
        this.downtime = downTime;
    }

    public double percentageOfAvailability() {
        Long nowTime = System.currentTimeMillis();
        if (Objects.nonNull(getBirthDate())) {
            double totalTime = nowTime - getBirthDate().getTime();
            if (Objects.nonNull(getDowntime())) {
                double availableTime = (totalTime - getDowntime());
                double availableTimePercentage = (availableTime / totalTime) * 10000;
                return Math.ceil(availableTimePercentage) / 100;
            } else {
                return 100;
            }
        }
        return 0;
    }
}
