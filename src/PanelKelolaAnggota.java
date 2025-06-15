import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class PanelKelolaAnggota extends JPanel {
    private ArrayList<Anggota> daftarAnggota = new ArrayList<>();
    private JTable table;
    private DefaultTableModel tableModel;
    private int nextId = 1;

    private PanelArisan panelArisan; // 🔗 Referensi ke PanelArisan

    public PanelKelolaAnggota() {
        setLayout(new BorderLayout());

        // === Judul ===
        JLabel title = new JLabel("Kelola Anggota", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // === Tabel ===
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nama"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // === Tombol ===
        JButton btnTambah = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnHapus = new JButton("Hapus");
        JButton btnImport = new JButton("Import"); // 🆕

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnTambah);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnImport); // 🆕
        add(buttonPanel, BorderLayout.SOUTH);

        // === Aksi Tambah ===
        btnTambah.addActionListener(e -> {
            String nama = JOptionPane.showInputDialog(this, "Masukkan Nama Anggota:");
            if (nama != null && !nama.trim().isEmpty()) {
                Anggota anggota = new Anggota(nextId++, nama.trim());
                daftarAnggota.add(anggota);
                tableModel.addRow(new Object[]{anggota.getId(), anggota.getNama()});
                updatePanelArisan();
            }
        });

        // === Aksi Edit ===
        btnEdit.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String namaBaru = JOptionPane.showInputDialog(this, "Edit Nama:", tableModel.getValueAt(selectedRow, 1));
                if (namaBaru != null && !namaBaru.trim().isEmpty()) {
                    Anggota anggota = daftarAnggota.get(selectedRow);
                    anggota.setNama(namaBaru.trim());
                    tableModel.setValueAt(namaBaru.trim(), selectedRow, 1);
                    updatePanelArisan();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih dulu anggotanya, Bang!");
            }
        });

        // === Aksi Hapus ===
        btnHapus.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin mau hapus anggota ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (konfirmasi == JOptionPane.YES_OPTION) {
                    daftarAnggota.remove(selectedRow);
                    tableModel.removeRow(selectedRow);
                    updatePanelArisan();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih dulu yang mau dihapus, Bang!");
            }
        });

        // === Aksi Import dari file ===
        btnImport.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                try (Scanner scanner = new Scanner(file)) {
                    daftarAnggota.clear();
                    tableModel.setRowCount(0);
                    nextId = 1;

                    while (scanner.hasNextLine()) {
                        String nama = scanner.nextLine().trim();
                        if (!nama.isEmpty()) {
                            Anggota anggota = new Anggota(nextId++, nama);
                            daftarAnggota.add(anggota);
                            tableModel.addRow(new Object[]{anggota.getId(), anggota.getNama()});
                        }
                    }

                    updatePanelArisan();
                    JOptionPane.showMessageDialog(this, "Berhasil import anggota, Bang!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Gagal import, Bang 😢\n" + ex.getMessage());
                }
            }
        });
    }

    public ArrayList<Anggota> getDaftarAnggota() {
        return daftarAnggota;
    }

    public void setPanelArisan(PanelArisan panelArisan) {
        this.panelArisan = panelArisan;
    }

    private void updatePanelArisan() {
        if (panelArisan != null) {
            panelArisan.setDaftarAnggota(daftarAnggota);
        }
    }
    
    public void simpanDataKeFile() {
    try (java.io.PrintWriter writer = new java.io.PrintWriter("data_anggota.txt")) {
        for (Anggota a : daftarAnggota) {
            writer.println(a.getNama());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public void loadDataDariFile() {
    try (java.util.Scanner scanner = new java.util.Scanner(new java.io.File("data_anggota.txt"))) {
        daftarAnggota.clear();
        tableModel.setRowCount(0);
        nextId = 1;

        while (scanner.hasNextLine()) {
            String nama = scanner.nextLine().trim();
            if (!nama.isEmpty()) {
                Anggota anggota = new Anggota(nextId++, nama);
                daftarAnggota.add(anggota);
                tableModel.addRow(new Object[]{anggota.getId(), anggota.getNama()});
            }
        }

        updatePanelArisan();
    } catch (Exception e) {
        System.out.println("Belum ada file data_anggota.txt, Bang. Gak masalah.");
    }
}

}
