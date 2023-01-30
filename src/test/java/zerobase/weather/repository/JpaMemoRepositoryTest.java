package zerobase.weather.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;
    @Test// 테스트코드 작성시 셋팅값 -> 조건 -> 예상값
    void insertMemoTest(){
        // given
        Memo memo = new Memo(4,"hgddgh");
        // when
        jpaMemoRepository.save(memo);
        Optional<Memo> byId = jpaMemoRepository.findById(memo.getId());
        Memo memo1 =  byId.get();
        // then
        assertEquals(memo.getId(),memo.getId());
        assertEquals(memo.getText(),memo1.getText());
    }


    @Test// 테스트코드 작성시 셋팅값 -> 조건 -> 예상값
    void insertMemoTest1(){
        // given
        Memo memo = new Memo(1,"hgddgh");
        Memo memo1 = new Memo(2,"heheh");
        Memo memo2 = new Memo(3,"bnmbm");
        Memo memo3 = new Memo(4,"rtyrty");
        // when
        jpaMemoRepository.save(memo);
        jpaMemoRepository.save(memo1);
        jpaMemoRepository.save(memo2);
        jpaMemoRepository.save(memo3);


        /**
         * 원하는 ID에 대한 Data를 활용하고 싶을때
         */
        List<Memo> memos = jpaMemoRepository.findAll();  // 전체를 리스트로 불러옴


        // then
        assertEquals(memos.get(0).getId(),memo.getId());
        assertEquals(memos.get(1).getId(),memo1.getId());
        assertEquals(memos.get(2).getId(),memo2.getId());
        assertEquals(memos.get(3).getId(),memo3.getId());
        assertEquals(memos.get(0).getText(),memo.getText());
        assertEquals(memos.get(1).getText(),memo1.getText());
        assertEquals(memos.get(2).getText(),memo2.getText());
        assertEquals(memos.get(3).getText(),memo3.getText());
    }


}
