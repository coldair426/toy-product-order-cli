# toy-product-order-cli

## 0. 실행 방법

- 아래 명령은 프로젝트 루트 디렉토리(`toy-product-order-cli`)에서 실행

### 0.1 환경 초기화 (선택)

```bash
./gradlew clean
```

- 이전 빌드 산출물(`build/`)을 모두 제거
- 빌드/실행 중 이상 동작이 있을 경우 실행을 권장

### 0.2 테스트 실행 (선택)

```bash
./gradlew test
```

- 모든 단위 테스트를 실행
- 실행 결과는 콘솔에 출력되며, 테스트 리포트는 아래 경로에서 확인할 수 있음
    - `build/reports/tests/test/index.html`

### 0.3 빌드 (테스트 포함)

```bash
./gradlew build
```

- `build`는 기본적으로 `test`를 포함
- 성공 시 실행 가능한 JAR 파일이 생성
    - `build/libs/order-0.0.1.jar`

### 0.4 애플리케이션 실행 (CLI)

```bash
java -jar build/libs/order-0.0.1.jar --spring.profiles.active=cli
```

- Spring Boot 애플리케이션을 JAR 형태로 실행하며, CLI 인터페이스가 동작
- IDE 실행 환경과 무관하게 동일한 방식으로 실행할 수 있음

## 1. 프로젝트 개요

- **본 프로젝트는 상품 주문(Order) 도메인을 중심으로 도메인 모델링, 테스트 전략, 그리고 동시성 처리를 구현한 예제**

### 1.0 설계·구현의 중점사항

- 도메인 규칙을 코드로 명확히 표현하는 설계
- 도메인과 애플리케이션 책임을 명확히 분리
- 책임이 명확한 레이어 분리
- 테스트를 통한 비즈니스 규칙 검증
- 동시성 문제에 대한 안정적인 처리

## 2. 전체 구조

### 2.1 구조 설계 의도

- 비즈니스 규칙을 중심에 두고,
- 입/출력(CLI, CSV, In-Memory Persistence 등)은 외부로 밀어내는 구조를 목표
- 테스트는 비즈니스 규칙을 중심으로 작성

#### Domain Layer(`domain`)

- 핵심 비즈니스 규칙을 표현하는 레이어
- Spring, IO, Repository 등 **기술적 요소에 의존하지 않음**
- Entity, VO, Policy 객체로 구성
- 도메인 스스로가 규칙을 보호하도록 설계(잘못된 상태 변경을 원천 차단)

```bash
domain
├── model        → Entity(Order, Inventory, Product), VO(Money, OrerLine)
├── policy       → 비즈니스 정책(배송비 정책)
└── exception    → 도메인 규칙 위반 예외
```

#### Application Layer(`application`)

- UseCase 흐름을 조합하고 실행하는 레이어
- 도메인 객체를 이용하지만, 도메인 내부 규칙을 침범하지 않음
- **무엇을 어떤 순서로 실행할지**의 책임
- **트랜젝션 경계 및 동시성 제어 위치**

```bash
application
├── usecase      → UseCase 청사진(Interface)
├── service      → UseCase 구현체
├── dto          → 입/출력 전용 모델
└── port         → 외부 의존성에 대한 청사진(Interface)
```

#### Adapter Layer(`adapter`)

- 외부와 시스템을 연결하는 레이어
- 환경에 따라 달라질 수 있는 요소 담당
- **Application, Domain Layer를 외부 기술로 부터 격리(보호)**

```bash
adapter
├── inbound      → CLI, CSV 등 입력 어댑터
└── outbound     → Repository 구현체 (In-Memory)
```

#### Bootstrap(`bootstrap`)

- App 초기화 담당

```bash
bootstrap
├── CsvDataInitializer
└── OrderCliApplication
```

### 2.2 Tree

```bash
src
├── main
│   ├── kotlin
│   │   └── com
│   │       └── ckmall
│   │           └── order
│   │               ├── OrderApplication.kt
│   │               ├── adapter
│   │               │   ├── inbound
│   │               │   │   ├── cli
│   │               │   │   │   └── OrderCliAdapter.kt
│   │               │   │   └── csv
│   │               │   │       ├── ProductCsvReader.kt
│   │               │   │       └── ProductCsvRow.kt
│   │               │   └── outbound
│   │               │       └── persistence
│   │               │           ├── InMemoryInventoryRepository.kt
│   │               │           ├── InMemoryOrderRepository.kt
│   │               │           └── InMemoryProductRepository.kt
│   │               ├── application
│   │               │   ├── dto
│   │               │   │   ├── CreateOrderDto.kt
│   │               │   │   └── ProductWithInventoryResponse.kt
│   │               │   ├── port
│   │               │   │   └── repository
│   │               │   │       ├── InventoryRepository.kt
│   │               │   │       ├── OrderRepository.kt
│   │               │   │       └── ProductRepository.kt
│   │               │   ├── service
│   │               │   │   ├── CreateOrderService.kt
│   │               │   │   └── GetAllProductsService.kt
│   │               │   └── usecase
│   │               │       ├── CreateOrderUseCase.kt
│   │               │       └── GetAllProductsUseCase.kt
│   │               ├── bootstrap
│   │               │   ├── CsvDataInitializer.kt
│   │               │   └── OrderCliApplication.kt
│   │               └── domain
│   │                   ├── exception
│   │                   │   └── SoldOutException.kt
│   │                   ├── model
│   │                   │   ├── Inventory.kt
│   │                   │   ├── Order.kt
│   │                   │   ├── Product.kt
│   │                   │   └── vo
│   │                   │       ├── Money.kt
│   │                   │       └── OrderLine.kt
│   │                   └── policy
│   │                       ├── DefaultShippingFeePolicy.kt
│   │                       └── ShippingFeePolicy.kt
│   └── resources
│       ├── application.yaml
│       └── products.csv
└── test
    └── kotlin
        └── com
            └── ckmall
                └── order
                    ├── adapter
                    │   └── outbound
                    │       └── persistence
                    │           ├── FakeInMemoryInventoryRepository.kt
                    │           ├── FakeInMemoryOrderRepository.kt
                    │           └── FakeInMemoryProductRepository.kt
                    ├── application
                    │   └── service
                    │       └── CreateOrderServiceTest.kt
                    └── domain
                        ├── model
                        │   ├── InventoryTest.kt
                        │   ├── OrderTest.kt
                        │   └── vo
                        │       ├── MoneyTest.kt
                        │       └── OrderLineTest.kt
                        └── policy
                            └── DefaultShippingFeePolicyTest.kt
```

### 2.3 요약

- 특정 아키텍처를 순수하게 적용하기 위한 설계가 아님
- 비즈니스 규칙을 가장 중심에 두고,
- 변경 가능성이 높은 요소를 외부로 밀어낸 결과이며
- 자연스럽게 헥사고날 아키텍처, 클린 아키텍처를 만족하는 구조가 되었음

## 3. 도메인 설계 방향

### 3.1 Value Object (VO)

- `Money`, `OrderItem` 등은 값 자체가 의미를 가지는 VO로 설계
- **불변성 보장**
- 생성 시점에 도메인 규칙 검증(`init {}`) 수행

```kotlin
data class Money private constructor(val amount: Long) {
    init {
        require(amount >= 0) { "금액은 항상 0 이상이어야 합니다." }
    }
}
```

### 3.2 Entity

- `Order`, `Inventory`, `Product` 등은 식별자를 가지는 엔티티로 설계
- **도메인 규칙은 엔티티 내부에 응집되도록 구현**
- 상태 변경은 의미 있는 메서드를 통해서만 가능

```kotlin
fun decrease(amount: Int) {
    require(amount > 0)
    if (quantity < amount) throw SoldOutException(productId)
    quantity -= amount
}
```

### 3.3 도메인을 이렇게 나눈 이유

- 단일 Bounded Context 안에서 **서로 다른 책임과 변경 이유를 가지는 도메인들을 분리**하여 설계
- **도메인 분리의 기준**
    - 비즈니스 책임의 명확성
    - 변경 이유의 분리(주문 도메인: 주문 비즈니스 정책이 바뀔 때 변경, 재고 도메인: 재고 및 동시성 정책이 바뀔 때 변경)
    - 동시성 요구사항의 차이
    - 확장 및 대체 가능성

#### 주문(Order) 도메인

- 주문이라는 비즈니스 행위 전체를 책임지는 영역
- **도메인 기능과 특성**
    - 중복 상품 병합
    - 주문 수량 관리
    - 상품 금액 합산
    - 배송비 정책 적용
    - 주문 총 금액 계산

#### 재고(Inventory) 도메인

- 상품 재고를 관리하는 영역
- **도메인 기능과 특성**
    - 여러 요청이 **동시에 접근**할 수 있음
    - 수량 변경이 매우 빈번함
    - 무결성이 깨질 경우 치명적인 오류로 이어짐
    - 동시성 제어의 대상이 되는 핵심 자원이며
    - 추후 DB Lock / Optimistic Lock / 분산 락 적용 가능성으로,
    - 최소 범위로 적용할 수 있도록 캡슐화

#### 상품(Product) 도메인

- 주문과 재고에서 공통으로 참조
- 자체적인 복잡한 비즈니스 규칙이나,
- 빈번한 상태 변경을 가지지 않음
- 때문에 독립적인 도메인 규칙 중심이 아닌 참조 역할의 엔티티로 설계

#### 도메인 분리의 결과

- 주문과 재고는 책임과 변경 이유에 따라 분리
- 각 도메인은 자신의 규칙에만 집중
- 동시성 문제는 재고 도메인에 국한되어 관리 가능
- 비즈니스 의미와 확장 가능성을 기준으로 한 설계

## 4. 테스트 전략

### 4.1 테스트 범위

| 레이어                  | 테스트 여부 | 이유                           |
|----------------------|--------|------------------------------|
| Domain               | ✅      | 핵심 비즈니스 규칙 검증                |
| Policy               | ✅      | 계산 로직 독립 검증                  |
| Application(Service) | ✅      | 유즈케이스 단위 흐름 검증               |
| Adapter              | ❌      | 비즈니스 규칙 및 동시성 검증을 제외한 부분은 제외 |

### 4.2 Mock vs Fake 전략

- 본 과제에서는 Mock보다 Fake 구현체를 우선 사용
- Fake 사용 이유
    - 상태 변화 검증 가능
    - 동시성 테스트 가능
    - 테스트가 구현 세부사항에 덜 의존
    - 비즈니스 흐름을 자연스럽게 표현 가능

```kotlin
class FakeInMemoryInventoryRepository(
    private val inventory: Inventory
) : InventoryRepository {
    private val inventories = inventories.associateBy { it.productId }.toMutableMap()

    override fun findByProductId(productId: String): Inventory? = inventories[productId]

    override fun findAll(): List<Inventory> = inventories.values.toList()

    override fun save(inventory: Inventory) {
        inventories[inventory.productId] = inventory
    }
}
```

## 5. 동시성 처리에 대한 접근

### 5.1 문제 인식

- 여러 주문이(쓰레드) 동시에 같은 재고를 차감할 경우
- 재고 초과 주문이 발생

### 5.2 처리 전략

- 애플리케이션 레이어에서 **임계 구역 설정**
- **재고 조회와 차감 로직을 하나의 원자적 연산**으로 묶음

```kotlin
synchronized(this) {
    if (!inventory.isAvailable(quantity)) {
        throw SoldOutException(productId)
    }
    inventory.decrease(quantity)
}
```

### 5.3 설계 판단 및 근거

- **동시성 문제는 도메인 자체 보다, 시스템이 도메인을 사용하는 방식의 문제로 판단**
- 도메인 엔티티는 순수한 비즈니스 규칙 표현에 집중
- 동시성 제어는 애플리케이션 레이어의 책임으로 분리
- 추후 DB락 등으로 **확장할 때도 도메인 변경 없이 대체** 가능

### 5.4 동시성 테스트

- `ExecutorService`와 `CountDownLatch`를 활용하여 실제 멀티 스레드 환경에서 `SoldOutException` 발생 여부를 검증
- 구현이 “우연히 통과”하는 것이 아니라,
- **경쟁 조건 환경에서 안전한지** 확인

## 6. 요약

- **테스트는 문서이자 설계 설명서 역할**
- **도메인 규칙은 엔티티 내부에 위치**
- **동시성 문제는 애플리케이션 레이어에서 제어**
- **Fake Repository를 활용하여 현실적인 테스트 구성**

## 7. 한계점 및 개선 방향

- 본 프로젝트는 **단일 JVM / In-Memory 환경**을 가정한 동시성 제어를 사용
- **실제 운영 환경에서는 DB Lock, Optimistic Lock, 또는 분산 락(Redis 등)을 활용한 설계가 필요**
- 향후 Persistence 계층 교체 시에도 도메인 로직 변경 없이 확장 가능하도록 설계