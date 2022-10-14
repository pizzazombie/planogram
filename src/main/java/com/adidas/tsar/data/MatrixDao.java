package com.adidas.tsar.data;

import com.adidas.tsar.domain.Matrix;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MatrixDao extends BaseDao {

    private final SessionFactory sessionFactory;
    private final MatrixRepository matrixRepository;

    public List<Matrix> findAll() {
        return executeAndReturn(sessionFactory, "select articleId, sizeIndex, quantity, storeId from matrix", resultSet -> {
            final var result = new ArrayList<Matrix>((int) matrixRepository.count());
            while (resultSet.next()) {
                result.add(new Matrix(
                    resultSet.getLong("articleId"),
                    resultSet.getString("SizeIndex"),
                    resultSet.getInt("quantity"),
                    resultSet.getInt("storeId")
                ));
            }
            return result;
        });
    }

    public void saveAll(List<Matrix> matrices) {
        try (var session = sessionFactory.openSession()) {
            session.doWork(conn -> {
                try {
                    try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO matrix (sizeIndex, quantity, articleId, storeId) VALUES ( ?, ?, ?, ?)")) {
                        int i = 1;
                        for (var result : matrices) {
                            stmt.setString(1, result.getSizeIndex());
                            stmt.setInt(2, result.getQuantity());
                            stmt.setLong(3, result.getArticleId());
                            stmt.setInt(4, result.getStoreId());
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

}
