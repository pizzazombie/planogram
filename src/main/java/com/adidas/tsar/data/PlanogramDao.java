package com.adidas.tsar.data;

import com.adidas.tsar.domain.Planogram;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PlanogramDao extends BaseDao {

    private static final String COUNT_FROM_PLANOGRAM_SQL = "select count(1) as rowCount from planogram";
    private static final String MAX_PLANOGRAM_ID_SQL = "select max(id) as maxId from planogram";
    private static final String TRUNCATE_TABLE_PLANOGRAM_SQL = "TRUNCATE TABLE planogram";
    public static final String SELECT_DISTINCT_STORE_CODE_FROM_PLANOGRAM = "select distinct storeCode from planogram";
    private final SessionFactory sessionFactory;

    public void truncatePlanogram() {
        execute(sessionFactory, TRUNCATE_TABLE_PLANOGRAM_SQL);
    }

    public void saveAll(List<Planogram> planograms) {
        try (var session = sessionFactory.openSession()) {
            session.doWork(conn -> {
                try {
                    final var maxId = findMaxId();
                    try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO planogram (id, articleCode, storeCode, sizeIndex, gtin, priority, presMin, salesFloorQty, finalSalesFloorQty, ignoreForReverseReplenishment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        int i = 1;
                        for (var result : planograms) {
                            stmt.setLong(1, maxId + i);
                            stmt.setString(2, result.getArticleCode());
                            stmt.setString(3, result.getStoreCode());
                            stmt.setString(4, result.getSizeIndex());
                            stmt.setString(5, result.getGtin());
                            stmt.setInt(6, result.getPriority());
                            stmt.setInt(7, result.getPresMin());
                            stmt.setInt(8, result.getSalesFloorQty());
                            stmt.setInt(9, result.getFinalSalesFloorQty());
                            stmt.setBoolean(10, result.isIgnoreForReverseReplenishment());
                            stmt.addBatch();

                            if (i % 1500 == 0) stmt.executeBatch();
                            ++i;
                        }
                        if ((i - 1) % 1500 != 0) stmt.executeBatch();
                        if ((i - 1) % 1500 != 0) stmt.executeBatch();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public Set<String> findAllStoreCodes() {
        return executeAndReturn(sessionFactory, SELECT_DISTINCT_STORE_CODE_FROM_PLANOGRAM, resultSet -> {
            final var result = new HashSet<String>((int) count());
            while (resultSet.next()) {
                result.add(resultSet.getString("storeCode"));
            }
            return result;
        });
    }

    public long findMaxId() {
        return executeAndReturn(sessionFactory, MAX_PLANOGRAM_ID_SQL, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getLong("maxId");
            } else {
                return 0L;
            }
        });
    }

    public long count() {
        return executeAndReturn(sessionFactory, COUNT_FROM_PLANOGRAM_SQL, resultSet -> {
            if (resultSet.next()) {
                return resultSet.getLong("rowCount");
            } else {
                return 0L;
            }
        });
    }

}
