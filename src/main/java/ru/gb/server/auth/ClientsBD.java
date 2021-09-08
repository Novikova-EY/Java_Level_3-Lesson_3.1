package ru.gb.server.auth;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientsBD {
    private String login;
    private String password;
    private String nickname;

    public void run() throws SQLException {
        createTable();
        insert("l1","p1", "n1");
        insert("l2","p2", "n2");
        insert("l3","p3", "n3");
    }

    private void createTable() throws SQLException {
        try {
            AuthenticationService.getStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS clients (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "login TEXT UNIQUE, " +
                            "password TEXT NOT NULL, " +
                            "nickname TEXT NOT NULL" +
                            ");");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insert(String login, String password, String nickname) throws SQLException {
        try (final PreparedStatement preparedStatement = AuthenticationService.getConnection().prepareStatement(
                "INSERT OR IGNORE INTO clients (login, password, nickname) " +
                        "VALUES (?, ?, ?);")) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, nickname);
            preparedStatement.executeUpdate();
        }
    }

    public static String getNickByLoginPass(String login, String password) throws SQLException {
        try (final PreparedStatement preparedStatement =
                     AuthenticationService.getConnection().prepareStatement(
                             "SELECT nickname "+
                                     "FROM clients " +
                                     "WHERE login = ? AND password = ?;")) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        }
    }

    public static String getLoginByNick(String nickname) throws SQLException {
        try (final PreparedStatement preparedStatement =
                     AuthenticationService.getConnection().prepareStatement(
                             "SELECT login "+
                                     "FROM clients " +
                                     "WHERE nickname = ?;")) {
            preparedStatement.setString(1, nickname);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        }
    }

    public static String getLoginByNickname(String nickname) throws SQLException {
        try (final PreparedStatement preparedStatement =
                     AuthenticationService.getConnection().prepareStatement(
                             "SELECT login " +
                                     "FROM clients " +
                                     "WHERE nickname = ?;")) {
            preparedStatement.setString(1, nickname);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                return resultSet.getString(1);
            }
            return null;
        }
    }

    public static void changeNickname(String nickname, String login) throws SQLException {
        try (final PreparedStatement preparedStatement =
                     AuthenticationService.getConnection().prepareStatement(
                             "UPDATE clients " +
                                     "SET nickname = ? " +
                                     "WHERE login = ?;")) {
            preparedStatement.setString(1, nickname);
            preparedStatement.setString(2, login);
            preparedStatement.executeUpdate();
        }
    }

    private void dropTable() throws SQLException {
        try {
            AuthenticationService.getStatement().executeUpdate("DROP TABLE IF EXISTS clients");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void printDB() throws SQLException {
        try (final PreparedStatement preparedStatement =
                     AuthenticationService.getConnection().prepareStatement("SELECT * FROM clients;")) {
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.printf("id - %d | login - %s | password - %s | nickname - %s\n",
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4));
            }
        }
    }
}
