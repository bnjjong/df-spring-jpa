```scss
[Client/다른 모듈]
│   (퍼블릭 API에는 도메인 예외만 보임)
▼
[Public Service/Port]  ── throws DomainException(= 도메인 전용)
│
▼
[Adapter A] ──(catch A-lib 예외)→ map → DomainException
[Adapter B] ──(catch B-lib 예외)→ map → DomainException
│
▼
[Lib A / Lib B ...]
```