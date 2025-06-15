public class Anggota {
    private final int id;
    private String nama;

    public Anggota(int id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    // Buat tampil bagus di combo box, dll
    @Override
    public String toString() {
        return nama;
    }
}
