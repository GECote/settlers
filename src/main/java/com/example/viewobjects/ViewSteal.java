package com.example.viewobjects;

/**
 * Created by Ming-PC on 4/12/2017.
 */
public class ViewSteal {
    private String intersectionID;
    private boolean isValid;
    public ViewSteal(){
    }

    public String getIntersectionID() {
        return intersectionID;
    }

    public void setIntersectionID(String intersectionID) {
        this.intersectionID = intersectionID;
    }

    public boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(boolean valid) {
        isValid = valid;
    }
}