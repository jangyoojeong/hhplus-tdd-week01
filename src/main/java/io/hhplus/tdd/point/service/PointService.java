package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.entity.PointHistory;
import io.hhplus.tdd.point.entity.UserPoint;

import java.util.List;

public interface PointService {

    UserPoint getPoint(Long userId);
    List<PointHistory> getPointHistory(Long userId);
    UserPoint charge(Long userId, Long amount);
    UserPoint use(Long userId, Long amount);

}