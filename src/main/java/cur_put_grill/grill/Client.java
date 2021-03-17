package cur_put_grill.grill;

import cur_put_grill.util.Randomizer;

public class Client extends Thread{
    private int id;
    private MeatType meatType;
    private GarnishType garnishType;
    private BreadType breadType;

    public Client(int id){
        this.id = id;
        this.meatType = MeatType.values()[Randomizer.randomInt(0, MeatType.values().length)];
        this.breadType = BreadType.values()[Randomizer.randomInt(0, BreadType.values().length)];
        this.garnishType = GarnishType.values()[Randomizer.randomInt(0, GarnishType.values().length)];
    }

    private void orderFood(){
        Grill.getInstance().order(this);
    }

    @Override
    public void run() {
        orderFood();
    }

    public int getClientId() {
        return id;
    }

    public MeatType getMeatType() {
        return meatType;
    }

    public GarnishType getGarnishType() {
        return garnishType;
    }

    public BreadType getBreadType() {
        return breadType;
    }
}
