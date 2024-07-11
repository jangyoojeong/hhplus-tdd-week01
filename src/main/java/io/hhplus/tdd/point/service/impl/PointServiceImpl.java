package io.hhplus.tdd.point.service.impl;

import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import io.hhplus.tdd.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointHistoryRepository pointHistoryRepository;
    private final UserPointRepository userPointRepository;

    // 잠금 관리를 위한 ConcurrentHashMap
    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    /**
     * 특정 유저의 포인트를 조회
     */
    @Override
    public UserPoint getPoint(Long userId) {
        return userPointRepository.selectById(userId);
    }
    /**
     * 특정 유저의 포인트 충전/이용 내역을 조회
     */
    @Override
    public List<PointHistory> getPointHistory(Long userId) {
        return pointHistoryRepository.selectAllByUserId(userId);
    }
    /**
     * 특정 유저의 포인트를 충전
     */
    @Override
    public UserPoint charge(Long userId, Long amount) {

        // 사용자 별로 잠금 생성
        ReentrantLock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock());

        // 잠금 획득
        lock.lock();

        try {

            // validation 1. 충전금액 0원 이상 확인
            if (amount == null || amount <= 0) {
                throw new IllegalArgumentException("포인트 충전은 0원 이상부터 가능합니다.");
            }

            // 기존 포인트 조회
            UserPoint userPoint = userPointRepository.selectById(userId);

            // 포인트 충전
            Long finalPoint = userPoint.point() + amount;
            UserPoint updatedUserPoint = userPointRepository.insertOrUpdate(userId, finalPoint);

            // 히스토리추가
            pointHistoryRepository.insert(userId, finalPoint, TransactionType.CHARGE, System.currentTimeMillis());

            return updatedUserPoint;

        } finally {
            // 잠금 해제
            lock.unlock();
        }
    }
    /**
     * 특정 유저의 포인트를 사용
     */
    @Override
    public UserPoint use(Long userId, Long amount) {

        // 사용자 별로 잠금 생성
        ReentrantLock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock());

        // 잠금 획득
        lock.lock();

        try {
            // validation 1. 사용금액 0원 이상 확인
            if (amount == null || amount <= 0) {
                throw new IllegalArgumentException("포인트 사용은 0원 이상부터 가능합니다.");
            }

            // 기존 포인트 조회
            UserPoint userPoint = userPointRepository.selectById(userId);

            // validation 2. 포인트 잔고 확인
            if (userPoint.point() < amount) {
                throw new IllegalArgumentException("포인트가 부족합니다.");
            }

            // 포인트 사용
            Long finalPoint = userPoint.point() - amount;
            UserPoint updatedUserPoint = userPointRepository.insertOrUpdate(userId, finalPoint);

            // 히스토리추가
            pointHistoryRepository.insert(userId, finalPoint, TransactionType.USE, System.currentTimeMillis());

            return updatedUserPoint;

        } finally {
            // 잠금 해제
            lock.unlock();
        }
    }
}
