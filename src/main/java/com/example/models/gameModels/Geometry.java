package com.example.models.gameModels;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by G on 17/02/27.
 */
@Entity
public abstract class Geometry {
    public Geometry(){}
    @Id
    protected String aId; // should be private final
    protected String Prefix;
    protected int X;
    protected int Y;

    protected Geometry(String pId){
        aId = pId;
        String[] splitId = pId.split("_");
        Prefix = splitId[0];
        X = Integer.parseInt(splitId[1]);
        Y = Integer.parseInt(splitId[2]);
    }

    @OneToMany
    protected List<Hex> HexNeighbours = new LinkedList<Hex>();
    @OneToMany
    protected List<Intersection> IntersectionNeighbours = new LinkedList<Intersection>();
    @OneToMany
    protected List<Edge> EdgeNeighbours = new LinkedList<Edge>();

    public String getId(){return aId;};

    public String getPrefix(){
        return Prefix;
    }

    public int getX(){
        return X;
    }

    public int getY(){
        return Y;
    }

    public List<Edge> getEdgeNeighbours(){return EdgeNeighbours;};
    public List<Intersection> getIntersectionNeighbours(){return IntersectionNeighbours;};
    public List<Hex> getHexNeighbours(){return HexNeighbours;};

    public abstract void setEdgeNeighbours(Map<String,Edge> pEdges);
    public abstract void setHexNeighbours(Map<String,Hex> pHexes);
    public abstract void setIntersectionNeighbours(Map<String,Intersection> pIntersections);
}
