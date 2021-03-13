# Reactive Spring Native applications 

Build using: 

`mvn clean spring-boot:build-image  `

Invoke the RSocket endpoint: 

`rsc tcp://localhost:8181 -r hello.world` 

Invoke the HTTP endpoint: 

`curl http://localhost:8080/customers`

