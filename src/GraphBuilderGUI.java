import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GraphBuilderGUI extends JFrame {
    private GraphPanel graphPanel = new GraphPanel();
    private JTextField minXField = new JTextField("0", 8);
    private JTextField maxXField = new JTextField("1.4", 8);
    private JLabel statusLabel = new JLabel("Готовий до побудови графіка");

    public GraphBuilderGUI() {
        setTitle("GraphBuilder v1.0.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 750);
        setLocationRelativeTo(null);
        setResizable(true);

        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setBackground(new Color(40, 40, 40));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);

        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(graphPanel);
        add(scrollPane, BorderLayout.CENTER);

        add(statusLabel, BorderLayout.SOUTH);

        ActionListener buildOnEnter = e -> buildGraph();
        minXField.addActionListener(buildOnEnter);
        maxXField.addActionListener(buildOnEnter);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(50, 50, 50));

        JLabel functionLabel = new JLabel("Функція: y = tan(x²)/(x² + 1) (Варіант 7)");
        functionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        functionLabel.setForeground(Color.CYAN);

        JLabel minXLabel = new JLabel("Мін. X:");
        minXLabel.setForeground(Color.WHITE);
        JLabel maxXLabel = new JLabel("Макс. X:");
        maxXLabel.setForeground(Color.WHITE);

        JButton buildButton = new JButton("Побудувати графік");
        buildButton.setBackground(new Color(70, 130, 180));
        buildButton.setForeground(Color.BLACK);
        buildButton.setFont(new Font("Arial", Font.BOLD, 12));
        buildButton.addActionListener(e -> buildGraph());

        JButton resetButton = new JButton("Скинути");
        resetButton.setBackground(new Color(220, 20, 60));
        resetButton.setForeground(Color.BLACK);
        resetButton.addActionListener(e -> {
            minXField.setText("0");
            maxXField.setText("1.4");
            statusLabel.setText("Параметри скинуто");
        });

        panel.add(functionLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(minXLabel);
        panel.add(minXField);
        panel.add(maxXLabel);
        panel.add(maxXField);
        panel.add(buildButton);
        panel.add(resetButton);

        return panel;
    }

    private void buildGraph() {
        try {
            double minX = Double.parseDouble(minXField.getText().trim());
            double maxX = Double.parseDouble(maxXField.getText().trim());

            if (minX >= maxX) {
                showError("Мін. X повинно бути менше Макс. X!");
                return;
            }

            if (maxX - minX < 0.01) {
                showError("Інтервал занадто малий!");
                return;
            }

            graphPanel.setParameters(minX, maxX);
            statusLabel.setText(String.format("Графік побудовано для [%.3f, %.3f]", minX, maxX));

        } catch (NumberFormatException ex) {
            showError("Введіть коректні числа!");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Помилка", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("Помилка введення");
    }

    public void showAbout() {
        String aboutText = "GraphBuilder - Побудовник графіків функцій\n" +
                "Версія: 1.0.0\n\n" +
                "Лабораторна робота №1\n" +
                "Варіант 7: y = tan(x²)/(x² + 1)\n\n" +
                "Функціональність:\n" +
                "• Побудова графіків\n" +
                "• Діалоговий режим введення меж\n" +
                "• Автоматичне масштабування\n" +
                "• Візуалізація осей\n\n" +
                "Створено з Java Swing студентом 3157ст3 Козаченко Денис";

        JOptionPane.showMessageDialog(this, aboutText, "Про програму", JOptionPane.INFORMATION_MESSAGE);
    }
}