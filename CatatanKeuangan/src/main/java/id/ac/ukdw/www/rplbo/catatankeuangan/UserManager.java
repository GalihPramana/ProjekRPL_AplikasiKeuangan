package id.ac.ukdw.www.rplbo.catatankeuangan;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;

public class UserManager {
    private static final String FILE_PATH = "users.txt";  // File tempat menyimpan data pengguna
    private static HashMap<String, String> users = new HashMap<>();

    static {
        loadUsers();  // Memuat data pengguna saat aplikasi dijalankan
    }

    // Membaca data pengguna dari file
    private static void loadUsers() {
        Path path = Paths.get(FILE_PATH);
        if (Files.exists(path)) {
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2) {
                        users.put(parts[0], parts[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Menambahkan pengguna baru ke file dan HashMap
    public static void addUser(String username, String password) {
        users.put(username, password);
        saveUsers();  // Simpan ke file setelah penambahan akun
    }

    // Menyimpan semua pengguna ke file
    private static void saveUsers() {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            for (String username : users.keySet()) {
                writer.write(username + ":" + users.get(username));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Memeriksa apakah pengguna valid
    public static boolean isValidUser(String username, String password) {
        return users.containsKey(username) && users.get(username).equals(password);
    }

    // Memeriksa apakah username sudah terdaftar
    public static boolean userExists(String username) {
        return users.containsKey(username);
    }
}
