package com.example.models.gameModels;

import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

@Embeddable
public class OwnedTransport {
    @OneToOne
    private Player aOwner;
    private Unit.Transport aUnit;

    public OwnedTransport(Player pOwner, Unit.Transport pUnit){
        aOwner= pOwner;
        aUnit = pUnit;
    }

    public OwnedTransport(){}

    public Player getOwner(){
        return aOwner;
    }
    public  Unit.Transport getUnit() { return  aUnit; }
}
