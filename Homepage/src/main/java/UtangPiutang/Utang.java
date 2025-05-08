package UtangPiutang;

public class Utang {
    private String nama;
    private double total;
    private double sisa;
    private double dibayar;

    public Utang(String nama, double total, double sisa, double dibayar) {
        this.nama = nama;
        this.total = total;
        this.sisa = sisa;
        this.dibayar = dibayar;
    }

    public String getNama() { return nama; }
    public double getTotal() { return total; }
    public double getSisa() { return sisa; }
    public double getDibayar() { return dibayar; }

    public void setNama(String nama) { this.nama = nama; }
    public void setTotal(double total) { this.total = total; }
    public void setSisa(double sisa) { this.sisa = sisa; }
    public void setDibayar(double dibayar) { this.dibayar = dibayar; }
}
