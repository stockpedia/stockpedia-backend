package com.ktb.member.repository;

import com.ktb.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Repository: 스프링 빈 등록, 데이터 액세스 계층임을 명시, 예외를 스프링의 DataAccessException 계층으로 변환
 * @RequiredArgsConstructor: final 필드만 모아서 생성자 자동 생성 → DI(생성자 주입) 방식
 * 생성자 주입을 사용하는 이유: 불변성 보장(final), 컴파일 타임에 감지
 */
@Repository
@RequiredArgsConstructor
public class JdbcMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 회원 정보를 member 테이블에 저장
     *
     * @param member
     */
    @Override
    public Member save(Member member) {
        /**
         * id: auto_increment
         * created_at: current_timestamp
         * updated_at: current_timestamp
         * id, created_at, updated_at는 DB가 자동으로 생성
         */
        String sql = "insert into member (email, password, nickname) values (?, ?, ?)";


        /**
         * DB가 자동으로 생성해줄 식별자인 ID를 저장할 객체를 new GeneratedKeyHolder로 생성
         * Statement.RETURN_GENERATED_KEYS
         * jdbcTemplate.update( conn -> {}, keyHolder)
         * 데이터 저장하고 나서 자동으로 생성된 ID가 있으면 keyHolder에 넣어달라
         */
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> {
            PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            psmt.setString(1, member.getEmail());
            psmt.setString(2, member.getPassword());
            psmt.setString(3, member.getNickname());

            return psmt;
        }, keyHolder);

        long id = keyHolder.getKey().longValue();
        return findById(id);
    }

    /**
     * @param id id를 기준으로 회원 조회
     */
    @Override
    public Member findById(Long id) {
        /**
         * 명시적인 Column을 가져오기 위해 '*'는 사용하지 않음
         */
        String sql = "select id, email, password, nickname, created_at, updated_at from member where id = ?";

        /**
         * jdbcTemplate.query(sql, DB 조회 결과를 자바 객체로 매핑, ?에 들어가는 값)
         * new DataClassRowMapper<>(도메인 클래스의 Class 객체(메타 정보))
         * Spring JDBC에서 DB 조회 결과(ResultSet)를 자바 객체로 자동으로 매핑해주는 클래스
         * 즉, DB 형식으로 저장된 rs를 조회해 자바 객체로 매핑
         * DB의 snake_case 자동 지원
         * 커스텀 RowMapper을 구현해도 된다.
         */
        List<Member> result = jdbcTemplate.query(sql, memberRowMapper(), id);

        /**
         * result의 결과
         * 조회 결과가 있으면: Member 객체가 담긴 List
         * id는 PK라서 UNIQUE → 결과는 0개 아니면 1개
         * 조회 결과가 없으면: 빈 리스트 List[]
         * null은 반환하지 않음
         * result가 빈 리스트이면 .get()로 접근했을때 IndexOutOfBoundsException가 발생하기 때문에
         * null을 리턴하게 하고 서비스 레이어에서 null 처리
         */
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * @param email email를 기준으로 회원 조회
     */
    @Override
    public Member findByEmail(String email) {
        String sql = "select id, email, password, nickname, created_at, updated_at from member where email = ?";

        List<Member> result = jdbcTemplate.query(sql, memberRowMapper(), email);

        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * 전체 조회
     * password는 조회 되면 안됨
     */
    @Override
    public List<Member> findAll() {
        String sql = "select id, email, nickname, created_at, updated_at from member";
        return jdbcTemplate.query(sql, memberRowMapper());
    }


    /**
     * @param id id 존재 여부 검사
     */
    @Override
    public boolean      existsById(Long id) {

        String sql = "select count(*) from member where id = ?";

        Integer i = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return i != null && i > 0;
    }

    /**
     * @param email email 중복 여부 검사
     */
    @Override
    public boolean existsByEmail(String email) {
        String sql = "select count(*) from member where email = ?";

        /**
         * count(*)는 항상 0 이상의 값을 반환
         * jdbcTemplate.queryForObject(sql, 결과 객체가 기대하는 타입, email);
         * queryForObject는 결과 row가 없으면 예외를 던질 수 있으므로 방어 코드가 필요
         * 방어적으로 i != null 삽입
         * i > 0만 해도 문제는 없음
         * i > 0이면 중복, 0이면 중복 없음으로 판단
         */
        Integer i = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return i != null && i > 0;
    }

    /**
     * @param nickname nickname 중복 여부 검사
     */
    @Override
    public boolean existsByNickname(String nickname) {
        String sql = "select count(*) from member where nickname = ?";

        Integer i = jdbcTemplate.queryForObject(sql, Integer.class, nickname);
        return i != null && i > 0;
    }


    /**
     * @param id
     * @param // encoded(password)
     *           id를 기준으로 password 업데이트
     */
    @Override
    public void updatePasswordById(Long id, String newPassword) {
        /**
         * 매개변수로 LocalDateTime.now()를 받아도 된다.
         */
        String sql = "update member set password = ?, updated_at = now() where id = ?";

        jdbcTemplate.update(sql, newPassword, id);
    }

    /**
     * @param id id를 기준으로 delete
     */
    @Override
    public void deleteById(Long id) {
        String sql = "delete from member where id = ?";

        jdbcTemplate.update(sql, id);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> new Member(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("nickname"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
    }
}

