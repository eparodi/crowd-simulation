package ar.edu.itba.ss;

import java.util.List;

public interface Integrator {

    void updatePositions(List<Particle> particles, List<Particle> oldParticles);

    void updateSpeeds(List<Particle> particles, List<Particle> oldParticles);

}
