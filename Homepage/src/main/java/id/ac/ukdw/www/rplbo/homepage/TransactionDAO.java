package id.ac.ukdw.www.rplbo.homepage;

import id.ac.ukdw.www.rplbo.homepage.Transaction;
import id.ac.ukdw.www.rplbo.homepage.config.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class TransactionDAO {

    private static TransactionDAO instance;

    private TransactionDAO() {}

    public static TransactionDAO getInstance() {
        if (instance == null) {
            instance = new TransactionDAO();
        }
        return instance;
    }

    public ObservableList<Transaction> getAllByUsername(String username) {
        ObservableList<Transaction> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM transaksi WHERE username = ? ORDER BY tanggal ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("transaksi_id");
                String kategori = rs.getString("kategori");
                int nominal = rs.getInt("nominal");
                String tanggal = rs.getString("tanggal");
                String deskripsi = rs.getString("description");

                list.add(new Transaction(String.valueOf(id), kategori, nominal, deskripsi, tanggal));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void insert(Transaction t, String username) {
        String sql = "INSERT INTO transaksi (username, kategori, nominal, tanggal, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, t.getSourceName());
            stmt.setInt(3, t.getJumlah());
            stmt.setString(4, t.getTanggal());
            stmt.setString(5, t.getDescription());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Transaction t) {
        String sql = "UPDATE transaksi SET kategori = ?, nominal = ?, tanggal = ?, description = ? WHERE transaksi_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, t.getSourceName());
            stmt.setInt(2, t.getJumlah());
            stmt.setString(3, t.getTanggal());
            stmt.setString(4, t.getDescription());
            stmt.setInt(5, Integer.parseInt(t.getIdTransaksi()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String idTransaksi) {
        String sql = "DELETE FROM transaksi WHERE transaksi_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, Integer.parseInt(idTransaksi));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
