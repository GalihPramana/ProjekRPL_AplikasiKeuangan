package id.ac.ukdw.www.rplbo.homepage.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");  // WAJIB agar JDBC bisa digunakan
            String url = "jdbc:sqlite:databaseAplikasiKeuanganRev1.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Koneksi berhasil.");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver tidak ditemukan.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
        }
        return conn;
    }
}
