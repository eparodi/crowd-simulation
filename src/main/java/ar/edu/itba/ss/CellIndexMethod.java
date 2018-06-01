package ar.edu.itba.ss;

import java.util.*;

public class CellIndexMethod{

    private int matrixSizeRows;
    private int matrixSizeColumns;
    private int numberOfCells;
    private double cellLength;
    private HashMap<Integer, List<Particle>> cells = new HashMap<>();
    Set<Particle> particles = new HashSet<>();

    public CellIndexMethod(double width, double height, double radius){
        this.matrixSizeRows = (int) Math.ceil(width / radius);
        this.matrixSizeColumns = (int) Math.ceil(height / radius) + 1;
        this.numberOfCells = this.matrixSizeRows * (this.matrixSizeColumns+1);
        this.cellLength = radius;
        for (int i = 0; i < numberOfCells; i++){
            cells.put(i, new LinkedList<>());
        }
    }

    private Integer findKeyOfParticle(Particle p){
        int cellX = (int) Math.floor(p.position[0] / this.cellLength);
        int cellY = (int) Math.floor(p.position[1] / this.cellLength);
        if (cellX > this.matrixSizeRows || cellX < 0 || cellY > this.matrixSizeColumns || cellY < 0){
            return null;
        }
        return cellY * this.matrixSizeRows + cellX;
    }

    private List<Integer> findNeighborsIndexCells(Particle p){
        List<Integer> neighborCells = new LinkedList<>();
        int cellX = p.cell % this.matrixSizeRows;
        int cellY = p.cell / this.matrixSizeRows;
        if (cellX == 0){
            cellX = this.matrixSizeRows;
        }
        if (cellY + 1 < this.matrixSizeColumns){
            if (cellX + 1 < this.matrixSizeRows){
                neighborCells.add((cellY+1) * this.matrixSizeRows + cellX + 1);
            }
            if (cellX - 1 > 0){
                neighborCells.add((cellY+1) * this.matrixSizeRows + cellX - 1);
            }
            neighborCells.add((cellY+1) * this.matrixSizeRows + cellX);
        }else if(cellY > this.matrixSizeColumns){
            throw new IllegalStateException("Matrix cell wrong.");
        }

        if (cellX < this.matrixSizeRows){
            neighborCells.add(cellY * this.matrixSizeRows + cellX + 1);
        }

        neighborCells.add(p.cell);
        return neighborCells;
    }

    public Boolean putParticle(Particle p){
        if (p.cell != null){
            cells.get(p.cell).remove(p);
        }
        Integer key = this.findKeyOfParticle(p);
        if (key != null){
            p.cell = key;
            if (cells.get(key) == null){
                System.out.println(key + " " + p.position[0] + " " + p.position[1]);
            }
            particles.add(p);
            cells.get(key).add(p);
            return true;
        }else{
            return false;
        }
    }

    public void setNeighbors(){

        for (Particle p: this.particles){
            p.neighbors = new HashSet<>();
        }
        for (Particle p : this.particles){
            List<Integer> index = findNeighborsIndexCells(p);
//            List<Particle> neighbors = new LinkedList<>();
            for (Integer i : index){
                List<Particle> particleList = cells.get(i);
//                neighbors.addAll(particleList);
                addNeighbor(p, particleList);
            }

//            addNeighbor(p, neighbors);
        }
    }

    private void addNeighbor(Particle p, List<Particle> particles){
        for (Particle particle : particles){
            if (!particle.equals(p)) {
                try {
                    p.neighbors.add(particle.getClone());
                    particle.neighbors.add(p.getClone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
