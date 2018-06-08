package ar.edu.itba.ss;

import java.util.*;

public class CellIndexMethod{

    private int matrixSizeRows;
    private int matrixSizeColumns;
    private int numberOfCells;
    private double cellLength;
    private HashMap<Integer, List<Pedestrian>> cells = new HashMap<>();
    Set<Pedestrian> pedestrians = new HashSet<>();

    public CellIndexMethod(double width, double height, double radius){
        this.matrixSizeRows = (int) Math.ceil(width / radius);
        this.matrixSizeColumns = (int) Math.ceil(height / radius) + 1;
        this.numberOfCells = this.matrixSizeRows * (this.matrixSizeColumns+1);
        this.cellLength = radius;
        for (int i = 0; i < numberOfCells; i++){
            cells.put(i, new LinkedList<>());
        }
    }

    private Integer findKeyOfParticle(Pedestrian p){
        int cellX = (int) Math.floor(p.position[0] / this.cellLength);
        int cellY = (int) Math.floor(p.position[1] / this.cellLength);
        if (cellX > this.matrixSizeRows || cellX < 0 || cellY >= this.matrixSizeColumns || cellY < 0){
            return null;
        }
        return cellY * this.matrixSizeRows + cellX;
    }

    private List<Integer> findNeighborsIndexCells(Pedestrian p){
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

    public Boolean putParticle(Pedestrian p){
        if (p.cell != null){
            cells.get(p.cell).remove(p);
        }
        Integer key = this.findKeyOfParticle(p);
        if (key != null){
            p.cell = key;
            if (cells.get(key) == null){
                System.err.println(key + " " + p.position[0] + " " + p.position[1]);
            }
            pedestrians.add(p);
            cells.get(key).add(p);
            return true;
        }else{
            return false;
        }
    }

    public void setNeighbors(){

        for (Pedestrian p: this.pedestrians){
            p.neighbors = new HashSet<>();
        }
        for (Pedestrian p : this.pedestrians){
            List<Integer> index = findNeighborsIndexCells(p);
//            List<Pedestrian> neighbors = new LinkedList<>();
            for (Integer i : index){
                List<Pedestrian> pedestrianList = cells.get(i);
//                neighbors.addAll(pedestrianList);
                addNeighbor(p, pedestrianList);
            }

//            addNeighbor(p, neighbors);
        }
    }

    private void addNeighbor(Pedestrian p, List<Pedestrian> pedestrians){
        for (Pedestrian pedestrian : pedestrians){
            if (!pedestrian.equals(p)) {
                try {
                    p.neighbors.add(pedestrian.getClone());
                    if (!pedestrian.isWall){
                        pedestrian.neighbors.add(p.getClone());
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
