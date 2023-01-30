package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary,Integer> {

    /**
     * 변수명을 입력할때 나오는 자동완성 기능을 활용해서 이름을 설정하면 JPA에서 알아서 쿼리를 만들어서 값을 가지고옴다고함.
     * 예) findAllByDate(LocalDate date) date(변수이름)의 값이 해당 값과 같은 변수를 가진 객체를 Table에서 가지고 온다.
     */
    /**
     * findBy는 찾기
     * deleteBy 지우기
     */

    /**
     * 해당하는 날짜와 같은 date(변수이름임)를 가진 객체를
     */
    List<Diary> findAllByDate(LocalDate date);

    /**
     * 범위에 있는 Data를 만드는 메소드
     */
    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     *
     *
     *
     */
    Diary getFirstByDate(LocalDate date);

    @Transactional
    void  deleteAllByDate(LocalDate date);
}
