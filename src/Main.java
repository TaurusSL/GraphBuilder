import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        GraphBuilderGUI mainWindow = new GraphBuilderGUI();
        createMenuBar(mainWindow);
        mainWindow.setVisible(true);
    }

    private static void createMenuBar(GraphBuilderGUI mainWindow) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(40, 40, 40));
        menuBar.setForeground(Color.WHITE);

        JMenu fileMenu = new JMenu("Файл");
        fileMenu.setForeground(Color.BLACK);
        JMenuItem exitItem = new JMenuItem("Вийти");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(mainWindow, "Ви дійсно хочете вийти?", "Підтвердження", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Довідка");
        helpMenu.setForeground(Color.BLACK);
        JMenuItem aboutItem = new JMenuItem("Про програму");
        aboutItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
        aboutItem.addActionListener(e -> mainWindow.showAbout());
        JMenuItem instructionsItem = new JMenuItem("Інструкції");
        instructionsItem.addActionListener(e -> showInstructions(mainWindow));
        helpMenu.add(instructionsItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        mainWindow.setJMenuBar(menuBar);
    }

    private static void showInstructions(Component parent) {
        String instructions = "ІНСТРУКЦІЇ ВИКОРИСТАННЯ\n\n" +
                "1. ВВЕДЕННЯ ПАРАМЕТРІВ:\n" +
                "   • Мін. X - мінімальне значення інтервалу\n" +
                "   • Макс. X - максимальне значення інтервалу\n" +
                "   • За замовчуванням: [0, 1.4]\n\n" +
                "2. ПОБУДОВА ГРАФІКА:\n" +
                "   • Натисніть кнопку 'Побудувати графік'\n" +
                "   • Або натисніть Enter в полі введення\n\n" +
                "3. ФУНКЦІЯ (Варіант 7):\n" +
                "   y = tan(x²)/(x² + 1)\n\n" +
                "4. ОСОБЛИВОСТІ:\n" +
                "   • Функція має асимптоту біля x ≈ 1.253\n" +
                "   • Програма автоматично масштабує координати\n" +
                "   • Графік відображається cyan кольором\n\n" +
                "5. КЛАВІАТУРНІ СКОРОЧЕННЯ:\n" +
                "   • Ctrl+Q - Вийти з програми\n" +
                "   • F1 - Про програму\n" +
                "   • Enter - Побудувати графік";

        JTextArea textArea = new JTextArea(instructions);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(50, 50, 50));
        textArea.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        scrollPane.setBackground(new Color(50, 50, 50));

        JOptionPane.showMessageDialog(parent, scrollPane, "Інструкції використання", JOptionPane.INFORMATION_MESSAGE);
    }
}