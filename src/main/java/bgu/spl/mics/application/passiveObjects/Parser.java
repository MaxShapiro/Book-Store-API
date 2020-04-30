package bgu.spl.mics.application.passiveObjects;

public class Parser {

    private BookInventoryInfo[] initialInventory;
    private Vehicles[] initialResources;
    private Services services;

    public BookInventoryInfo[] getInitialInventory() {
        return initialInventory;
    }

    public Vehicles[] getInitialResources() {
        return initialResources;
    }

    public Services getServices() {
        return services;
    }

    public void setBooks(){
        for(int i =0;i<initialInventory.length;i++){
            initialInventory[i].setBook();
        }
    }

}


