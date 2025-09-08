

### 클래스 다이어그램 (Ports & Adapters + 예외/도메인 모델)

```mermaid
classDiagram
    %% --- Domain/Port ---
    class BillingGateway {
      <<interface>>
      <<port>>
      +issueReceipt(id: String, amount: Long): Either<BillingError, Receipt>
    }

    class RestBillingGateway {
      <<adapter>>
      -http: BillingClient
      +issueReceipt(id: String, amount: Long): Either<BillingError, Receipt>
    }

    BillingGateway <|.. RestBillingGateway

    %% --- Client (External System) ---
    class BillingClient {
      <<client>>
      +postIssue(campaignId: String, amount: Long): Receipt
    }

    class FakeBillingClient {
      -failRate: Double
      +postIssue(campaignId: String, amount: Long): Receipt
    }

    class HttpBillingClient {
      -rest: RestClient
      -endpoint: String = "/api/v1/receipts"
      +postIssue(campaignId: String, amount: Long): Receipt
    }

    BillingClient <|.. FakeBillingClient
    BillingClient <|.. HttpBillingClient
    RestBillingGateway --> BillingClient : uses

    %% --- Exceptions (Client-side) ---
    class BillingClientException {
      <<RuntimeException>>
      +message: String
      +cause: Throwable?
    }
    class BillingClientBadRequest
    class BillingClientUnavailable

    BillingClientException <|-- BillingClientBadRequest
    BillingClientException <|-- BillingClientUnavailable
    BillingClient .. BillingClientException : throws

    %% --- Domain Error (Typed Error for Either.Left) ---
    class BillingError {
      <<sealed>>
      <<interface>>
    }
    class Technical {
      +where: String
      +cause: Throwable
    }
    BillingError <|-- Technical

    %% --- Success Model ---
    class Receipt {
      +campaignId: String
      +amount: Long
      +txId: String
    }

    %% --- External Framework (note) ---
    class RestClient {
      <<framework>>
    }
    HttpBillingClient --> RestClient : composes
```

**해설 요약**

* `BillingGateway`는 **Port(도메인 인터페이스)**, `RestBillingGateway`는 **Adapter(인프라)**.
* Adapter는 `BillingClient`(외부 청구 시스템 클라이언트)를 사용해 **HTTP 호출을 감싸고**,
  발생한 `BillingClientException`을 **도메인 타입드 에러**(`BillingError.Technical`)로 **매핑**하여 `Either.Left`로 반환.
* 성공 시 `Receipt`를 `Either.Right`로 반환.
* `BillingClient` 구현은 **Fake**(테스트용) / **HttpBillingClient**(실서비스용) 두 가지.

---

### 시퀀스 다이어그램 (호출 흐름 & 에러 매핑)

```mermaid
sequenceDiagram
    autonumber
    participant S as Service/UseCase
    participant G as RestBillingGateway (Adapter)
    participant C as BillingClient
    participant H as HttpBillingClient
    participant R as External Billing API

    Note over S: 도메인 서비스는 Port만 의존<br/>issueReceipt(id, amount): Either<BillingError, Receipt>

    S->>G: issueReceipt(id, amount)
    alt Adapter uses Client
        G->>C: postIssue(id, amount)
        opt 구현: HttpBillingClient
            C->>H: delegate
            H->>R: HTTP POST /api/v1/receipts {id, amount}
            alt 2xx 성공
                R-->>H: {campaignId, amount, txId}
                H-->>G: Receipt
                G-->>S: Right(Receipt)
            else 4xx/5xx/네트워크 오류
                R-->>H: 4xx/5xx or Exception
                H-->>G: throws BillingClientException(...)
                Note over G: Arrow either { catch { ... } { BillingError.Technical(...) } }
                G-->>S: Left(BillingError.Technical)
            end
        end
    end
```

**핵심 포인트**

* **성공 경로**: `Receipt` → `Either.Right`.
* **실패 경로**: `BillingClientException`(외부/HTTP/직렬화 등) → **어댑터에서** `BillingError.Technical`로 **변환** → `Either.Left`.
* 상위(Service/UseCase/Controller)는 **타입드 에러**만 받으므로, HTTP 상태 매핑/사용자 메시지/재시도 정책 등을 **도메인 규칙대로** 결정하기 쉽습니다.

