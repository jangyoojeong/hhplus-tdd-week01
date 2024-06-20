package io.hhplus.tdd.point;

import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class PointConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Test
    @DisplayName("동일유저_동시성_테스트_포인트_충전_및_사용")
    public void chargeAndUseTest_동일유저_동시성_테스트_포인트_충전_및_사용 () {

        // Given
        Long userId = 1L;

        // When
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            pointService.charge(userId, 500L);
        });
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            pointService.use(userId, 100L);
        });
        CompletableFuture<Void> task3 = CompletableFuture.runAsync(() -> {
            pointService.charge(userId, 150L);
        });

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2, task3);
        allTasks.join();

        UserPoint result = pointService.getPoint(userId);

        //Then
        assertNotNull(result);
        assertEquals(result.point(), 500 - 100 + 150);
    }

    @Test
    @DisplayName("동일유저_동시성_테스트_포인트_충전_및_사용_포인트_모자를_경우_예외_발생")
    public void chargeAndUseTest_동일유저_동시성_테스트_포인트_충전_및_사용_포인트_모자를_경우_예외_발생 () {

        // Given
        Long userId = 1L;

        // When & Then
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
                pointService.charge(userId, 500L);
        });
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            assertThrows(IllegalArgumentException.class, () -> {
                pointService.use(userId, 5000L);
            });
        });
        CompletableFuture<Void> task3 = CompletableFuture.runAsync(() -> {
                pointService.charge(userId, 150L);
        });

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2, task3);
        allTasks.join();
    }
}