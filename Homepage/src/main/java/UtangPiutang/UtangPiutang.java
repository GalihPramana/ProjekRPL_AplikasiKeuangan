package UtangPiutang;

public class UtangPiutang {
    private String nama;
    private String tanggal;
    private int jumlahUtang;
    private int sisaUtang;
    private int sudahDibayar;
    private boolean lunas;

    public UtangPiutang(String nama, String tanggal, int jumlahUtang, int sisaUtang, int sudahDibayar, boolean lunas) {
        this.nama = nama;
        this.tanggal = tanggal;
        this.jumlahUtang = jumlahUtang;
        this.sisaUtang = sisaUtang;
        this.sudahDibayar = sudahDibayar;
        this.lunas = lunas;
    }

    public String getNama() { return nama; }
    public String getTanggal() { return tanggal; }
    public int getJumlahUtang() { return jumlahUtang; }
    public int getSisaUtang() { return sisaUtang; }
    public int getSudahDibayar() { return sudahDibayar; }
    public boolean isLunas() { return lunas; }
}
