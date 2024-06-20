package io.hhplus.tdd.database;


import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 해당 Table 클래스는 변경하지 않고 공개된 API 만을 사용해 데이터를 제어합니다.
 */
@Component
public class PointHistoryTable {
    private final List<PointHistory> table = new ArrayList<>();
    private long cursor = 1;
    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    public PointHistory insert(long userId, long amount, TransactionType type, long updateMillis) {
        throttle(300L);
        PointHistory pointHistory = new PointHistory(cursor++, userId, amount, type, updateMillis);
        table.add(pointHistory);
        return pointHistory;
    }

    public List<PointHistory> selectAllByUserId(long userId) {
        List<PointHistory> PointHistoryList  = Collections.emptyList();
        PointHistoryList = table.stream().filter(pointHistory -> pointHistory.userId() == userId).toList();
        log.info("PointHistoryList.size() : {}", PointHistoryList.size());
        return PointHistoryList;
    }

    private void throttle(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.random() * millis));
        } catch (InterruptedException ignored) {

        }
    }
}
