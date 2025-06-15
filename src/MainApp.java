import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainApp() {
        setTitle("Arisan App v1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 720);
        setLocationRelativeTo(null);

        // === Panel Menu (atas) ===
        JPanel menuPanel = new JPanel();
        JButton btnAnggota = new JButton("Kelola Anggota");
        JButton btnArisan = new JButton("Arisan");
        menuPanel.add(btnAnggota);
        menuPanel.add(btnArisan);

        // === Bikin panel utama ===
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // === Bikin panel-panel yang dibagikan ===
        PanelKelolaAnggota panelAnggota = new PanelKelolaAnggota();
        PanelArisan panelArisan = new PanelArisan();

        // Biar PanelKelola bisa kirim data ke PanelArisan
        panelAnggota.setPanelArisan(panelArisan);

        // Tambahkan panel-panel ke CardLayout
        mainPanel.add(panelAnggota, "anggota");
        mainPanel.add(panelArisan, "arisan");

        // === Aksi Tombol ===
        btnAnggota.addActionListener(e -> {
            cardLayout.show(mainPanel, "anggota");
        });

        btnArisan.addActionListener(e -> {
            panelArisan.setDaftarAnggota(panelAnggota.getDaftarAnggota());
            cardLayout.show(mainPanel, "arisan");
        });

        // === Layout Frame ===
        setLayout(new BorderLayout());
        add(menuPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // === ✅ Load data saat aplikasi dibuka
        panelAnggota.loadDataDariFile();

        // === ✅ Simpan data saat aplikasi ditutup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                panelAnggota.simpanDataKeFile();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}
