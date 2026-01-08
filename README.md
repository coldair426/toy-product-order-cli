# toy-product-order-cli 

## 0. 실행 방법

> 아래 명령은 프로젝트 루트 디렉토리(`toy-product-order-cli`)에서 실행합니다.

### 0.1 테스트 실행

```bash
./gradlew test
```

- 실행 결과는 콘솔에 출력되며, 테스트 리포트는 아래 경로에서 확인할 수 있습니다.
  - `build/reports/tests/test/index.html`

### 0.2 애플리케이션 실행

```bash
./gradlew bootRun
```

- `bootRun`은 Spring Boot 애플리케이션을 실행합니다.
- 실행 후, CLI 진입점(프로젝트 구현에 따라 `OrderCliApplication` 또는 관련 Runner)이 동작합니다.

### 0.3 빌드(테스트 포함) 실행

```bash
./gradlew build
```

- `build`는 기본적으로 `test`를 포함하므로, 테스트까지 함께 검증하고 싶을 때 사용합니다.

### 0.4 유용한 옵션

```bash
# 특정 테스트 클래스만 실행
./gradlew test --tests "com.ckmall.order.domain.model.OrderTest"

# 특정 테스트 메서드만 실행
./gradlew test --tests "com.ckmall.order.application.service.CreateOrderServiceTest.여러 주문이 동시에 재고를 초과하면 SoldOutException이 발생한다"

# Gradle 데몬/캐시 문제 의심 시 클린 후 재실행
./gradlew clean test
```

## 1. 프로젝트 개요

본 프로젝트는 **상품 주문(Order) 도메인**을 중심으로 도메인 모델링, 테스트 전략, 그리고 동시성 처리를 구현한 예제입니다.

단순 기능 구현에 그치지 않고, 다음 사항을 중점적으로 설계·구현하였습니다.

- 도메인 규칙을 코드로 명확히 표현하는 설계
- 도메인과 애플리케이션 책임을 명확히 분리
- 책임이 명확한 레이어 분리
- 테스트를 통한 비즈니스 규칙 검증
- 동시성 문제에 대한 안정적인 처리

## 2. 전체 구조

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

## 3. 도메인 설계 방향

### 3.1 Value Object (VO)

- `Money`, `OrderItem` 등은 값 자체가 의미를 가지는 VO로 설계
- 불변성을 보장
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
- 상태 변경은 의미 있는 메서드를 통해서만 가능

```kotlin
fun decrease(amount: Int) {
    require(amount > 0)
    if (quantity < amount) throw SoldOutException(productId)
    quantity -= amount
}
```

- 도메인 규칙은 엔티티 내부에 응집되도록 구현

## 4. 테스트 전략

### 4.1 테스트 범위

| 레이어           | 테스트 여부 | 이유                       |
|------------------|-------------|----------------------------|
| Domain           | ✅          | 핵심 비즈니스 규칙 검증     |
| Policy           | ✅          | 계산 로직 독립 검증          |
| Application(Service) | ✅          | 유즈케이스 단위 흐름 검증    |
| Adapter          | ❌          | 과제 범위 외                |

### 4.2 Mock vs Fake 전략

본 과제에서는 Mock보다 Fake 구현체를 우선 사용하였습니다.

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
- 재고 초과 주문이 발생할 수 있음

### 5.2 처리 전략

- 애플리케이션 레이어에서 임계 구역 설정
- 재고 조회와 차감 로직을 하나의 원자적 연산으로 묶음

```kotlin
synchronized(this) {
    if (!inventory.isAvailable(quantity)) {
        throw SoldOutException(productId)
    }
    inventory.decrease(quantity)
}
```

### 5.3 설계 판단 및 근거

- 도메인 엔티티는 순수한 비즈니스 규칙 표현에 집중
- 동시성 제어는 애플리케이션 레이어의 책임으로 분리

### 5.4 동시성 테스트

- `ExecutorService`와 `CountDownLatch`를 활용하여 실제 멀티 스레드 환경에서 `SoldOutException` 발생 여부를 검증

```kotlin
assertEquals(expected, soldOutExceptionCount)
```

- 구현이 “우연히 통과”하는 것이 아니라
- 경쟁 조건 환경에서 안전한지 확인

## 6. 요약

- 테스트는 문서이자 설계 설명서 역할
- 도메인 규칙은 엔티티 내부에 위치
- 동시성 문제는 애플리케이션 레이어에서 제어
- Fake Repository를 활용하여 현실적인 테스트 구성
- Mock에 의존하지 않는 안정적인 테스트 구조

## 7. 한계점 및 개선 방향

- 본 프로젝트는 단일 JVM / In-Memory 환경을 가정한 동시성 제어를 사용합니다.
- 실제 운영 환경에서는 DB Lock, Optimistic Lock, 또는 분산 락(Redis 등)을 활용한 설계가 필요합니다.
- 향후 Persistence 계층 교체 시에도 도메인 로직 변경 없이 확장 가능하도록 설계되었습니다.