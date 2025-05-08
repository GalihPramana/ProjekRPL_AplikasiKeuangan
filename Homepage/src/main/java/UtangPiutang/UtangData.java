package UtangPiutang;

public class UtangData {
    private String nama;
    private double total;
    private double sisa;
    private double dibayar;

    public UtangData(String nama, double total, double sisa, double dibayar) {
        this.nama = nama;
        this.total = total;
        this.sisa = sisa;
        this.dibayar = dibayar;
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getSisa() { return sisa; }
    public void setSisa(double sisa) { this.sisa = sisa; }

    public double getDibayar() { return dibayar; }
    public void setDibayar(double dibayar) { this.dibayar = dibayar; }
}