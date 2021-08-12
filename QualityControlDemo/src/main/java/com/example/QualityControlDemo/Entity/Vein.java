package com.example.QualityControlDemo.Entity;

public class Vein {
    public SimpleTemplate door;
    public SimpleTemplate mesenteryVein;
    public String cancerHydrant;
    public String veinSideLoop;
    public Vein() {

    }

    public void setVeinSideLoop(String veinSideLoop) {
        this.veinSideLoop = veinSideLoop;
    }

    public void setCancerHydrant(String cancerHydrant) {
        this.cancerHydrant = cancerHydrant;
    }

    public void setDoor(SimpleTemplate door) {
        this.door = door;
    }

    public void setMesenteryVein(SimpleTemplate mesenteryVein) {
        this.mesenteryVein = mesenteryVein;
    }
}
