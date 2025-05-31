package id.ac.ukdw.www.rplbo.homepage.util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final String SESSION_FILE = "session.ser";
    private static Map<String, Object> session = new HashMap<>();

    private SessionManager() {}

    public static void set(String key, Object value) {
        session.put(key, value);
        saveSession();
    }

    public static Object get(String key) {
        return session.get(key);
    }

    public static void remove(String key) {
        session.remove(key);
        saveSession();
    }

    public static void clear() {
        session.clear();
        saveSession();
    }

    public static boolean isLoggedIn() {
        return session.containsKey("user");
    }

    public static void saveSession() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SESSION_FILE))) {
            out.writeObject(session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadSession() {
        File file = new File(SESSION_FILE);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                session = (Map<String, Object>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
