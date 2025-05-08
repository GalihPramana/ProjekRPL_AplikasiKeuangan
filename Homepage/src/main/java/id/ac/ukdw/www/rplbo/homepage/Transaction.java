package id.ac.ukdw.www.rplbo.homepage;


import javafx.beans.property.*;

public class Transaction {
    private final StringProperty idTransaksi;
    private final StringProperty sourceName;
    private final IntegerProperty jumlah;
    private final StringProperty description;
    private final StringProperty tanggal;

    public Transaction(String idTransaksi, String sourceName, int jumlah, String description, String tanggal) {
        this.idTransaksi = new SimpleStringProperty(idTransaksi);
        this.sourceName = new SimpleStringProperty(sourceName);
        this.jumlah = new SimpleIntegerProperty(jumlah);
        this.description = new SimpleStringProperty(description);
        this.tanggal = new SimpleStringProperty(tanggal);
    }

    public String getIdTransaksi() {
        return idTransaksi.get();
    }

    public void setIdTransaksi(String id) {
        this.idTransaksi.set(id);
    }

    public StringProperty idTransaksiProperty() {
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

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
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
