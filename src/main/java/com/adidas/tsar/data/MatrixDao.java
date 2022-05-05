package com.adidas.tsar.data;

import com.adidas.tsar.domain.Matrix;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MatrixDao {

    private final SessionFactory sessionFactory;
    private final MatrixRepository matrixRepository;

    public List<Matrix> findAll() {
        try (var session = sessionFactory.openSession()) {
            return session.doReturningWork(conn -> {
                final var result = new ArrayList<Matrix>((int) matrixRepository.count());
                final var statement = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                statement.setFetchSize(Integer.MIN_VALUE);
                final var resultSet = statement.executeQuery("select id, sap, articleId, sizeIndex, quantity from matrix");
                while (resultSet.next()) {
                    result.add(new Matrix(
                        resultSet.getLong("id"),
                        resultSet.getLong("articleId"),
                        resultSet.getString("SizeIndex"),
                        resultSet.getInt("quantity"),
                        resultSet.getString("sap")
                    ));
                }
                return result;
            });
        }
    }

}
