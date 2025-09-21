public class WindowParams {
    private double minX, maxX, minY, maxY;
    private int i1, i2, j1, j2;

    public WindowParams(double minX, double maxX, double minY, double maxY, int i1, int i2, int j1, int j2) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.i1 = i1;
        this.i2 = i2;
        this.j1 = j1;
        this.j2 = j2;
    }

    public int toScreenX(double x) {
        return (int) (i1 + Math.round((x - minX) * (i2 - i1) / (maxX - minX)));
    }

    public int toScreenY(double y) {
        return (int) (j2 + Math.round((y - minY) * (j1 - j2) / (maxY - minY)));
    }

    public double toRealX(int i) {
        return minX + (i - i1) * (maxX - minX) / (i2 - i1);
    }

    public double toRealY(int j) {
        return minY + (j - j2) * (maxY - minY) / (j1 - j2);
    }

    public double getMaxX() { return maxX; }
    public double getMaxY() { return maxY; }
    public double getMinX() { return minX; }
    public double getMinY() { return minY; }
    public int getI1() { return i1; }
    public int getI2() { return i2; }
    public int getJ1() { return j1; }
    public int getJ2() { return j2; }
}