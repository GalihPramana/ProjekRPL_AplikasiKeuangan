package id.ac.ukdw.www.rplbo.homepage;

public class LogTransaksi {
    private int id;
    private String username;
    private String aksi;
    private String kategori;
    private int nominal;
    private String deskripsi;
    private String tanggalTransaksi;
    private String waktuLog;

    public LogTransaksi(int id, String username, String aksi, String kategori, int nominal, String deskripsi, String tanggalTransaksi, String waktuLog) {
        this.id = id;
        this.username = username;
        this.aksi = aksi;
        this.kategori = kategori;
        this.nominal = nominal;
        this.deskripsi = deskripsi;
        this.tanggalTransaksi = tanggalTransaksi;
        this.waktuLog = waktuLog;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getAksi() { return aksi; }
    public String getKategori() { return kategori; }
    public int getNominal() { return nominal; }
    public String getDeskripsi() { return deskripsi; }
    public String getTanggalTransaksi() { return tanggalTransaksi; }
    public String getWaktuLog() { return waktuLog; }
}
