package cur_put_grill.grill;

enum MeatType{
    KIUFTE(1, 2, 1),
    PLESKAVICA(2, 3, 2),
    STEAK(3, 4, 3);

    private double price;
    private int timeInSeconds;
    private int iDinDB;

    MeatType(double price, int timeInSeconds, int dbId){
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

class Meat {
    private MeatType meatType;

    public Meat(MeatType meatType) {
        this.meatType = meatType;
    }

    public MeatType getMeatType() {
        return meatType;
    }
}
