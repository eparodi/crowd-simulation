package ar.edu.itba.ss;

public class Configuration {

    protected double time;
    protected double fps;
    protected int pedestrians;
    protected double desiredSpeed;

    public Configuration(double time, double fps, int pedestrians, double desiredSpeed) {
        this.time = time;
        this.fps = fps;
        this.pedestrians = pedestrians;
        this.desiredSpeed = desiredSpeed;
    }

    public double getTime() {
        return time;
    }

    public double getFps() {
        return fps;
    }

    public int getPedestrians() {
        return pedestrians;
    }

    public double getDesiredSpeed() {
        return desiredSpeed;
    }
}
