package id.ac.ukdw.www.rplbo.homepage.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
    private static final String DB_URL = "jdbc:sqlite:databaseAplikasiKeuanganRev1.db";
    private static Connection connection = null;

    // Private constructor untuk mencegah instansiasi
    private DBConnection() {
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Memastikan driver tersedia
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);

                // Setelah koneksi terbentuk, aktifkan foreign_keys:
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                    System.out.println("Koneksi berhasil, foreign_keys=ON.");
                }
                System.out.println("Koneksi berhasil, foreign_keys=ON.");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Driver tidak ditemukan.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Koneksi gagal: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Koneksi ditutup.");
            }
        } catch (SQLException e) {
            System.out.println("Gagal menutup koneksi.");
            e.printStackTrace();
        }
    }
}
