package repository;
import model.Cursa;
import model.Rezervare;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RepoRezervare implements RezervareRepository {
    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger(RepoRezervare.class);

    public RepoRezervare(Properties props) {
        logger.info("Initializing RepoRezervare with properties: {}", props);
        jdbcUtils = new JdbcUtils(props);
    }

    @Override
    public void save(Rezervare entity) {
        logger.traceEntry("saving rezervare {}", entity);
        Connection con = jdbcUtils.getConnection();
        try (PreparedStatement preStmt = con.prepareStatement("INSERT INTO rezervare (\"idCursa\", \"numeClient\", \"nrLocuri\") VALUES (?, ?, ?)"))  {
            preStmt.setLong(1, entity.getCursa().getId());
            preStmt.setString(2, entity.getNumeClient());
            preStmt.setLong(3, entity.getNrLocuri());

            int result = preStmt.executeUpdate();
            logger.trace("Saved {} rows", result);
        } catch (SQLException ex) {
            logger.error("Error saving rezervare {}", entity, ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public Rezervare findOne(Long idRezervare) {
        logger.traceEntry("finding rezervare with id {}", idRezervare);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rezervare WHERE \"idRezervare\" = ?")) {
            preparedStatement.setLong(1, idRezervare);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Long idCursa = resultSet.getLong("idCursa");
                    String numeClient = resultSet.getString("numeClient");
                    Integer nrLocuri = resultSet.getInt("nrLocuri");

                    Cursa cursa = null;
                    // Construim obiectul Rezervare
                    Rezervare rezervare = new Rezervare(cursa, numeClient, nrLocuri);
                    rezervare.setId(idRezervare);
                    return rezervare;
                }
            }
        } catch (SQLException ex) {
            logger.error("Error finding rezervare with id {}", idRezervare, ex);
            System.out.println("Error DB " + ex);
        }
        logger.traceExit("No rezervare found with id {}", idRezervare);
        return null;
    }

    @Override
    public Iterable<Rezervare> findAll() {
        logger.traceEntry("find all rezervari");
        Connection connection = jdbcUtils.getConnection();
        List<Rezervare> rezervareList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rezervare")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long idRezervare = resultSet.getLong("idRezervare");
                    Long idCursa = resultSet.getLong("idCursa");
                    String numeClient = resultSet.getString("numeClient");
                    Integer nrLocuri = resultSet.getInt("nrLocuri");

                    Cursa cursa = null;
                    // Construim obiectul Rezervare
                    Rezervare rezervare = new Rezervare(cursa, numeClient, nrLocuri);
                    rezervare.setId(idRezervare);
                    rezervareList.add(rezervare);
                }
            }
        } catch (SQLException e) {
            logger.error("Error la DB: Rezervare ", e);
            System.out.println("Error la DB: Rezervare " + e.getMessage());
        }
        logger.traceExit(rezervareList);
        return rezervareList;
    }

    @Override
    public void delete(Long idRezervare) {
        logger.traceEntry("delete rezervare with id {}", idRezervare);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM rezervare WHERE \"idRezervare\" = ?")) {
            preparedStatement.setLong(1, idRezervare);
            int result = preparedStatement.executeUpdate();
            logger.trace("rezervare deleted {}", result);
        } catch (SQLException e) {
            logger.error("Error la DB: Rezervare ", e);
            System.out.println("Error la DB: Rezervare " + e.getMessage());
        }
        logger.traceExit();
    }

    @Override
    public void update(Rezervare rezervare) {
        logger.traceEntry("update rezervare {}", rezervare);
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE rezervare SET \"idCursa\" = ?, \"numeClient\" = ?, \"nrLocuri\" = ? WHERE \"idRezervare\" = ?")) {
            preparedStatement.setLong(1, rezervare.getCursa().getId());
            preparedStatement.setString(2, rezervare.getNumeClient());
            preparedStatement.setLong(3, rezervare.getNrLocuri());
            preparedStatement.setLong(4, rezervare.getId());

            int result = preparedStatement.executeUpdate();
            logger.trace("rezervare updated {}", result);
        } catch (SQLException e) {
            logger.error("Error updating rezervare {}", rezervare, e);
            System.out.println("Error la DB -- Rezervare " + e.getMessage());
        }
        logger.traceExit();
    }

    public Iterable<Rezervare> findAllByCursaId(Long idCursa) {
        logger.traceEntry("find all rezervari for cursa with id {}", idCursa);
        Connection connection = jdbcUtils.getConnection();
        List<Rezervare> rezervareList = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rezervare WHERE \"idCursa\" = ?")) {
            preparedStatement.setLong(1, idCursa);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Long idRezervare = resultSet.getLong("idRezervare");
                    String numeClient = resultSet.getString("numeClient");
                    Integer nrLocuri = resultSet.getInt("nrLocuri");
                    Rezervare rezervare = new Rezervare(null, numeClient, nrLocuri);
                    rezervare.setId(idRezervare);
                    rezervareList.add(rezervare);
                }
            }
        } catch (SQLException e) {
            logger.error("Error la DB: Rezervare ", e);
            System.out.println("Error la DB: Rezervare " + e.getMessage());
        }
        logger.traceExit(rezervareList);
        return rezervareList;
    }
}
