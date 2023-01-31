package zerobase.weather.service;



import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;
//    private DiaryService(DiaryRepository diaryRepository){
//        this.diaryRepository = diaryRepository;
//    }
    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);
    /**
     * @Value 어노테이션은 application.properties 파일에서 ${변수명} 을 찾아 그 값을 하기 변수에 넣어줌.
     * 아래 상황은 API의 라이센스를 가지고오기위해 설정해준 상황임.
     */
    @Value("${openweathermap.key}")
    private String apiKey;


    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 시간단위로 날씨데이터를 API로 가지고 오는 메소드 구현
     */

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveWeatherDate(){
        this.dateWeatherRepository.save(getWeatherFromApi());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * API관련 메소드
     * getWeatherString : API에서 설정한지역의 날씨 데이터를 가지고오는 메소드
     *                   주소값의 변수입력란에 서울을 상수로 적어놓아서 서을날씨 Data만 가지고옴.
     * parseWeather : API Data가 Json으로 오기때문에 Json을 파싱하는 메소드
     *              API를 배포한 사이트의 메뉴얼을 참조하여 변수를 Key로 놓고 Val을 변수의 값으로 설정함.
     *              파싱한 값을 Diary(Entity객체)에 입력하여 JAP객체인 DiaryRepository를 이용하여 저장.
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text){
//        // open weader map에서 날시 데이터 가지고오기
//        String weatherData = getWeatherString();
//        // 받아온 날씨 json 파싱하기
//        Map<String, Object> parsedWeather = parseWeather(weatherData);
//        // 파싱된 데이터 일기값 DB에 넣기.
        // 날시 데이터 가지고오기 (API 에서 가지고오기 or DB 에서 가지고오기
        logger.info("started to create diary");
        DateWeather dateWeather = getDateWeather(date);
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        nowDiary.setDate(date);
        diaryRepository.save(nowDiary);
        logger.info("end to create diary");
    }

    private DateWeather getDateWeather(LocalDate date){
        List<DateWeather> dateWeathers = dateWeatherRepository.findAllByDate(date);
        if(dateWeathers.size() == 0){
            // 새로운 api에서 날씨 정보를 가져와야 한다.
            // 정책상.. 현재 날씨를 가지고오도록 하거나,, 날없이 일기를 쓰도록
            return getWeatherFromApi();
        }else{
            return dateWeathers.get(0);
        }
    }




    /**
     * DataBase에서 입력한 날짜와 동일한 날짜를 가진 Data객체를 찾아 리스트로 가지고오는 메소드
     * @param date 기준이되는 날짜
     * @return JPA객체(diaryRepository)에 날짜를 입력하여 List로 가지고옴.
     */

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
//        if(date.isAfter(LocalDate.ofYearDay(3080,1))){
//            throw new InvalidDate();
//        }
        return diaryRepository.findAllByDate(date);
    }

    /**
     * DataBase에서 Date값이 입력한 두 날짜범위내에 있는 객체를 찾아 리스트로 가지고오는 메소드
     * @param startDate 시작일
     * @param endDate 끝일
     * @return JPA객체(diaryRepository)에 날짜를 입력하여 List로 가지고옴.
     */
    public List<Diary> readDiarys(LocalDate startDate , LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate,endDate);
    }
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }
    public void deleteDiary(LocalDate date){
        diaryRepository.deleteAllByDate(date);
    }

    private String getWeatherString() {

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            URL url = new URL(apiUrl); // apiUrl 저장
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // connection 객체에서 응답 코드를 받음.
            connection.setRequestMethod("GET"); // 요청해서 응답을 요청해서 받음..
            int responseCode = connection.getResponseCode(); // 요청상태 코드로 정상이면 200이 들어옴. 대부분의 API들이 정상으로 받으면 200으로 받음.
            BufferedReader br;
            if(responseCode == 200){ // 응답코드가 200이면 정상동작 알람.
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }else{ // 응답코드가 200이 아니면 error 알람.
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();

            // connection객체에서 받아온 API에 Data값이 없어질대까지 Data받아서 Response에 넣어주는데, 서울로 설정해서 1번만 받음.
            // 스트링빌더가 스프링보다 속도가 빠르기때문에 사용함.
            while((inputLine = br.readLine()) != null){
                response.append(inputLine);
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private Map<String,Object> parseWeather(String jsonString){
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;
        System.out.println(jsonString);
        // Json 파일 읽기
        try{
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        }catch (ParseException e){
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main",weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }


    private DateWeather getWeatherFromApi(){
        // open weather map 에서 날씨 데이터 가지고오기
        String weatherData = getWeatherString();
        // 날씨 json 파싱
        Map<String, Object> parsedWeather = parseWeather(weatherData);
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parsedWeather.get("main").toString());
        dateWeather.setIcon(parsedWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parsedWeather.get("temp"));
        return dateWeather;
    }


}
