# SSE(Server-Sent Events)

## 동작 순서
###  클라이언트 연결
  - 브라우저가 /subscribe 호출
  - 해당 Spring Boot 인스턴스가 SseEmitter 생성
  - 현재 인스턴스 메모리에 저장

### 이벤트 발행
  - 어느 인스턴스에서 POST /notifications/broadcast 호출
  - RedisPubSubService.publish()
  - Redis 채널에 JSON 메시지 발행

### Redis 메시지 수신
  - 모든 인스턴스의 RedisSubscriber가 그 메시지 받음

### 각 인스턴스에서 로컬 SSE 전송
  - 각 인스턴스는 자기 SseEmitterRepository에 있는 클라이언트들에게만 broadcastLocal()

## Redis 실행

```shell
docker run --name local-redis -p 6379:6379 -d redis:7
```

## 테스트 : 클라이언트로 브라우저 이용

### 인스턴스 2개 실행
  - 인스턴스 1은 8080에서 실행

```shell
./gradlew bootRun
```

  - 인스턴스 2는 8081에서 실행

```shell
./gradlew bootRun --args='--server.port=8081'
```

  - 브라우저 2개를 띄워서
    - http://localhost:8080
    - http://localhost:8081
  - 8080 쪽으로 이벤트 발행
```shell
curl -X POST http://localhost:8080/notifications/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message":"hello from 8080"}'
```

## 테스트 : 클라이언트로 curl 이용

### 인스턴스 1개 실행

```shell
./gradlew bootRun
```

### 구독(SSE 연결)

  - 터미널을 2개 연다.
  - 두개의 터미널에서 curl로 SSE 연결
```shell
curl -N http://localhost:8080/subscribe
```

### 다른 터미널에서 이벤트 발생
  - 터미널을 연다.
  - 터미널에서 이벤트 발생

```shell
curl -X POST http://localhost:8080/notifications/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message":"hello"}'
```
