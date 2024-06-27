package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.impl.PointServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

public class PointServiceTest {
    // @Mock : 모의 객체 생성
    // @InjectMocks : 테스트 대상 객체를 생성하고 초기화

    @Mock
    private PointHistoryRepository pointHistoryRepository;
    @Mock
    private UserPointRepository userPointRepository;
    @Mock
    private UserPointTable userPointTable;
    @InjectMocks
    private PointServiceImpl pointService;

    private UserPoint userPoint1;
    private UserPoint userPoint2;

    @BeforeEach
    void setUp() {
        // 초기화
        MockitoAnnotations.openMocks(this);

        Long id1 = 1L;
        Long id2 = 2L;

        Long point1 = 150L;
        Long point2 = 200L;

        userPoint1 = new UserPoint(id1, point1, System.currentTimeMillis());
        userPoint2 = new UserPoint(id2, point2, System.currentTimeMillis());

        // 테이블 초기 데이터 적재
        userPointTable.insertOrUpdate(userPoint1.id(), userPoint1.point());
        userPointTable.insertOrUpdate(userPoint2.id(), userPoint2.point());
    }

    @Test
    @DisplayName("특정_유저_포인트_조회_테스트 : 유저가_없을_경우")
    public void pointTest_유저가_없을_경우 () {
        // Given > 테스트를 위해 준비를 하는 과정
        Long id99 = 99L;
        UserPoint userPoint99 = new UserPoint(id99, 0L, System.currentTimeMillis());

        given(userPointRepository.selectById(userPoint99.id())).willReturn(userPoint99);

        // When > 실제로 액션을 하는 테스트를 실행
        UserPoint result1 = pointService.getPoint(userPoint99.id());

        // Then > 테스트를 검증
        // 없는 유저 조회시 디폴트 포인트 0리턴 확인
        assertNotNull(result1);
        assertEquals(userPoint99.id(), result1.id());
        assertEquals(userPoint99.point(), result1.point());
    }

    @Test
    @DisplayName("특정_유저_포인트_조회_테스트 : 포인트_리턴확인")
    public void pointTest_포인트_리턴확인 () {
        // Given > 테스트를 위해 준비를 하는 과정
        given(userPointRepository.selectById(userPoint1.id())).willReturn(userPoint1);
        given(userPointRepository.selectById(userPoint2.id())).willReturn(userPoint2);

        // When > 실제로 액션을 하는 테스트를 실행
        UserPoint result1 = pointService.getPoint(userPoint1.id());
        UserPoint result2 = pointService.getPoint(userPoint2.id());

        // Then > 테스트를 검증
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(userPoint1.point(), result1.point());
        assertEquals(userPoint2.point(), result2.point());
    }

    @Test
    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회하는 기능 테스트 : 유저가_없을_경우")
    public void historyTest_유저가_없을_경우 () {
        // Given > 테스트를 위해 준비를 하는 과정
        Long id99 = 99L;

        // 빈 리스트 생성
        List<PointHistory> emptyHistoryList = Collections.emptyList();

        given(pointHistoryRepository.selectAllByUserId(id99)).willReturn(emptyHistoryList);

        // When > 실제로 액션을 하는 테스트를 실행
        List<PointHistory> resultHistory = pointService.getPointHistory(id99);

        // Then > 테스트를 검증
        // 없는 유저 조회시 빈 리스트 리턴 확인
        assertNotNull(resultHistory);
        assertEquals(0, resultHistory.size());
    }

    @Test
    @DisplayName("특정 유저의 포인트 충전/이용 내역을 조회하는 기능 테스트 : 유저가_있을_경우")
    public void historyTest_유저가_있을_경우 () {
        // Given > 테스트를 위해 준비를 하는 과정
        List<PointHistory> pointHistoryList = new ArrayList<>(Arrays.asList(
                new PointHistory(1L, userPoint1.id(), 0L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userPoint1.id(), userPoint1.point(), TransactionType.CHARGE, System.currentTimeMillis())
        ));

        given(pointHistoryRepository.selectAllByUserId(userPoint1.id())).willReturn(pointHistoryList);

        // When > 실제로 액션을 하는 테스트를 실행
        List<PointHistory> resultHistory = pointService.getPointHistory(userPoint1.id());

        // Then > 테스트를 검증
        // 리턴된 리스트 확인
        assertNotNull(resultHistory);
        assertEquals(pointHistoryList.size(), resultHistory.size());
    }


    @Test
    @DisplayName("특정_유저_포인트_충전_테스트 : 포인트_충전_확인")
    public void chargeTest_포인트_충전_확인 () {
        // Given > 테스트를 위해 준비를 하는 과정
        Long addPoint1 = 500L;
        Long addPoint2 = 300L;

        UserPoint updatedUserPoint1 = new UserPoint(userPoint1.id(), userPoint1.point() + addPoint1, System.currentTimeMillis());
        UserPoint updatedUserPoint2 = new UserPoint(userPoint2.id(), userPoint2.point() + addPoint2, System.currentTimeMillis());

        given(userPointRepository.selectById(userPoint1.id())).willReturn(userPoint1);
        given(userPointRepository.insertOrUpdate(userPoint1.id(), userPoint1.point() + addPoint1)).willReturn(updatedUserPoint1);

        given(userPointRepository.selectById(userPoint2.id())).willReturn(userPoint2);
        given(userPointRepository.insertOrUpdate(userPoint2.id(), userPoint2.point() + addPoint2)).willReturn(updatedUserPoint2);

        // When > 실제로 액션을 하는 테스트를 실행
        UserPoint result1 = pointService.charge(userPoint1.id(), addPoint1);
        UserPoint result2 = pointService.charge(userPoint2.id(), addPoint2);

        // Then > 테스트를 검증
        assertEquals(userPoint1.id(), result1.id());
        assertEquals(updatedUserPoint1.point(), result1.point());

        assertEquals(userPoint2.id(), result2.id());
        assertEquals(updatedUserPoint2.point(), result2.point());
    }

    @Test
    @DisplayName("특정_유저_포인트_충전_테스트 : 충전_내역_히스토리_추가_확인")
    public void chargeTest_충전_내역_히스토리_추가_확인 () {
        // Given > 테스트를 위해 준비를 하는 과정
        Long addPoint = 500L;
        Long finalPoint = userPoint1.point() + addPoint;

        List<PointHistory> pointHistoryList = new ArrayList<>(Arrays.asList(
                new PointHistory(1L, userPoint1.id(), 0L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userPoint1.id(), userPoint1.point(), TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(3L, userPoint1.id(), finalPoint, TransactionType.CHARGE, System.currentTimeMillis())
        ));

        UserPoint updatedUserPoint = new UserPoint(userPoint1.id(), finalPoint, System.currentTimeMillis());
        PointHistory pointHistory = new PointHistory(3L, userPoint1.id(), finalPoint, TransactionType.CHARGE, System.currentTimeMillis());

        given(userPointRepository.selectById(userPoint1.id())).willReturn(userPoint1);
        given(userPointRepository.insertOrUpdate(userPoint1.id(), finalPoint)).willReturn(updatedUserPoint);
        given(pointHistoryRepository.insert(userPoint1.id(), finalPoint, TransactionType.CHARGE, System.currentTimeMillis())).willReturn(pointHistory);
        given(pointHistoryRepository.selectAllByUserId(userPoint1.id())).willReturn(pointHistoryList);

        // When > 실제로 액션을 하는 테스트를 실행
        pointService.charge(userPoint1.id(), addPoint);
        List<PointHistory> resultHistory = pointService.getPointHistory(userPoint1.id());

        // Then > 테스트를 검증
        // 리턴된 리스트 확인
        assertNotNull(resultHistory);
        assertEquals(pointHistoryList.size(), resultHistory.size());
    }
    
    @Test
    @DisplayName("특정_유저_포인트_충전_테스트 : 충전_포인트_0원보다_작을_경우_예외_발생")
    public void chargeTest_충전_포인트_0원보다_작을_경우_예외_발생 () {
        // Given > 테스트를 위해 준비를 하는 과정
        Long addPoint = 0L;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            pointService.charge(userPoint1.id(), addPoint);
        });
    }

    @Test
    @DisplayName("특정_유저의_포인트를_사용하는_기능_테스트 : 포인트_사용_확인")
    public void useTest_포인트_사용_확인 () {
        // Given > 테스트를 위해 준비를 하는 과정
        Long usePoint1 = 10L;
        Long usePoint2 = 50L;

        UserPoint updatedUserPoint1 = new UserPoint(userPoint1.id(), userPoint1.point() - usePoint1, System.currentTimeMillis());
        UserPoint updatedUserPoint2 = new UserPoint(userPoint2.id(), userPoint2.point() - usePoint2, System.currentTimeMillis());

        given(userPointRepository.selectById(userPoint1.id())).willReturn(userPoint1);
        given(userPointRepository.insertOrUpdate(userPoint1.id(), userPoint1.point() - usePoint1)).willReturn(updatedUserPoint1);

        given(userPointRepository.selectById(userPoint2.id())).willReturn(userPoint2);
        given(userPointRepository.insertOrUpdate(userPoint2.id(), userPoint2.point() - usePoint2)).willReturn(updatedUserPoint2);

        // When > 실제로 액션을 하는 테스트를 실행
        UserPoint result1 = pointService.use(userPoint1.id(), usePoint1);
        UserPoint result2 = pointService.use(userPoint2.id(), usePoint2);

        // Then > 테스트를 검증
        assertEquals(userPoint1.id(), result1.id());
        assertEquals(updatedUserPoint1.point(), result1.point());

        assertEquals(userPoint2.id(), result2.id());
        assertEquals(updatedUserPoint2.point(), result2.point());

    }
    
    @Test
    @DisplayName("특정_유저의_포인트를_사용하는_기능_테스트 : 사용_내역_히스토리_추가_확인")
    public void useTest_사용_내역_히스토리_추가_확인 () {
        // Given > 테스트를 위해 준비를 하는 과정
        Long usePoint = 10L;
        Long finalPoint = userPoint1.point() - usePoint;

        List<PointHistory> pointHistoryList = new ArrayList<>(Arrays.asList(
                new PointHistory(1L, userPoint1.id(), 0L, TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, userPoint1.id(), userPoint1.point(), TransactionType.CHARGE, System.currentTimeMillis()),
                new PointHistory(3L, userPoint1.id(), finalPoint, TransactionType.USE, System.currentTimeMillis())
        ));

        UserPoint updatedUserPoint = new UserPoint(userPoint1.id(), finalPoint, System.currentTimeMillis());
        PointHistory pointHistory = new PointHistory(3L, userPoint1.id(), finalPoint, TransactionType.USE, System.currentTimeMillis());

        given(userPointRepository.selectById(userPoint1.id())).willReturn(userPoint1);
        given(userPointRepository.insertOrUpdate(userPoint1.id(), finalPoint)).willReturn(updatedUserPoint);
        given(pointHistoryRepository.insert(userPoint1.id(), finalPoint, TransactionType.USE, System.currentTimeMillis())).willReturn(pointHistory);
        given(pointHistoryRepository.selectAllByUserId(userPoint1.id())).willReturn(pointHistoryList);

        // When > 실제로 액션을 하는 테스트를 실행
        pointService.use(userPoint1.id(), usePoint);
        List<PointHistory> resultHistory = pointService.getPointHistory(userPoint1.id());

        // Then > 테스트를 검증
        // 리턴된 리스트 확인
        assertNotNull(resultHistory);
        assertEquals(pointHistoryList.size(), resultHistory.size());
    }

    @Test
    @DisplayName("특정_유저의_포인트를_사용하는_기능_테스트 : 사용_포인트_0원보다_작을_경우_예외_발생")
    public void useTest_사용_포인트_0원보다_작을_경우_예외_발생 () {
        // Given > 테스트를 위해 준비를 하는 과정
        Long usePoint = 0L;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            pointService.charge(userPoint1.id(), usePoint);
        });
    }

    @Test
    @DisplayName("특정_유저의_포인트를_사용하는_기능_테스트 : 기존_포인트가_사용_포인트_보다_작을_경우_예외_발생")
    public void useTest_기존_포인트가_사용_포인트_보다_작을_경우_예외_발생 () {
        // Given > 테스트를 위해 준비를 하는 과정
        Long usePoint = 500L;

        given(userPointRepository.selectById(userPoint1.id())).willReturn(userPoint1);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            pointService.use(userPoint1.id(), usePoint);
        });
    }

}