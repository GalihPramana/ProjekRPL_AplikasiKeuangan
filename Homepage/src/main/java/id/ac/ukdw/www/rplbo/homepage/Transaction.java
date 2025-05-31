// Transaction.java
package id.ac.ukdw.www.rplbo.homepage;

import javafx.beans.property.*;

public class Transaction {
    private final IntegerProperty idTransaksi;
    private final StringProperty sourceName;
    private final IntegerProperty jumlah;
    private final StringProperty tanggal;

    public Transaction(int idTransaksi, String sourceName, int jumlah, String tanggal) {
        this.idTransaksi = new SimpleIntegerProperty(idTransaksi);
        this.sourceName = new SimpleStringProperty(sourceName);
        this.jumlah = new SimpleIntegerProperty(jumlah);
        this.tanggal = new SimpleStringProperty(tanggal);
    }

    public int getIdTransaksi() {
        return idTransaksi.get();
    }

    public void setIdTransaksi(int id) {
        this.idTransaksi.set(id);
    }

    public IntegerProperty idTransaksiProperty() {
        return idTransaksi;
    }

    public String getSourceName() {
        return sourceName.get();
    }

    public void setSourceName(String sourceName) {
        this.sourceName.set(sourceName);
    }

    public StringProperty sourceNameProperty() {
        return sourceName;
    }

    public int getJumlah() {
        return jumlah.get();
    }

    public void setJumlah(int jumlah) {
        this.jumlah.set(jumlah);
    }

    public IntegerProperty jumlahProperty() {
        return jumlah;
    }

    public String getTanggal() {
        return tanggal.get();
    }

    public void setTanggal(String tanggal) {
        this.tanggal.set(tanggal);
    }

    public StringProperty tanggalProperty() {
        return tanggal;
    }
}
