package UtangPiutang;

public class UtangPiutang {
    private int jumlahUtang;
    private int sisaUtang;
    private int sudahDibayar;

    public UtangPiutang(int jumlahUtang, int sisaUtang, int sudahDibayar) {
        this.jumlahUtang = jumlahUtang;
        this.sisaUtang = sisaUtang;
        this.sudahDibayar = sudahDibayar;
    }

    public int getJumlahUtang() {
        return jumlahUtang;
    }
    public void setJumlahUtang(int jumlahUtang) {
        this.jumlahUtang = jumlahUtang;
    }

    public int getSisaUtang() {
        return sisaUtang;
    }
    public void setSisaUtang(int sisaUtang) {
        this.sisaUtang = sisaUtang;
    }

    public int getSudahDibayar() {
        return sudahDibayar;
    }
    public void setSudahDibayar(int sudahDibayar) {
        this.sudahDibayar = sudahDibayar;
    }
}
