package zerobase.weather.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    // JDBC를 저장하는 저장소
    private final JdbcTemplate jdbcTemplate; // JDBC를 활용한 Database의 Data를 저장하객체를 불러옴


    /**
     *  application.properties 에 넣은 Mysql 설정값을 주입함.
     *  @Autowired를 사용함으로 아마 컨테이너에 등록된 DataSource를 볼러오는것으로 보이며,
     *  application.properties 에 MySQL 정보를 등록함으로서 컨테이너에 MySQL 정보가 등록됐을것으로 보임.
     */
    @Autowired //
    public JdbcMemoRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource); // Data
    }

    /**
     *  객체를 Table에 저장하는 메소드
     *  해당 Database Table에 Insert하는 SQL언어를 직접 입력하고,
     *  jdbcTemplate.update(sql, 관련변수1,2,3,4,5.....) 있는것들 입력하면 맞춰서 저장됨.
     *  젹혀있는 get**에서 **이 key 일것이고, val 은 메소드에서 불러온 값일듯.
     */
    public Memo save(Memo memo){
        String sql = "insert into memo values(?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
    }

    /**
     * 전체불러오기
     * 해당 Database Table에 Data를 Select하는 SQL언어를 직접 입력하고,
     *  query메소드 파라메타에 SQL언어와 memoRowMapper() 메소드를 이용하면 Table목록을 List로 만들어서 반환해줌.
     *  memoRowMapper() 에 Data를 불러오는것이 없어서
     *  sql(Table을 불러오는문법)과 memoRowMapper() 메소드를 조합하여 List로 만들어주는듯.
     */
    public List<Memo> findAll(){
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    /**
     * 특정 객체를 Table에서 검색하여 불러옴.
     * findAll과 sql 언어 입력이 비슷하나, where을 활용해 id값(key)에 맞는 값을 불러옴.
     * stream.findFirst()는 여러개값이 넘어오는것을 방지하기위해 첫번째로 발견한 값을 불러오도록 코딩
     */
    public Optional<Memo> findById(int id) {
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }

    /**
     * Table에서 원하는 객체를 받아오기위한 형식
     * 람다인듯한데 아직 잘모름.
     *  Memo객체에 @AllArgsConstructor 를 어노테이션해야 아래와 같은 형식의 코드가 가능한듯 하다.
     *
     *  RowMapper : JDBC를 통해서 Mysql에서 Data를 가지고오면 Resultset 이라는 형식으로
     *            Data를 가지고오며, 이것을  변환해주는 Class
     */
    private RowMapper<Memo> memoRowMapper(){
        return (rs, rowNum) -> new Memo(rs.getInt("id")
                ,rs.getString("text")
                );
    }
}
