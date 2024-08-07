# hhplus-tdd-week01
항해 플러스 백앤드5기 1주차


## [ Chapter 1-1 ] Test Driven Development


### :black_nib: 과제
[ point 패키지의 TODO 와 테스트코드를 작성해주세요. ]
* PATCH  `/point/{id}/charge` : 포인트를 충전한다.
* PATCH `/point/{id}/use` : 포인트를 사용한다.
* GET `/point/{id}` : 포인트를 조회한다.
* GET `/point/{id}/histories` : 포인트 내역을 조회한다.
* 잔고가 부족할 경우, 포인트 사용은 실패하여야 합니다.
* 동시에 여러 건의 포인트 충전, 이용 요청이 들어올 경우 순차적으로 처리되어야 합니다.


### :black_nib: 과제 평가 기준
[ Step 1 ]
* 테스트 케이스의 작성 및 작성 이유 주석의 작성 여부
* 프로젝트 내의 주석에 필요한 기능의 작성 여부
* 단위테스트 구현 여부

[ Step 2 ]
* 로컬에서 동시성 제어 및 관련 통합 테스트 작성 여부

[ 과제 제출 ] 
* 코드 리뷰 받고 싶은 부분들을 PR 형태로 제공
* 프로젝트 셋팅을 위한 커밋들은 제외


### :date: Schedule
* 6.18 ( 화요일 ) - Step 1
  - TDD로 개발하기 과제 `PR 링크` 제출
    ** 평가기준
     * 테스트 케이스의 작성 및 작성 이유 주석의 작성 여부
     * 프로젝트 내의 주석에 필요한 기능의 작성 여부
     * 단위테스트 구현 여부
      
* 6.21 ( 금요일 ) - Step 2
  - Step1 과제 리팩토링 `PR 링크` 제출
    ** 평가기준
     * 로컬에서 동시성 제어 및 관련 통합 테스트 작성 여부


#### :pencil2: Test Double ?
* Mock
  * 테스트를 위해 특정 기능에 대해 정해진 응답을 제공하는 객체
  * 입력과 상관없이 어떤 행동 을 할 지에 초점을 맞춘 객체
  * Mock Library 를 통해 특정 행동에 대한 출력을 정의
* Stup
  * 테스트에 필요한 호출에 대해 미리 준비된 응답을 제공하는 객체
  * 입력에 대해 어떤 상태 를 반영하는 지에 초점을 맞춘 객체
* Mock ( 행동 ) vs Stub ( 상태 )
  * Mock 의 장점 = 테스트마다 응답을 원하는 대로 지정하기 쉽다. (But 재사용이 안됨, `MockLibrary`의 도움을 받음으로 인해 라이브러리에 의한 러닝커브 존재.)
  * Stub 의 장점 = `UserRepository` 라는 걸 Stub 한 `UserFakeRepository` 라는 구현체가 있을 경우 `UserRepository` 를 쓰는 모든 단위 테스트에서 가져다 쓸 수 있다.


#### :pencil2: TDD 접근법 ?
1. 요구사항 분석 및 TD 작성 ( ~~ 면 안된다 )
   ex) 유저가 잔액이 부족하면 주문이 실패한다.
       상품이 썩었으면 주문이 실패한다.
       상품 재고가 부족하면 주문이 실패한다.
2. Test 코드 작성
   * 기능구현이 안되어 있으므로 Test 코드는 실패
   * Test 코드를 작성하면서 기능에 필요한 파라미터 파악한 내용을 토대로 Test 코드가 성공하도록 기능을 조악하게 구현
3. 코드 리팩토링 (이 과정에서 기능 분리했다면 테스트 코드 이관 작업도 같이)
4. 1부터 다시 반복 (한 두 싸이클 정도 돌리면 기능 완성)


#### :pencil2: 동시성 제어 ?
* ConcurrentHashMap을 이용, 각 유저별로 ReentrantLock을 관리하여 각 요청에 대해 순차적으로 잠금 획득
* 동일 자원에 대한 동시성 제어 여부 테스트
  * CompletableFuture.allOf()를 이용한 로컬에서의 동시성 제어 테스트
