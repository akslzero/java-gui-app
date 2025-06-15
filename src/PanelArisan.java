import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

import org.apache.poi.xwpf.usermodel.*; // ✅ Tambahin ini buat export Word
import java.io.FileOutputStream;

public class PanelArisan extends JPanel {
    private JComboBox<Anggota> comboAnggota;
    private JComboBox<String> comboBulan; // ✅ Combo untuk bulan
    private JSpinner spinnerTahun; // ✅ Spinner untuk tahun
    private DefaultTableModel tableModel;
    private ArrayList<Anggota> daftarPemenang = new ArrayList<>();

    public PanelArisan() {
        setLayout(new BorderLayout());

        // Judul
        JLabel title = new JLabel("Yang Mendapat Arisan", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // Tabel
        tableModel = new DefaultTableModel(new Object[]{"No", "Nama"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Combo dan tombol
        comboAnggota = new JComboBox<>();
        comboBulan = new JComboBox<>(new String[]{
            "JANUARI", "FEBRUARI", "MARET", "APRIL", "MEI", "JUNI",
            "JULI", "AGUSTUS", "SEPTEMBER", "OKTOBER", "NOVEMBER", "DESEMBER"
        });
        comboBulan.setSelectedIndex(5); // Default ke JUNI

        spinnerTahun = new JSpinner(new SpinnerNumberModel(2025, 2000, 2100, 1));

        JButton btnTambah = new JButton("Tambah Pemenang");
        JButton btnExport = new JButton("Export ke Word");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Pilih Nama:"));
        inputPanel.add(comboAnggota);
        inputPanel.add(new JLabel("Bulan:"));
        inputPanel.add(comboBulan);
        inputPanel.add(new JLabel("Tahun:"));
        inputPanel.add(spinnerTahun);
        inputPanel.add(btnTambah);
        inputPanel.add(btnExport);

        add(inputPanel, BorderLayout.SOUTH);

        // Aksi Tambah Pemenang
        btnTambah.addActionListener(e -> {
            if (tableModel.getRowCount() >= 11) {
                JOptionPane.showMessageDialog(this, "Udah 11 pemenang Bang, cukup ya 😂");
                return;
            }

            Anggota terpilih = (Anggota) comboAnggota.getSelectedItem();
            if (terpilih == null) return;

            if (daftarPemenang.contains(terpilih)) {
                JOptionPane.showMessageDialog(this, "Anggota ini udah menang, Bang!");
                return;
            }

            daftarPemenang.add(terpilih);
            tableModel.addRow(new Object[]{tableModel.getRowCount() + 1, terpilih.getNama()});
        });

        // Aksi Export ke Word
        btnExport.addActionListener(e -> {
            if (daftarPemenang.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada pemenang Bang, mau export apa? 😅");
                return;
            }

            try {
                XWPFDocument doc = new XWPFDocument();

                String bulan = (String) comboBulan.getSelectedItem();
                int tahun = (Integer) spinnerTahun.getValue();

                // Judul tengah & tebal
                XWPFParagraph para = doc.createParagraph();
                para.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun run = para.createRun();
                run.setText("YANG MENDAPAT ARISAN BULAN " + bulan + " " + tahun);
                run.setBold(true);
                run.setFontSize(14);

                // Tabel dengan header: NO | NAMA | PARAF | KETERANGAN
                XWPFTable tableDoc = doc.createTable();

                // Header row
                XWPFTableRow header = tableDoc.getRow(0);
                header.getCell(0).setText("NO");
                header.addNewTableCell().setText("NAMA");
                header.addNewTableCell().setText("PARAF");
                header.addNewTableCell().setText("KETERANGAN");

                // Data rows
                for (int i = 0; i < daftarPemenang.size(); i++) {
                    Anggota a = daftarPemenang.get(i);
                    XWPFTableRow row = tableDoc.createRow();
                    row.getCell(0).setText(String.valueOf(i + 1));
                    row.getCell(1).setText(a.getNama());
                    row.getCell(2).setText(""); // Paraf kosong
                    row.getCell(3).setText(""); // Keterangan kosong
                }

                // Rata tengah semua sel biar rapi
                // Rata tengah semua sel biar rapi
                for (XWPFTableRow row : tableDoc.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

                        // Pastikan setiap cell punya minimal 1 paragraph
                        if (cell.getParagraphs().isEmpty()) {
                            cell.addParagraph();
                        }

                        XWPFParagraph p = cell.getParagraphs().get(0);
                        p.setAlignment(ParagraphAlignment.CENTER);
                    }
                }


                // Simpan dokumen pakai file chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new java.io.File("Pemenang_Arisan_" + bulan + "_" + tahun + ".docx"));
                int result = fileChooser.showSaveDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    java.io.File file = fileChooser.getSelectedFile();
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        doc.write(out);
                    }
                    JOptionPane.showMessageDialog(this, "Berhasil export ke:\n" + file.getAbsolutePath());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal export Bang 😓: " + ex.getMessage());
            }
        });
    }

    // Buat nerima data anggota dari PanelKelolaAnggota
    public void setDaftarAnggota(ArrayList<Anggota> daftarAnggota) {
        comboAnggota.removeAllItems();
        for (Anggota a : daftarAnggota) {
            comboAnggota.addItem(a);
        }
    }

    // Getter buat export nanti
    public ArrayList<Anggota> getDaftarPemenang() {
        return daftarPemenang;
    }
}
