# doma-spring-boot-demo

[![doma-spring-boot-demo](https://github.com/backpaper0/doma-spring-boot-demo/workflows/doma-spring-boot-demo/badge.svg)](https://github.com/backpaper0/doma-spring-boot-demo/actions?query=workflow%3Adoma-spring-boot-demo)

## Build native image

```
./mvnw spring-boot:build-image
docker run -it --rm -p 8080:8080 demo:0.0.1-SNAPSHOT
```

```
curl localhost:8080/
```

