import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {
    private WindowParams windowParams;
    private List<Point2D> graphPoints = new ArrayList<>();
    private double minX, maxX;
    private String functionInfo = "y = tg(x²)/(x² + 1)";

    private static class Point2D {
        double x, y;
        Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    public GraphPanel() {
        setBackground(new Color(20, 20, 25));
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

        int numPoints = 8000;
        double step = (maxX - minX) / numPoints;
        List<Double> validY = new ArrayList<>();

        for (double x = minX; x <= maxX; x += step) {
            try {
                double y = calculateFunction(x);
                if (!Double.isNaN(y) && !Double.isInfinite(y) && Math.abs(y) < 1e4) {
                    validY.add(y);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                }
            } catch (Exception e) {

            }
        }

        if (validY.isEmpty()) {
            return;
        }

        double yRange = maxY - minY;
        if (yRange < 0.1) yRange = 0.1;

        minY -= yRange * 0.15;
        maxY += yRange * 0.15;

        windowParams = new WindowParams(minX, maxX, minY, maxY,
                60, getWidth() - 40, 40, getHeight() - 80);

        double adaptiveStep = step;
        double prevX = minX;
        double prevY = calculateFunction(minX);

        if (!Double.isNaN(prevY) && !Double.isInfinite(prevY) && Math.abs(prevY) < 1e4) {
            graphPoints.add(new Point2D(prevX, prevY));
        }

        for (double x = minX + step; x <= maxX; x += adaptiveStep) {
            try {
                double y = calculateFunction(x);

                if (!Double.isNaN(y) && !Double.isInfinite(y) && Math.abs(y) < 1e4) {
                    if (!graphPoints.isEmpty()) {
                        Point2D lastPoint = graphPoints.get(graphPoints.size() - 1);
                        double deltaY = Math.abs(y - lastPoint.y);

                        if (deltaY > yRange * 0.5) {
                            graphPoints.add(new Point2D(Double.NaN, Double.NaN));
                        }
                    }

                    graphPoints.add(new Point2D(x, y));

                    if (!graphPoints.isEmpty() && graphPoints.size() > 1) {
                        Point2D lastPoint = graphPoints.get(graphPoints.size() - 2);
                        if (!Double.isNaN(lastPoint.y)) {
                            double derivative = Math.abs((y - lastPoint.y) / (x - lastPoint.x));
                            if (derivative > yRange / (maxX - minX) * 10) {
                                adaptiveStep = step * 0.5;
                            } else {
                                adaptiveStep = step;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                graphPoints.add(new Point2D(Double.NaN, Double.NaN));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (windowParams == null || graphPoints.isEmpty()) {
            g.setColor(new Color(150, 150, 150));
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            FontMetrics fm = g.getFontMetrics();
            String text = "Введіть межі інтервалу та натисніть 'Побудувати графік'";
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = getHeight() / 2;
            g.drawString(text, x, y);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawGrid(g2d);
        drawAxes(g2d);
        drawGraph(g2d);
        drawLabels(g2d);
        drawAsymptotes(g2d);
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(new Color(40, 40, 45));
        g2d.setStroke(new BasicStroke(0.5f));

        double xStep = (windowParams.getMaxX() - windowParams.getMinX()) / 10;
        for (int i = 1; i < 10; i++) {
            double x = windowParams.getMinX() + i * xStep;
            int screenX = windowParams.toScreenX(x);
            if (screenX >= windowParams.getI1() && screenX <= windowParams.getI2()) {
                g2d.drawLine(screenX, windowParams.getJ1(), screenX, windowParams.getJ2());
            }
        }

        double yStep = (windowParams.getMaxY() - windowParams.getMinY()) / 10;
        for (int i = 1; i < 10; i++) {
            double y = windowParams.getMinY() + i * yStep;
            int screenY = windowParams.toScreenY(y);
            if (screenY >= windowParams.getJ1() && screenY <= windowParams.getJ2()) {
                g2d.drawLine(windowParams.getI1(), screenY, windowParams.getI2(), screenY);
            }
        }
    }

    private void drawAxes(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(1.5f));

        g2d.setColor(new Color(200, 200, 200));
        int yAxisScreen = windowParams.toScreenY(0);
        if (yAxisScreen >= windowParams.getJ1() && yAxisScreen <= windowParams.getJ2()) {
            g2d.drawLine(windowParams.getI1(), yAxisScreen, windowParams.getI2(), yAxisScreen);
        }

        int xAxisScreen = windowParams.toScreenX(0);
        if (xAxisScreen >= windowParams.getI1() && xAxisScreen <= windowParams.getI2()) {
            g2d.drawLine(xAxisScreen, windowParams.getJ1(), xAxisScreen, windowParams.getJ2());
        }

        g2d.setColor(new Color(120, 120, 125));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(windowParams.getI1(), windowParams.getJ1(),
                windowParams.getI2() - windowParams.getI1(),
                windowParams.getJ2() - windowParams.getJ1());
    }

    private void drawAsymptotes(Graphics2D g2d) {
        g2d.setColor(new Color(255, 100, 100, 100));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10, new float[]{5, 5}, 0));

        for (int n = 0; n < 10; n++) {
            double asymptote = Math.sqrt(Math.PI/2 + Math.PI * n);
            if (asymptote >= minX && asymptote <= maxX) {
                int screenX = windowParams.toScreenX(asymptote);
                g2d.drawLine(screenX, windowParams.getJ1(), screenX, windowParams.getJ2());
            }

            asymptote = -Math.sqrt(Math.PI/2 + Math.PI * n);
            if (asymptote >= minX && asymptote <= maxX) {
                int screenX = windowParams.toScreenX(asymptote);
                g2d.drawLine(screenX, windowParams.getJ1(), screenX, windowParams.getJ2());
            }
        }
    }

    private void drawGraph(Graphics2D g2d) {
        g2d.setColor(new Color(0, 255, 150));
        g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        Path2D path = new Path2D.Double();
        boolean pathStarted = false;

        for (int i = 0; i < graphPoints.size(); i++) {
            Point2D point = graphPoints.get(i);

            if (Double.isNaN(point.x) || Double.isNaN(point.y)) {
                if (pathStarted) {
                    g2d.draw(path);
                    path.reset();
                    pathStarted = false;
                }
                continue;
            }

            int screenX = windowParams.toScreenX(point.x);
            int screenY = windowParams.toScreenY(point.y);

            if (screenX >= windowParams.getI1() && screenX <= windowParams.getI2() &&
                    screenY >= windowParams.getJ1() && screenY <= windowParams.getJ2()) {

                if (!pathStarted) {
                    path.moveTo(screenX, screenY);
                    pathStarted = true;
                } else {
                    path.lineTo(screenX, screenY);
                }
            } else {
                if (pathStarted) {
                    g2d.draw(path);
                    path.reset();
                    pathStarted = false;
                }
            }
        }

        if (pathStarted) {
            g2d.draw(path);
        }
    }

    private void drawLabels(Graphics2D g2d) {
        g2d.setColor(new Color(220, 220, 220));
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Функція: " + functionInfo, 70, 25);

        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString(String.format("Інтервал: [%.3f, %.3f]", minX, maxX), 70, 40);

        g2d.setColor(new Color(180, 180, 180));
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("X", windowParams.getI2() - 15, windowParams.getJ2() + 20);
        g2d.drawString("Y", windowParams.getI1() - 20, windowParams.getJ1() + 15);

        drawAxisLabels(g2d);
    }

    private void drawAxisLabels(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.setColor(new Color(160, 160, 160));

        double xStep = (windowParams.getMaxX() - windowParams.getMinX()) / 8;
        for (int i = 0; i <= 8; i++) {
            double x = windowParams.getMinX() + i * xStep;
            int screenX = windowParams.toScreenX(x);
            if (screenX >= windowParams.getI1() && screenX <= windowParams.getI2()) {
                String label = String.format("%.2f", x);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                g2d.drawString(label, screenX - labelWidth/2, windowParams.getJ2() + 15);

                g2d.drawLine(screenX, windowParams.getJ2() - 3, screenX, windowParams.getJ2() + 3);
            }
        }

        double yStep = (windowParams.getMaxY() - windowParams.getMinY()) / 8;
        for (int i = 0; i <= 8; i++) {
            double y = windowParams.getMinY() + i * yStep;
            int screenY = windowParams.toScreenY(y);
            if (screenY >= windowParams.getJ1() && screenY <= windowParams.getJ2()) {
                String label = String.format("%.2f", y);
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(label, windowParams.getI1() - fm.stringWidth(label) - 8, screenY + 4);

                g2d.drawLine(windowParams.getI1() - 3, screenY, windowParams.getI1() + 3, screenY);
            }
        }
    }
}