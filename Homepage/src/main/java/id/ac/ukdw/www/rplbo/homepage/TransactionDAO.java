//package id.ac.ukdw.www.rplbo.homepage;
//import id.ac.ukdw.www.rplbo.homepage.Transaction;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class TransactionDAO {
//
//    public static List<Transaction> loadAllTransactions() {
//        List<Transaction> transactions = new ArrayList<>();
//        String sql = "SELECT * FROM transaksi";
//
//        try (Connection conn = DBHelper.connect();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                transactions.add(new Transaction(
//                        rs.getString("id"),
//                        rs.getString("kategori"),
//                        rs.getInt("jumlah"),
//                        rs.getString("deskripsi"),
//                        rs.getString("tanggal")
//                ));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return transactions;
//    }
//
//    public static void insertTransaction(Transaction trx) {
//        String sql = "INSERT INTO transaksi(id, kategori, jumlah, deskripsi, tanggal) VALUES(?,?,?,?,?)";
//
//        try (Connection conn = DBHelper.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, trx.getIdTransaksi());
//            pstmt.setString(2, trx.getSourceName());
//            pstmt.setInt(3, trx.getJumlah());
//            pstmt.setString(4, trx.getDescription());
//            pstmt.setString(5, trx.getTanggal());
//            pstmt.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void updateTransaction(Transaction trx) {
//        String sql = "UPDATE transaksi SET kategori = ?, jumlah = ?, deskripsi = ?, tanggal = ? WHERE id = ?";
//
//        try (Connection conn = DBHelper.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, trx.getSourceName());
//            pstmt.setInt(2, trx.getJumlah());
//            pstmt.setString(3, trx.getDescription());
//            pstmt.setString(4, trx.getTanggal());
//            pstmt.setString(5, trx.getIdTransaksi());
//            pstmt.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void deleteTransaction(String id) {
//        String sql = "DELETE FROM transaksi WHERE id = ?";
//
//        try (Connection conn = DBHelper.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, id);
//            pstmt.executeUpdate();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//
