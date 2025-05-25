package id.ac.ukdw.www.rplbo.homepage;

public class UtangPiutang {
    private String nama;
    private String kepadaSiapa;
    private String tanggal;
    private int jumlahUtang;
    private String status;

    public UtangPiutang(String nama, String kepadaSiapa, String tanggal, int jumlahUtang, String status) {
        this.nama = nama;
        this.kepadaSiapa = kepadaSiapa;
        this.tanggal = tanggal;
        this.jumlahUtang = jumlahUtang;
        this.status = status;
    }

    public UtangPiutang(String kepada, String tanggal, int jumlah, String s) {
    }

    public String getNama() {
        return nama;
    }

    public String getKepadaSiapa() {
        return kepadaSiapa;
    }

    public String getTanggal() {
        return tanggal;
    }

    public int getJumlahUtang() {
        return jumlahUtang;
    }

    public String getStatus() {
        return status;
    }
}
