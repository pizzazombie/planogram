package com.adidas.tsar.data;

import org.hibernate.SessionFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class BaseDao {

    public <T> T executeAndReturn(SessionFactory sessionFactory, String sql, ResultSetFunction<T> mapFunction) {
        try (var session = sessionFactory.openSession()) {
            return session.doReturningWork(conn -> {
                try {
                    final var statement = conn.createStatement();
                    final var resultSet = statement.executeQuery(sql);
                    return mapFunction.apply(resultSet);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void execute(SessionFactory sessionFactory, String sql) {
        try (var session = sessionFactory.openSession()) {
            session.doWork(conn -> {
                try {
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.execute();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @FunctionalInterface
    public interface ResultSetFunction<R> {

        R apply(ResultSet t) throws SQLException;

    }

}
