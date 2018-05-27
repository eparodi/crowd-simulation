package ar.edu.itba.ss;

import java.util.List;

public class Beeman implements Integrator{

    private double dt;

    public Beeman(double dt) {
        this.dt = dt;
    }

    @Override
    public void updatePositions(List<Particle> particles, List<Particle> oldParticles) {

        for (Particle p: particles){

            if (p.acceleration == null){
                p.acceleration = getAcceleration(p, oldParticles);
            }

            for (int i = 0; i < p.position.length; i++){
                p.position[i] = p.position[i] + p.speed[i] * dt +
                        (2.0 / 3) * p.acceleration[i] * Math.pow(dt, 2) -
                        (1.0 / 6) * p.previousAcceleration[i] * Math.pow(dt, 2);
            }

        }

    }

    @Override
    public void updateSpeeds(List<Particle> particles, List<Particle> oldParticles){

        for (Particle p : particles) {


            double[] oldSpeed = new double[]{p.speed[0], p.speed[1]};

            for (int i = 0; i < p.speed.length; i++){
                p.speed[i] = p.speed[i] + (3.0 / 2) * p.acceleration[i] * dt -
                        (1.0 / 2) * p.previousAcceleration[i] * dt;
            }

            double[] newAcceleration = getAcceleration(p, oldParticles);

            for (int i = 0; i < p.speed.length; i++){
                p.speed[i] = oldSpeed[i] + (1.0 / 3) * newAcceleration[i] * dt +
                        (5.0 / 6) * p.acceleration[i] * dt -
                        (1.0 / 6) * p.previousAcceleration[i] * dt;
            }

            p.previousAcceleration = p.acceleration;
            p.acceleration = newAcceleration;
        }
    }

    private double[] getAcceleration(Particle p, List<Particle> particles){
        double[] newForce = CrowdSimulation.forces(p, particles);
        newForce[0] = newForce[0] / p.mass;
        newForce[1] = newForce[1] / p.mass;
        return newForce;
    }
}

