package cur_put_grill.grill;

enum BreadType{
    WHITE(0.9, 2, 1),
    WHOLE_GRAIN(1.4, 4, 2);

    private double price;
    private int timeInSeconds;
    private int iDinDB;

    BreadType(double price, int timeInSeconds, int dbId){
        this.price = price;
        this.timeInSeconds = timeInSeconds;
        this.iDinDB = dbId;
    }

    public double getPrice() {
        return price;
    }

    public int getTimeInSeconds() {
        return timeInSeconds;
    }

    public int getIdInDB() {
        return iDinDB;
    }
}

class Bread {
    private BreadType breadType;

    public Bread(BreadType breadType) {
        this.breadType = breadType;
    }

    public BreadType getBreadType() {
        return breadType;
    }
}
