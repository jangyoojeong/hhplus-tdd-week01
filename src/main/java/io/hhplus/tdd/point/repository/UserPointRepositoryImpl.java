package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.entity.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointTable userPointTable;

    @Override
    public UserPoint selectById(Long id) {
        UserPoint userPoint = userPointTable.selectById(id);
       // return userPoint.toDomain();
        return userPoint;
    }

    @Override
    public UserPoint insertOrUpdate(long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }
}