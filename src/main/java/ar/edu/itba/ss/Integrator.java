package ar.edu.itba.ss;

import java.util.Set;

public interface Integrator {

    void updatePositions(Set<Pedestrian> pedestrians);

    void updateSpeeds(Set<Pedestrian> pedestrians);

}
