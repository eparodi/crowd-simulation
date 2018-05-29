package ar.edu.itba.ss;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CrowdSimulation {

    private final static double MINIMUM_RADIUS = 0.25;
    private final static double MAXIMUM_RADIUS = 0.29;
    private final static double ELASTIC_CONSTANT = 1.2 * Math.pow(10, 5);
    private final static double VISCOUS_CONSTANT = 2.4 * Math.pow(10, 5);
    private final static double MASS = 80;
    private final static double SOCIAL_FORCE = 2000; // Newton
    private final static double SOCIAL_DISTANCE = 0.08; // Metres
    private final static double ROOM_LENGTH = 20;
    private final static double DOOR_LENGTH = 1.2;
    private final static double WALL_Y = 10;
    private final static double[] TARGET_POSITION = new double[]{ROOM_LENGTH/2, 0};
    private final static double DRIVING_TIME = 0.5;
    private static double desiredSpeed = 0.8;

    public static void main(String args[]) throws CloneNotSupportedException {
        Configuration config = new CliParser().parseOptions(args);
        List<Particle> particles = createParticles(config.getPedestrians());
        desiredSpeed = config.getDesiredSpeed();
        simulate(config, particles);
    }

    private static List<Particle> createParticles(int numberOfParticles){
        List<Particle> particles = new LinkedList<>();
        Random r = new Random();

        for (int i = 0; i < numberOfParticles; i++){

            double radius = r.nextDouble() * (MAXIMUM_RADIUS - MINIMUM_RADIUS) + MINIMUM_RADIUS;

            double x;
            double y;

            do {
                x = randomCoord(radius);
                y = randomCoord(radius);
            }
            while (!validCords(x,y, radius, particles));

            particles.add(new Particle(i+1, new double[]{x, y + WALL_Y}, radius, MASS));
        }

        return particles;
    }

    /**
     * Returns a random coordinate between the radius and L - radius.
     * @param radius radius of the particle.
     * @return a coordinate in the (radius, L - radius) interval.
     */
    private static double randomCoord(double radius){
        return  radius + (ROOM_LENGTH - 2 * radius) * Math.random();
    }

    /**
     * Checks if there is already a particle on that coordinates.
     * @param x coordinate to check.
     * @param y coordinate to check.
     * @param radius radius of the new particle.
     * @param particles list of particles in the cell.
     * @return true if there is already a particle on the given coordinates, false otherwise.
     */
    private static boolean validCords(double x, double y, double radius, List<Particle> particles) {

        for (Particle p: particles){
            boolean valid = Math.pow(p.position[0] - x, 2) + Math.pow(p.position[1] - y, 2) > Math.pow(p.radius + radius, 2);
            if (!valid){
                return false;
            }
        }

        return true;
    }

    private static void simulate(Configuration config, List<Particle> particles) throws CloneNotSupportedException {
        final double time = config.getTime();
        final double animationTime = config.getFps();
        int iterations = 0;
        printParticles(iterations++, particles);

        double dt = Math.pow(10, -5);
        int dt2 = 0;

        Integrator integrator = new Beeman(dt);

        for (double t = 0; t < time; t+=dt){

            List<Particle> clones = cloneParticles(particles);
            integrator.updatePositions(particles, clones);

            integrator.updateSpeeds(particles, clones);

            if (++dt2 % animationTime == 0) {
                printParticles(iterations++, particles);
            }
        }
    }

    public static double[] forces(Particle p, List<Particle> particles) {

        double[] force = new double[2];

        if (contactWithFloor(p)) {
            force = floorForce(p);
        }

        for (Particle neighbour : particles) {

            if (!neighbour.equals(p)){

                /* Particle collision */
                double superposition = p.radius + neighbour.radius - p.getDistanceTo(neighbour);

                double dx = neighbour.position[0] - p.position[0];
                double dy = neighbour.position[1] - p.position[1];
                double mod = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                double ex = (dx / mod);
                double ey = (dy / mod);

                if (superposition > 0) {
                    double relativeSpeed = (p.speed[0] - neighbour.speed[0]) * ex + (p.speed[1] - neighbour.speed[1]) * ey;

                    double normalForce = -ELASTIC_CONSTANT * superposition - VISCOUS_CONSTANT * relativeSpeed;

                    force[0] += normalForce * ex;
                    force[1] += normalForce * ey;
                }

                /* Social force */
                force[0] += SOCIAL_FORCE * Math.exp(-Math.abs(dx) / SOCIAL_DISTANCE) * ex;
                force[1] += SOCIAL_FORCE * Math.exp(-Math.abs(dy) / SOCIAL_DISTANCE) * ey;

                double[] target = getTarget(p);
                double dxTarget = target[0] - p.position[0];
                double dyTarget = target[1] - p.position[1];
                mod = Math.sqrt(Math.pow(dxTarget, 2) + Math.pow(dyTarget, 2));
                ex = dxTarget / mod;
                ey = dyTarget / mod;
                /* Driving force*/
                force[0] += p.mass * (desiredSpeed * ex - p.speed[0]) / DRIVING_TIME;
                force[1] += p.mass * (desiredSpeed * ey - p.speed[1]) / DRIVING_TIME;
            }
        }

        return force;
    }

    private static double[] getTarget(Particle p) {
        double target[];
        double doorX = ROOM_LENGTH/2 - DOOR_LENGTH/2;

        if (p.position[0] < doorX){
            target = new double[]{doorX + 2*p.radius, WALL_Y};
        }else if(p.position[0] > ROOM_LENGTH/2 + DOOR_LENGTH/2){
            target = new double[]{doorX + DOOR_LENGTH - 2*p.radius, WALL_Y};
        }else{
            target = TARGET_POSITION;
        }

        return target;
    }

    private static double[] floorForce(Particle p) {
        double force[] = new double[2];
        double superposition = p.radius - (Math.abs(p.position[1] - WALL_Y));

        if (superposition > 0) {

            double dx = 0;
            double dy = -Math.abs(p.position[1] - WALL_Y); //TODO: check

            double mod = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            double ex = (dx / mod);
            double ey = (dy / mod);

            double relativeSpeed = p.speed[0] * ex + p.speed[1] * ey;

            double normalForce = -ELASTIC_CONSTANT * superposition - VISCOUS_CONSTANT * relativeSpeed;

            force[0] += normalForce * ex;
            force[1] += normalForce * ey;
        }

        return force;
    }

    private static boolean contactWithFloor(Particle p) {
        return p.position[1] > WALL_Y && p.position[1] < (p.radius + WALL_Y) &&
                (p.position[0] < (p.radius + ROOM_LENGTH/2 - DOOR_LENGTH/2) ||
                        p.position[0] > (ROOM_LENGTH/2 + DOOR_LENGTH/2 - p.radius));
    }

    private static void printParticles(int iteration, List<Particle> particles){
        System.out.println(particles.size());
        System.out.println(iteration);
        for (Particle p: particles)
            System.out.println(p.position[0] + "\t" + p.position[1] + "\t" + p.radius);
    }

    private static List<Particle> cloneParticles(List<Particle> particles) throws CloneNotSupportedException {
        List<Particle> clones = new LinkedList<>();
        for (Particle p: particles){
            clones.add(p.getClone());
        }
        return clones;
    }
}
