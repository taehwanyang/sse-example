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

## 테스트 : nginx ingress controller가 있을 때
  - 쿠버네티스에 파드로 애플리케이션을 배포한다면 클라이언트와 서버 사이에 nginx ingress controller가 있다.
  - 이 경우 nginx가 데이터를 모았다가 보낼 수도 있음.
  - 이 경우 sse 엔드포인트와 일반 API ingress를 분리한다.
  - 레디스와 sse-example 애플리케이션을 배포

```shell
cd k8s
kubectl apply -f redis-deployment.yaml
kubectl apply -f redis-service.yaml

kubectl apply -f sse-example-deploy.yaml
kubectl apply -f sse-example-service.yaml
kubectl apply -f sse-example-ingress-sse.yaml
kubectl apply -f  sse-example-ingress-api.yaml
```

  - 쿠버네티스에 배포된 ingress controller가 nginx라고 가정한다.
  - /etc/hosts에 도메인을 추가한다.

```shell
127.0.0.1       sse.ythwork.com
```

  - 요청 URL만 sse.ythwork.com으로 변경하여 테스트한다.

```shell
curl -N http://sse.ythwork.com/subscribe
```

```shell
curl -X POST http://sse.ythwork.com/notifications/broadcast \
  -H "Content-Type: application/json" \
  -d '{"message":"hello"}'
```
