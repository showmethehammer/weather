package zerobase.weather.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@Transactional // test할때 Database를 생성하고, 끝나면 삭제
@SpringBootTest
class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test// 테스트코드 작성시 셋팅값 -> 조건 -> 예상값
    void insertMemoTest(){
        // given
        Memo newMemo = new Memo(2,"hihihi");
        jdbcMemoRepository.save(newMemo);
        // when
        Optional<Memo> byId = jdbcMemoRepository.findById(newMemo.getId());
        Memo reMemo = byId.get();
        // then
        assertEquals(newMemo.getId(),reMemo.getId());
        assertEquals(newMemo.getText(),newMemo.getText());
    }
    @Test // 테스트코드 작성시 셋팅값 -> 조건 -> 예상값
    void findAllMemoTest(){
        // given
        Memo newMemo = new Memo(1,"hihihi");
        jdbcMemoRepository.save(newMemo);
        Memo newMemo1 = new Memo(2,"aaa");
        jdbcMemoRepository.save(newMemo1);
        Memo newMemo2 = new Memo(3,"bbb");
        jdbcMemoRepository.save(newMemo2);
        // when
        List<Memo> list = jdbcMemoRepository.findAll();
        // then
        assertEquals(list.get(0).getId(), newMemo.getId());
        assertEquals(list.get(1).getId(), newMemo1.getId());
        assertEquals(list.get(2).getId(), newMemo2.getId());
        assertEquals(list.get(0).getText(), newMemo.getText());
        assertEquals(list.get(1).getText(), newMemo1.getText());
        assertEquals(list.get(2).getText(), newMemo2.getText());
    }

}