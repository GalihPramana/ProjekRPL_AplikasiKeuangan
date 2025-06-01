package id.ac.ukdw.www.rplbo.homepage;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class KelolaKategori {
    private final StringProperty kategori;

    public KelolaKategori(String kategori) {
        this.kategori = new SimpleStringProperty(kategori);
    }

    public String getKategori() {
        return kategori.get();
    }

    public void setKategori(String kategori) {
        this.kategori.set(kategori);
    }

    public StringProperty kategoriProperty() {
        return kategori;
    }

    public String getNamaKategori() {
        return getKategori();
    }

    public void setNamaKategori(String nama) {
        setKategori(nama);
    }
}
