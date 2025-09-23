import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {
    private WindowParams windowParams;
    private List<Point> graphPoints = new ArrayList<>();
    private double minX, maxX;
    private String functionInfo = "y = tg(x²)/(x² + 1)";

    public GraphPanel() {
        setBackground(new Color(30, 30, 30));
        setPreferredSize(new Dimension(800, 600));
    }

    public void setParameters(double minX, double maxX) {
        this.minX = minX;
        this.maxX = maxX;
        calculateGraphPoints();
        repaint();
    }

    private double calculateFunction(double x) {
        double arg = x * x;
        double tanVal = Math.tan(arg);
        double denom = arg + 1;
        return tanVal / denom;
    }

    private void calculateGraphPoints() {
        graphPoints.clear();
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        int numPoints = 2000;
        double step = (maxX - minX) / numPoints;
        List<Double> validY = new ArrayList<>();

        for (double x = minX; x <= maxX; x += step) {
            double y = calculateFunction(x);
            if (!Double.isNaN(y) && !Double.isInfinite(y) && Math.abs(y) < 1e6) {
                validY.add(y);
                minY = Math.min(minY, y);
                maxY = Math.max(maxY, y);
            }
        }

        double yRange = maxY - minY;
        minY -= yRange * 0.1;
        maxY += yRange * 0.1;

        windowParams = new WindowParams(minX, maxX, minY, maxY, 50, getWidth() - 50, 50, getHeight() - 100);

        for (double x = minX; x <= maxX; x += step) {
            double y = calculateFunction(x);
            if (!Double.isNaN(y) && !Double.isInfinite(y) && Math.abs(y) < 1e6) {
                int screenX = windowParams.toScreenX(x);
                int screenY = windowParams.toScreenY(y);
                graphPoints.add(new Point(screenX, screenY));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (windowParams == null || graphPoints.isEmpty()) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Введіть межі інтервалу та натисніть 'Побудувати графік'", 200, 300);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawAxes(g2d);
        drawGraph(g2d);
        drawLabels(g2d);
    }

    private void drawAxes(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1));

        int xAxisScreen = windowParams.toScreenX(0);
        if (xAxisScreen >= windowParams.getI1() && xAxisScreen <= windowParams.getI2()) {
            g2d.drawLine(xAxisScreen, windowParams.getJ1(), xAxisScreen, windowParams.getJ2());
        }

        int yAxisScreen = windowParams.toScreenY(0);
        if (yAxisScreen >= windowParams.getJ1() && yAxisScreen <= windowParams.getJ2()) {
            g2d.drawLine(windowParams.getI1(), yAxisScreen, windowParams.getI2(), yAxisScreen);
        }

        g2d.setColor(new Color(100, 100, 100));
        g2d.drawRect(windowParams.getI1(), windowParams.getJ1(), windowParams.getI2() - windowParams.getI1(), windowParams.getJ2() - windowParams.getJ1());
    }

    private void drawGraph(Graphics2D g2d) {
        g2d.setColor(Color.CYAN);
        g2d.setStroke(new BasicStroke(2));

        for (int i = 0; i < graphPoints.size() - 1; i++) {
            Point p1 = graphPoints.get(i);
            Point p2 = graphPoints.get(i + 1);
            if (Math.abs(p1.y - p2.y) < 200) {
                g2d.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
            }
        }
    }

    private void drawLabels(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Функція: " + functionInfo, 60, 30);
        g2d.drawString(String.format("Інтервал: [%.2f, %.2f]", minX, maxX), 60, 45);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("X", windowParams.getI2() - 10, windowParams.getJ2() + 15);
        g2d.drawString("Y", windowParams.getI1() - 15, windowParams.getJ1() + 10);

        drawAxisLabels(g2d);
    }

    private void drawAxisLabels(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        double xStep = (windowParams.getMaxX() - windowParams.getMinX()) / 5;
        for (int i = 0; i <= 5; i++) {
            double x = windowParams.getMinX() + i * xStep;
            int screenX = windowParams.toScreenX(x);
            g2d.drawString(String.format("%.2f", x), screenX - 15, windowParams.getJ2() + 15);
        }

        double yStep = (windowParams.getMaxY() - windowParams.getMinY()) / 5;
        for (int i = 0; i <= 5; i++) {
            double y = windowParams.getMinY() + i * yStep;
            int screenY = windowParams.toScreenY(y);
            g2d.drawString(String.format("%.2f", y), windowParams.getI1() - 40, screenY + 5);
        }
    }
}
