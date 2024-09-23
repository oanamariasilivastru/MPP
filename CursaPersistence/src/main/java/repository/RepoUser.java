package repository;
import jdk.jfr.Percentage;
import model.Entity;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RepoUser implements UserRepository {
    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger(RepoUser.class);

    public RepoUser(Properties props) {
        logger.info("Initializing RepoUser with properties: {}", props);
        jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public User save(User user) {
        logger.traceEntry("saving user {}", user);
        Connection con = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("INSERT INTO \"user\" (username, password) VALUES (?, ?)")) {
            preStmt.setString(1, user.getUsername());
            preStmt.setString(2, user.getPassword());

            int result = preStmt.executeUpdate();
            logger.trace("Saved {} rows", result);
            return user;
        } catch (SQLException ex) {
            logger.error("Error saving user {}", user, ex);
            System.out.println("Error DB " + ex);

        }
        logger.traceExit();
        return null;
    }


    public User findOne(Long idUser) {
        logger.traceEntry("finding user with id {}", idUser);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"user\" WHERE \"idUser\" = ?")) {
            preparedStatement.setLong(1, idUser);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("idUser");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    // Construim obiectul User
                    User user = new User(username, password);
                    user.setId(id);
                    return user;
                }
            }
        } catch (SQLException ex) {
            logger.error("Error finding user with id {}", idUser, ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No user found with id {}", idUser);
        return null;
    }


    public Iterable<User> findAll() {
        logger.traceEntry("find all users");
        Connection connection = jdbcUtils.getConnection();
        List<User> userList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"user\"")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("idUser");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");

                    // Construim obiectul User
                    User user = new User(username, password);
                    user.setId(id);
                    userList.add(user);
                }
            }
        } catch (SQLException e) {
            logger.error("Error la DB -- User ", e);
            System.out.println("Error la DB -- User " + e.getMessage());
        }
        logger.traceExit(userList);
        return userList;
    }


    @Override
    public void delete(Long idUser) {
        logger.traceEntry("delete user with id {}", idUser);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM \"user\" WHERE \"idUser\" = ?")) {
            preparedStatement.setLong(1, idUser);
            int result = preparedStatement.executeUpdate();
            logger.trace("user deleted {}", result);
        } catch (SQLException e) {
            logger.error("Error la DB -- User ", e);
            System.out.println("Error la DB -- User " + e.getMessage());
        }
        logger.traceExit();
    }

    @Override
    public void update(User user) {
        logger.traceEntry("update user {}", user);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE \"user\" SET username = ?, password = ? WHERE \"idUser\" = ?")) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setLong(3, user.getId());

            int result = preparedStatement.executeUpdate();
            logger.trace("user updated {}", result);
        } catch (SQLException e) {
            logger.error("Error updating user {}", user, e);
            System.out.println("Error la DB -- User " + e.getMessage());
        }
        logger.traceExit();
    }
    @Override
    public User findByUsernamePassword(String username, String password) {
        logger.traceEntry("finding user by username and password: {}", username);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM \"user\" WHERE username = ? AND password = ?")) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("idUser");
                    User user = new User(username, password);
                    user.setId(id);
                    logger.traceExit("found user: {}", user);
                    System.out.println(user.getId());
                    System.out.println(user);
                    return user;
                }
            }
        } catch (SQLException ex) {
            logger.error("Error finding user by username and password: {}", username, ex);
            System.out.println("Error la DB -- User " + ex.getMessage());
        }
        logger.traceExit("No user found with username and password: {}", username);
        return null;
    }

}
