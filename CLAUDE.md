# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 개요

Stockpedia 백엔드 — Spring Boot 4 (Java 21) 기반 REST API로, 커뮤니티/게시판 서비스(회원, 게시글, 댓글, 좋아요, 파일 업로드)를 제공한다. Gradle/Spring 모듈명은 `KTB`이며 모든 Java 코드는 `com.ktb` 베이스 패키지 아래에 위치한다.

## 빌드 & 테스트 명령어

```bash
./gradlew build              # 컴파일 + 단위 테스트 실행 (통합 테스트 제외)
./gradlew bootJar            # 실행 가능한 jar 빌드 (build/libs/*-SNAPSHOT.jar)
./gradlew bootRun            # 로컬 실행 (기본 프로파일: local)
./gradlew test               # 단위 테스트만 — `integration` JUnit 태그는 제외됨
./gradlew integrationTest    # @Tag("integration") 테스트만 실행; `test` 이후에 실행됨
./gradlew test --tests 'com.ktb.member.*'          # 특정 패키지
./gradlew test --tests 'com.ktb.member.MemberServiceTest.someMethod'  # 단일 테스트 메서드
```

테스트 태스크 분리에 주의: 기본 `test` 태스크는 `integration` 태그를 **제외**한다(`build.gradle` 참고). 통합 테스트는 `integrationTest`로 실행한다. 테스트는 슬라이스로 격리하지 않는 한 MySQL과 Redis 연결이 필요하다(아래 프로파일 참고).

## 로컬 개발 실행 의존성

앱 실행에는 **MySQL**과 **Redis**가 필수다 — 없으면 기동 실패한다.
- `local` 프로파일: MySQL `localhost:3307` (db `ktb`, root/1234), Redis `localhost:6379`
- `test` 프로파일: MySQL `localhost:3306` (db `temp_database`)
- 세션을 Redis에 저장하므로(Spring Session), 인증만 쓰더라도 Redis는 필수다.

## 설정 프로파일

프로파일은 `spring.profiles.active`로 선택한다(`application.yml` 기본값은 `local`, Docker 이미지는 `--spring.profiles.active=prod` 전달).

- **`local`** — MySQL `localhost:3307`, `ddl-auto: create`, SQL 로깅 ON, `application-monitoring.yml` import
- **`test`** — MySQL `localhost:3306`, `ddl-auto: create-drop`
- **`prod`** — 모든 값을 환경변수로 주입(`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `S3_BUCKET`, `REDIS_*`), `ddl-auto` 기본값 `validate`. `forward-headers-strategy: framework` 활성화(nginx/ALB 뒤에서 동작)
- **`monitoring`** — actuator `health`, `info`, `prometheus` 엔드포인트 노출

프로파일은 빈 선택도 바꾼다: `S3FileStorage`는 `@Profile("prod")`, `LocalFileStorage`는 `@Profile({"local","test"})`이며 둘 다 `FileStorage`를 구현한다. 이 중 어느 것도 활성화되지 않은 새 프로파일을 추가하면 `FileStorage` 빈이 없게 되니 주의.

## 아키텍처

### 패키지 구조
계층이 아닌 **기능/도메인 단위**로 구성된다. 각 도메인 패키지(`member`, `post`, `comment`, `postlike`, `file`, `auth`, `postImage`, `profileImage`)는 자체 `controller`/`service`/`repository`/`domain`/`dto`를 가진다. 횡단 관심사(인프라)는 `com.ktb.global` 아래에 위치한다(`auth`, `redis`, `s3`, `web`, `querydsl`, `utils/{response,entity,exception}`).

### 계층 규칙
- **Controller** (`@RestController`) — `ResponseEntity<ApiResponse<T>>`를 반환한다. HTTP 상태 코드는 명시적으로 설정하고, 본문은 `ApiResponse.success(message, data)`로 감싼다. 인증은 세션의 `@SessionAttribute("loginMember")`(회원 id인 `Long`)로 읽는다 — **Spring Security 필터 체인은 없다**. `SecurityConfig`는 `BCryptPasswordEncoder` 빈만 제공한다.
- **Service** (`@Service`) — 비즈니스 규칙을 담고, 규칙 위반 시 `BusinessException(ErrorCode.XXX)`를 던진다. 소유권 검증(세션의 `currentMemberId`와 리소스 소유자 비교)은 서비스 계층에서 수행한다.
- **Repository** — Spring Data JPA 인터페이스. 동적/프로젝션 쿼리는 QueryDSL 커스텀 리포지토리 패턴을 따른다: `XxxRepository extends JpaRepository<…>, XxxRepositoryCustom`, `XxxRepositoryImpl`이 커스텀 인터페이스를 주입받은 `JPAQueryFactory`(`global/querydsl/QueryDslConfig`의 빈)로 구현. QueryDSL `Q`-타입은 애노테이션 프로세서가 `build/generated`에 생성한다.

### DTO 규칙
요청/응답 DTO는 도메인별 컨테이너 클래스 내부의 **중첩 static 클래스**로 묶는다. 예: `MemberRequest.SignUpRequest`, `MemberResponse.ProfileResponse`, `PostResponse.DetailPostResponse`. 새 엔드포인트 추가 시 이 규칙을 따른다.

### 응답 & 에러 규약
- 성공/실패 모두 `ApiResponse<T>` 레코드 `(boolean success, String code, String message, T data)`를 반환한다.
- `GlobalExceptionHandler`(`@RestControllerAdvice`)가 예외를 이 형식으로 매핑한다. **모든 도메인 에러는 `ErrorCode`를 거친다**(`HttpStatus`, 숫자 코드, 메시지를 담은 단일 enum) — `BusinessException`으로 던진다. 새 에러는 임의 응답을 만들지 말고 `ErrorCode` enum에 케이스를 추가한다. 표준 프레임워크 예외(검증 실패, 본문 파싱 실패, 미디어 타입, 업로드 크기 초과)는 이미 전용 핸들러가 있다.

### 엔티티 & Auditing
엔티티는 `global/utils/entity` 계층을 상속한다: `CreatedEntity`(`@CreatedDate createdAt`) → `BaseEntity`(`@LastModifiedDate updatedAt` 추가) → `SoftDeleteEntity`(`deletedAt` + `softDelete()`/`isDeleted()` 추가). JPA Auditing은 `KtbApplication`의 `@EnableJpaAuditing`으로 활성화된다. **삭제는 소프트 삭제**다 — `deletedAt`을 세팅하고 조회 시 항상 `deletedAt IS NULL`로 필터링한다(예: `findByIdAndDeletedAtIsNull`, QueryDSL의 `deletedAt.isNull()` 조건).

### 조회수/카운터 패턴 (Redis)
쓰기가 잦은 카운터는 주 DB 경로에서 분리한다:
- `PostViewCountService`가 Redis 키 `view:post:{id}`를 증가시키고, 회원별 중복 방지 키 `view:dedup:post:{id}:member:{memberId}`(TTL 30분)를 사용한다.
- `ViewCountScheduler`(`@Scheduled(fixedDelay=30000)`, `@EnableScheduling`으로 활성화)가 `view:post:*`를 SCAN → 각 키를 `getAndDelete`로 비운 뒤 → `PostRepository.incrementViewCount(id, delta)`로 MySQL에 델타를 반영한다.
- 좋아요/댓글 수는 엔티티 dirty checking이 아니라 원자적 JPQL `@Modifying UPDATE ... SET count = count ± 1`(`PostRepository`)로 갱신해 lost update를 막는다. 새 카운터도 엔티티 read-modify-write 대신 이 관용구를 재사용한다.

### 파일 저장
`FileController` → `FileStorage` 인터페이스 + 프로파일로 선택되는 구현체. `S3FileStorage`(prod)는 설정된 버킷의 `images/{dir}/{uuid.ext}`에 업로드하고 공개 S3 URL을 반환한다. `LocalFileStorage`는 `file.upload-path`(`uploads/`) 아래에 저장하며 `WebConfig`의 `/uploads/**` 리소스 핸들러로 다시 서빙된다. 멀티파트 제한: 파일당 4MB, 요청당 10MB(`application.yml`).

### 세션 & CORS
`WebConfig`가 세션 쿠키(`SESSION`, HttpOnly, Secure, `SameSite=None`)와 CORS(허용 오리진 `www.shiftknu.com`, `api.shiftknu.com`, `localhost:3000`, credentials 허용)를 설정한다. Redis 기반 HTTP 세션은 네임스페이스 `ktb:session`을 쓴다(`RedisConfiguration`, `@EnableRedisHttpSession`). 타임아웃 30분.

## 배포 / CI-CD (GitOps)

- **Dockerfile** — 멀티 스테이지(temurin 21 jdk → jre), `bootJar -x test`로 빌드, `prod` 프로파일로 실행.
- **`.github/workflows/`** — `main` push 시: 이미지를 ECR에 빌드/푸시(`stockpedia-backend:<sha>`), 이후 `kube/argocd/umbrella-helm/values.yaml`의 이미지 태그를 갱신하고 `[ci skip]`으로 커밋. Argo CD가 해당 경로를 감시해 배포한다. PR/merge CI 워크플로는 `dev` 브랜치를 대상으로 한다.
- **`kube/argocd/`** — Argo CD `Application`들과 Helm 차트(엄브렐라 차트 + `backend-helm` 서브차트, 그리고 monitoring/portainer/cluster 차트). 런타임 k8s 설정 변경은 앱이 아니라 이곳의 Helm `values.yaml`/템플릿을 수정하는 것이다.
- **`deploy.sh`** — EC2(`52.78.129.16`)로 직접 scp하는 레거시 경로. ECR + Argo CD 플로로 대체되었지만 저장소에 남아있다.
- `src/main/resources/jdbc/`의 `.bak` 파일들은 은퇴한 수동 JDBC 리포지토리 계층으로, JPA/QueryDSL로 대체되었다 — 활성 코드로 취급하지 말 것.

## 참고 사항

- 상당수 코드에 Spring/JPA 근거를 설명하는 한국어 Javadoc이 광범위하게 달려 있다(학습 프로젝트 관례). 기존 파일 수정 시 이 스타일을 유지한다.
- 개발 환경에서 SQL 로깅용 p6spy가 활성화되어 있다.
