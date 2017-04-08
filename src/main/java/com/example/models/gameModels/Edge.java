package com.example.models.gameModels;


import javax.persistence.Entity;
import java.util.Map;

/**
 * Created by G on 17/02/27.
 */
@Entity
public class Edge extends Geometry{

    public Edge(){}

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public OwnedTransport getaTransport() {
        return aTransport;
    }

    public void setaTransport(OwnedTransport aTransport) {
        this.aTransport = aTransport;
    }

    private boolean isOccupied;
    private OwnedTransport aTransport;

    public Edge(String pId)
    {
        super(pId);
        isOccupied = false;
    }

    public boolean getOccupancyFlag()
    {
       return isOccupied;
    }

    public void setOccupancyFlag(boolean p){isOccupied = p;}

    public OwnedTransport getTransport(){
        return aTransport;
    }

    public void setTransport(OwnedTransport pTransport)
    {
        aTransport = pTransport;
        isOccupied = true;
    }

    public OwnedTransport removeTransport()
    {
        OwnedTransport tempUnit = aTransport;
        aTransport = null;
        isOccupied = false;
        return tempUnit;
    }

    @Override
    public void setEdgeNeighbours(Map<String,Edge> pEdges){
        final int x = getX();
        final int xm = x-1;
        final int xp = x+1;

        final int y = getY();
        final int ym = y-1;
        final int yp = y+1;
        String id1;
        String id2;
        String id3;
        String id4;

        switch (getPrefix()){
            case "e1": id2 = "e2_"+x+"_"+y;
                id1 = "e2_"+x+"_"+ym;
                id3 = "e3_"+x+"_"+ym;
                id4 = "e3_"+xm+"_"+y;
                break;

            case "e2":  id2 = "e1_"+x+"_"+y;
                id1 = "e1_"+x+"_"+yp;
                id3 = "e3_"+x+"_"+y;
                id4 = "e3_"+xm+"_"+y;
                break;

            case "e3":  id2 = "e2_"+x+"_"+y;
                id1 = "e2_"+xp+"_"+y;
                id3 = "e1_"+x+"_"+yp;
                id4 = "e1_"+xp+"_"+y;
                break;

            default: id2 = "blah";
                id1 = "blah";
                id3 = "blah";
                id4 = "blah";
                //will return null
        }
          //  System.out.println("THIS: "+getId());

        if(pEdges.get(id1)!=null) {
            System.out.println(id1);
            EdgeNeighbours.add(pEdges.get(id1));
        }
        if(pEdges.get(id2)!=null){
            System.out.println(id2);
            EdgeNeighbours.add(pEdges.get(id2));
        }
        if(pEdges.get(id3)!=null) {
            System.out.println(id3);
            EdgeNeighbours.add(pEdges.get(id3));
        }
        if(pEdges.get(id4)!=null) {
            System.out.println(id4);
            EdgeNeighbours.add(pEdges.get(id4));
        }

    }
    @Override
    public void setHexNeighbours(Map<String,Hex> pHexes){
        final int x = getX();
        final int xm = x-1;
        final int xp = x+1;

        final int y = getY();
        final int ym = y-1;
        final int yp = y+1;

        String id1 = "h_"+x+"_"+y;
        String id2;
        switch (getPrefix()){
            case "e1": id2 = "h_"+xm+"_"+y; break;

            case "e2": id2 = "h_"+xm+"_"+yp; break;

            case "e3": id2 = "h_"+x+"_"+yp; break;

            default: id2 = "blah"; //will return null
        }
        if(pHexes.get(id1)!=null)
            HexNeighbours.add(pHexes.get(id1));
        if(pHexes.get(id2)!=null)
            HexNeighbours.add(pHexes.get(id2));
    }

    @Override
    public void setIntersectionNeighbours(Map<String,Intersection> pIntersections) {
        final int x = getX();
        final int xm = x-1;
        final int xp = x+1;

        final int y = getY();
        final int ym = y-1;
        final int yp = y+1;

        String id1;
        String id2;
        switch (getPrefix()){
            case "e1": id2 = "i3_"+x+"_"+y;
                       id1 = "i4_"+x+"_"+y;
                       break;

            case "e2": id2 = "i3_"+x+"_"+y;
                       id1 = "i4_"+x+"_"+yp;
                       break;

            case "e3": id2 = "i3_"+xp+"_"+y;
                       id1 = "i4_"+x+"_"+yp;
                       break;
            default: id2 = "blah";
                     id1 = "blah";   //will return null
        }
        if(pIntersections.get(id1)!=null)
            IntersectionNeighbours.add(pIntersections.get(id1));
        if(pIntersections.get(id2)!=null)
            IntersectionNeighbours.add(pIntersections.get(id2));
    }
}
