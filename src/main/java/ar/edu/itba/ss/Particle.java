package ar.edu.itba.ss;

public class Particle implements Cloneable{

    protected int id;
    protected double[] position;
    protected double[] speed = new double[]{0,0};
    protected double radius;
    protected double mass;
    protected double[] acceleration = null;// new double[]{0,0};
    protected double[] previousAcceleration = new double[]{0,0};

    public Particle(int id, double[] position, double radius, double mass){
        this.id = id;
        this.position = position;
        this.radius = radius;
        this.mass = mass;
    }

    public double getDistanceTo(Particle neighbour) {
        return Math.sqrt(Math.pow(this.position[0] - neighbour.position[0], 2) +
                Math.pow(this.position[1] - neighbour.position[1], 2));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Particle particle = (Particle) o;

        return id == particle.id;
    }

    @Override
    public int hashCode() {
        return new Integer(id).hashCode();
    }

    Particle getClone() throws CloneNotSupportedException {
        return (Particle) super.clone();
    }

}
