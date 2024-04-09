package repository;

import model.Cursa;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RepoCursa implements CursaRepository {
    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger(RepoCursa.class);

    public RepoCursa(Properties props) {
        logger.info("Initializing RepoCursa with properties: {}", props);
        jdbcUtils = new JdbcUtils(props);
    }
    @Override
    public void save(Cursa entity) {
        logger.traceEntry("saving cursa {}", entity);
        Connection con = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("INSERT INTO cursa (destinatie, data) VALUES (?, ?)")) {
            preStmt.setString(1, entity.getDestinatie());
            LocalDateTime data = entity.getData();
            preStmt.setTimestamp(2, Timestamp.valueOf(data));

            int result = preStmt.executeUpdate();
            logger.trace("Saved {} rows", result);
        } catch (SQLException ex) {
            logger.error("Error saving cursa {}", entity, ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }
    @Override
    public Cursa findOne(Long idCursa) {
        logger.traceEntry("finding cursa with id {}", idCursa);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cursa WHERE \"idCursa\" = ?")) {
            preparedStatement.setLong(1, idCursa);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("idCursa");
                    String destinatie = resultSet.getString("destinatie");
                    LocalDateTime data = resultSet.getTimestamp("data").toLocalDateTime();

                    // Construim obiectul Cursa
                    Cursa cursa = new Cursa(destinatie, data);
                    cursa.setId(id);
                    return cursa;
                }
            }
        } catch (SQLException ex) {
            logger.error("Error finding cursa with id {}", idCursa, ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No cursa found with id {}", idCursa);
        return null;
    }

    @Override
    public Iterable<Cursa> findAll() {
        logger.traceEntry("find all curse");
        Connection connection = jdbcUtils.getConnection();
        List<Cursa> cursaList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cursa")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long id = resultSet.getLong("idCursa");
                    String destinatie = resultSet.getString("destinatie");
                    LocalDateTime data = resultSet.getTimestamp("data").toLocalDateTime();

                    // Construim obiectul Cursa
                    Cursa cursa = new Cursa(destinatie, data);
                    cursa.setId(id);
                    cursaList.add(cursa);
                }
            }
        } catch (SQLException e) {
            logger.error("Error la DB: Cursa ", e);
            System.out.println("Error la DB: Cursa " + e.getMessage());
        }
        logger.traceExit(cursaList);
        return cursaList;
    }
    @Override
    public void delete(Long idCursa) {
        logger.traceEntry("delete cursa with id {}", idCursa);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM cursa WHERE \"idCursa\" = ?")) {
            preparedStatement.setLong(1, idCursa);
            int result = preparedStatement.executeUpdate();
            logger.trace("cursa deleted {}", result);
        } catch (SQLException e) {
            logger.error("Error la DB: Cursa ", e);
            System.out.println("Error la DB: Cursa " + e.getMessage());
        }
        logger.traceExit();
    }
    @Override
    public void update(Cursa cursa) {
        logger.traceEntry("update cursa {}", cursa);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE cursa SET destinatie = ?, data = ? WHERE \"idCursa\" = ?")) {
            preparedStatement.setString(1, cursa.getDestinatie());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(cursa.getData()));
            preparedStatement.setLong(3, cursa.getId());

            int result = preparedStatement.executeUpdate();
            logger.trace("cursa updated {}", result);
        } catch (SQLException e) {
            logger.error("Error updating cursa {}", cursa, e);
            System.out.println("Error la DB -- Cursa " + e.getMessage());
        }
        logger.traceExit();
    }

    @Override
    public Cursa findByDestData(String destinatie, LocalDateTime time) {
        logger.traceEntry("finding cursa by destination and data: {} {}", destinatie, time);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM cursa WHERE destinatie = ? AND data = ?")) {
            preparedStatement.setString(1, destinatie);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(time));
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long id = resultSet.getLong("idCursa");
                    LocalDateTime data = resultSet.getTimestamp("data").toLocalDateTime();
                    Cursa cursa = new Cursa(destinatie, data);
                    cursa.setId(id);
                    logger.traceExit("found cursa: {}", cursa);
                    return cursa;
                }
            }
        } catch (SQLException ex) {
            logger.error("Error finding cursa by destination and data: {} {}", destinatie, time, ex);
            System.out.println("Error la DB -- Cursa " + ex.getMessage());
        }
        logger.traceExit("No cursa found with destination {}", destinatie);
        return null;
    }

    public int getNumarLocuriRezervateByCursaId(long idCursa) {
        try (Connection connection = jdbcUtils.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT SUM(\"nrLocuri\") FROM rezervare WHERE \"idCursa\" = ?")) {
            preparedStatement.setLong(1, idCursa);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int numarLocuriRezervate = resultSet.getInt(1);
                    return numarLocuriRezervate;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return 0;
    }



}
