package io.hhplus.tdd.point;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointController.class)
public class PointControllerTest {
    // @Autowired : 스프링이 관리하는 빈(Bean)을 주입.
    // MockMvc : 모의 http request, response 만들어 테스트 진행
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PointService pointService;

    @Test
    @DisplayName("특정_유저의_포인트를_조회하는_기능_테스트")
    public void pointTest_특정_유저의_포인트를_조회하는_기능_테스트 () throws Exception{

        // Given > 테스트를 위해 준비를 하는 과정
        Long id = 1L;
        UserPoint userPoint = new UserPoint(id, 0L, 0L);
        given(pointService.getPoint(id)).willReturn(userPoint);

        // When > 실제로 액션을 하는 테스트를 실행
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/point/{id}", id));

        // Then > 테스트를 검증
        MvcResult mvcResult = resultActions
                .andExpect(status().isOk())                      // 상태 코드 200인 성공적인 응답을 기대
                .andExpect(jsonPath("$.id").value(id)) // JSON 속성 "property"의 값이 "expected"와 같을 것으로 기대
                .andDo(print())                                  // 응답값 print
                .andReturn();                                    // mockMvc의 결과값 리턴


    }

    @Test
    @DisplayName("특정_유저의_포인트_충전_이용_내역을_조회하는_기능_테스트")
    public void historyTest_특정_유저의_포인트_충전_이용_내역을_조회하는_기능_테스트 () throws Exception{

        // Given > 테스트를 위해 준비를 하는 과정
        Long id = 1L;
        List<PointHistory> pointHistoryList = Collections.emptyList();
        given(pointService.getPointHistory(id)).willReturn(pointHistoryList);

        // When > 실제로 액션을 하는 테스트를 실행
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/point/{id}/histories", id));

        // Then > 테스트를 검증
        MvcResult mvcResult = resultActions
                .andExpect(status().isOk())                      // 상태 코드 200인 성공적인 응답을 기대
                .andDo(print())                                  // 응답값 print
                .andReturn();                                    // mockMvc의 결과값 리턴
    }

    @Test
    @DisplayName("특정_유저의_포인트를_충전하는_기능_테스트")
    public void chargeTest_특정_유저의_포인트를_충전하는_기능_테스트 () throws Exception{

        // Given > 테스트를 위해 준비를 하는 과정
        Long id = 1L;
        Long amount = 100L;
        UserPoint userPoint = new UserPoint(id, amount, 0L);
        given(pointService.charge(id, amount)).willReturn(userPoint);

        // When > 실제로 액션을 하는 테스트를 실행
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount)));

        // Then > 테스트를 검증
        MvcResult mvcResult = resultActions
                .andExpect(status().isOk())                      // 상태 코드 200인 성공적인 응답을 기대
                .andExpect(jsonPath("$.id").value(id)) // JSON 속성 "property"의 값이 "expected"와 같을 것으로 기대
                .andDo(print())                                  // 응답값 print
                .andReturn();                                    // mockMvc의 결과값 리턴
    }

    @Test
    @DisplayName("특정_유저의_포인트를_사용하는_기능_테스트")
    public void useTest_특정_유저의_포인트를_사용하는_기능_테스트 () throws Exception{
        // Given > 테스트를 위해 준비를 하는 과정
        Long id = 1L;
        Long amount = 100L;
        UserPoint userPoint = new UserPoint(id, amount, 0L);
        given(pointService.use(id, amount)).willReturn(userPoint);

        // When > 실제로 액션을 하는 테스트를 실행
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/point/{id}/use", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount)));

        // Then > 테스트를 검증
        MvcResult mvcResult = resultActions
                .andExpect(status().isOk())                      // 상태 코드 200인 성공적인 응답을 기대
                .andExpect(jsonPath("$.id").value(id)) // JSON 속성 "property"의 값이 "expected"와 같을 것으로 기대
                .andDo(print())                                  // 응답값 print
                .andReturn();                                    // mockMvc의 결과값 리턴
    }

}