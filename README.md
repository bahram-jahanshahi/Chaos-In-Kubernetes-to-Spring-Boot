# Chaos In Kubernetes to Spring Boot
This project aims to build a chaotic environment for a simple service in a Kubernetes cluster. The chaos is to challenge the availability of a service.  
For this goal, It's needed to have a mechanism to make system resilient and robust. Then it should be possible to measure and monitor the availability of the service.  
Everytime chaos is caused, system should be resilient enough and be ready for failover. Kubernetes cluster is capable to check the healthiness and readiness of each pod, therefore it's a good solution to benefit this feature.  

## Chaotic Environment vs Smooth Environment 

## Chaos Service
Chaos service aims to challenge the availability of particular service. In our example this service send a http request to that particular service in order to make it unavailable constantly and periodically.

## Simple Service 
This service has a **_chaotic http endpoint_** (/chaos/availability) to end chaos in terms of availability. 

## Trusted Computing Base via JWT
It's important that just the chaos service is permitted to invoke the chaotic http endpoint. Therefore, this endpoint should verify the chaos service by JWT.

## Measuring The Availability
There should be a mechanism to measure the availability of the simple service: it should be for example 99.99 percent.

## Chaos Metrics
Every time chaos service sends a request to chaotic http endpoint, the timeline metric should send to a monitoring party in order to check out the availability of the service in those times.

