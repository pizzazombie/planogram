package com.adidas.tsar.data;

import com.adidas.tsar.domain.Planogram;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PlanogramDao {

    private final SessionFactory sessionFactory;

    public void truncatePlanogram() {
        try (var session = sessionFactory.openSession()) {
            session.doWork(conn -> {
                try {
                    try (PreparedStatement stmt = conn.prepareStatement("TRUNCATE TABLE planogram")) {
                        stmt.execute();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void saveAll(List<Planogram> planograms) {
        try (var session = sessionFactory.openSession()) {
            session.doWork(conn -> {
                try {
                    try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO planogram (articleCode, storeCode, sizeIndex, priority, presMin, salesFloorQty, finalSalesFloorQty, ignoreForReverseReplenishment) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                        int i = 1;
                        for (var result : planograms) {
                            stmt.setString(1, result.getArticleCode());
                            stmt.setString(2, result.getStoreCode());
                            stmt.setString(3, result.getSizeIndex());
                            stmt.setInt(4, result.getPriority());
                            stmt.setInt(5, result.getPresMin());
                            stmt.setInt(6, result.getSalesFloorQty());
                            stmt.setInt(7, result.getFinalSalesFloorQty());
                            stmt.setInt(8, result.getIgnoreForReverseReplenishment());
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

    public long count() {
        try (var session = sessionFactory.openSession()) {
            return session.doReturningWork(conn -> {
                try {
                    final var stmtCnt = conn.createStatement();
                    final var storeCountRes = stmtCnt.executeQuery("select count(1) as rowCount from planogram");
                    if (storeCountRes.next()) {
                        return storeCountRes.getLong("rowCount");
                    } else {
                        return 0L;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
