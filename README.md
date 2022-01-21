# Chaos In Kubernetes to Spring Boot
This project aims to build a chaotic environment for a simple service in a Kubernetes cluster. The chaos is to challenge the availability of a service.  
For this goal, It's needed to have a mechanism to make system resilient and robust. Then it should be possible to measure and monitor the availability of the service.  
Everytime chaos is caused, system should be resilient enough and be ready for failover. Kubernetes cluster is capable to check the healthiness and readiness of each pod, therefore it's a good solution to benefit this feature.  

## Chaotic Environment vs Smooth Environment 

## SLO
The `availability percentage` of the simple service should be `99.9`.

## Architecture
### Chaos Service
Chaos service aims to challenge the availability of particular service. In our example this service send a http request to that particular service in order to make it unavailable constantly and periodically.

### Simple Service 
This service has a **_chaotic http endpoint_** (/chaos/availability) to end chaos in terms of availability. 

### Trusted Computing Base via JWT
It's important that just the chaos service is permitted to invoke the chaotic http endpoint. Therefore, this endpoint should verify the chaos service by JWT.

## Measuring The Availability
There should be a mechanism to measure the availability of the simple service: it should be for example 99.99 percent.  
### Who measures the availability?
Chaos service is responsible for measuring the _**uptime percentage**_ of the simple service. 
### How to measure the uptime percentage?
Chaos service constantly and periodically pings the simple service (each 60 seconds for example); if the response http status  of the ping is not 200, it means chaos service is not available. 
Then chaos service keeps this time as a `start time of unavailability` of the simple service. After 60 seconds chaos service pings the simple service again and if the service is not available then _**downtime**_ should be calculated and added to **_total downtime_** of simple service. 
After updating the total downtime of the simple service the `start time of unavailability` should be updated to current time. At the first time the simple service is pinged and found available the `birth time` of simple service should be persisted.   
The uptime percentage formula is:
```text
total_time = current_system_time - birth_time
uptime_percentage = ((total_time - total_down_time) / total_time)) * 100
```
### Reporting of the availability
The http endpoint is `/measurement/uptimes` and report schema is
```json
[
  {
    "instance_id": "simple-service",
    "current_status": "up",
    "availability_slo": 99.9,
    "availability_slo_satisfied": false,
    "birth_time": 238463956487345,
    "total_time": 234876,
    "downtime": 546,
    "uptime": 234330,
    "uptime_percentage": 99.76
  }
]
```

## Incident Management
If the chaos service's http endpoint `/measurement/uptimes/{instance_id}/availability_slo_satisfied` return `false` then it should be alerted as an **_incident_**.

