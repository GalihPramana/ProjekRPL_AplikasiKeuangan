package UtangPiutang;

public class UtangPiutang {
    private String nama;
    private String kepada;
    private String tanggal;
    private int jumlahUtang;
    private int sudahDibayar;
    private int sisaUtang;
    private String status;

    public UtangPiutang(String nama, String kepada, String tanggal, int jumlahUtang, int sudahDibayar, int sisaUtang, String status) {
        this.nama = nama;
        this.kepada = kepada;
        this.tanggal = tanggal;
        this.jumlahUtang = jumlahUtang;
        this.sudahDibayar = sudahDibayar;
        this.sisaUtang = sisaUtang;
        this.status = status;
    }

    public String getNama() { return nama; }
    public String getKepada() { return kepada; }
    public String getTanggal() { return tanggal; }
    public int getJumlahUtang() { return jumlahUtang; }
    public int getSudahDibayar() { return sudahDibayar; }
    public int getSisaUtang() { return sisaUtang; }
    public String getStatus() { return status; }
}
