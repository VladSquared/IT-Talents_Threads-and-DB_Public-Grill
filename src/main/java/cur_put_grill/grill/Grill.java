package cur_put_grill.grill;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class Grill {
    public static final int CAPACITY_OF_MEAT_CONTAINER = 20;
    public static final int CAPACITY_OF_BREAD_CONTAINER = 30;
    public static final int CAPACITY_OF_GARNISH_CONTAINER = 5000;//in grams

    private static Grill instance;

    private String name;
    private HashMap<MeatType, LinkedList<Meat>> readyMeat;
    private HashMap<BreadType, LinkedList<Bread>> readyBread;
    private HashMap<GarnishType, Integer> readyGarnish;
    private LinkedBlockingQueue<Client> clients;

    private static final Object lockMeat = new Object();
    private static final Object lockBread = new Object();
    private static final Object lockGarnish = new Object();

    public synchronized static Grill getInstance() {
        if (instance == null) {
            try {
                instance = new Grill("Grill-1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private Grill(String name) throws Exception {

        if (name.matches("[a-zA-Z 0-9-]+")) {
            this.name = name;
        } else {
            throw new Exception("Wrong name!");
        }
        //init meat container
        readyMeat = new HashMap<>();
        for (MeatType meatType : MeatType.values()) {
            readyMeat.put(meatType, new LinkedList());
        }

        //init bread container
        readyBread = new HashMap<>();
        for (BreadType breadType : BreadType.values()) {
            readyBread.put(breadType, new LinkedList());
        }

        //init garnish
        readyGarnish = new HashMap<>();
        for (GarnishType garnishType : GarnishType.values()) {
            readyGarnish.put(garnishType, 0);
        }

        this.clients = new LinkedBlockingQueue();

        initStaff();

        //start printing statistics in file
        Thread stats = new Thread(() -> printCurrentState());
        stats.setDaemon(true);
        stats.start();

        //start print statistics from DB
        Thread statsFromDB = new Thread(()->GrillDAO.printStatisticsFromDB());
        statsFromDB.setDaemon(true);
        statsFromDB.start();

    }

    void initStaff() {
        BreadChief breadChief = new BreadChief();
        breadChief.start();

        GarnishChief garnishChief = new GarnishChief();
        garnishChief.start();

        GrillChief grillChief = new GrillChief();
        grillChief.start();

        Cashier cashier = new Cashier();
        cashier.start();
    }

    int makeBread() {  //returns a millis to sleep
        synchronized (lockBread) {

            //find what to produce
            BreadType lessAmountOfProduct = BreadType.WHITE;
            int lessAmountOfCount = readyBread.get(BreadType.WHITE).size();
            for (Map.Entry<BreadType, LinkedList<Bread>> bread : readyBread.entrySet()) {
                if (bread.getValue().size() < lessAmountOfCount) {
                    lessAmountOfProduct = bread.getKey();
                    lessAmountOfCount = bread.getValue().size();
                }
            }

            //in case all containers are full
            if (lessAmountOfCount == CAPACITY_OF_BREAD_CONTAINER) {
                try {
                    lockBread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 0; //0 millis to sleep -> to check again
            }

            //produce a product
            readyBread.get(lessAmountOfProduct).offer(new Bread(lessAmountOfProduct));

            lockBread.notifyAll();

            //return time to sleep
            return lessAmountOfProduct.getTimeInSeconds() * 1000;

        }
    }

    void getABread(BreadType breadType) {
        synchronized (lockBread) {
            //check available
            while (true) {
                if (readyBread.get(breadType).size() == 0) {
                    try {
                        lockBread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }

            //reduce quantity
            readyBread.get(breadType).poll();

            lockBread.notifyAll();
        }
    }

    int makeMeat() {  //returns a millis to sleep
        synchronized (lockMeat) {

            //find what to produce
            MeatType lessAmountOfProduct = MeatType.KIUFTE;
            int lessAmountOfCount = readyMeat.get(MeatType.KIUFTE).size();
            for (Map.Entry<MeatType, LinkedList<Meat>> meat : readyMeat.entrySet()) {
                if (meat.getValue().size() < lessAmountOfCount) {
                    lessAmountOfProduct = meat.getKey();
                    lessAmountOfCount = meat.getValue().size();
                }
            }

            //in case all containers are full
            if (lessAmountOfCount == CAPACITY_OF_MEAT_CONTAINER) {
                try {
                    lockMeat.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 0; //0 millis to sleep -> to check again
            }

            //produce a product
            readyMeat.get(lessAmountOfProduct).offer(new Meat(lessAmountOfProduct));

            lockMeat.notifyAll();

            //return time to sleep
            return lessAmountOfProduct.getTimeInSeconds() * 1000;

        }
    }

    void getAMeat(MeatType meatType) {
        synchronized (lockMeat) {
            //check available
            while (true) {
                if (readyMeat.get(meatType).size() == 0) {
                    try {
                        lockMeat.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }

            //reduce quantity
            readyMeat.get(meatType).poll();

            lockMeat.notifyAll();
        }
    }

    int makeGarnish() {  //returns a millis to sleep
        synchronized (lockGarnish) {

            //find what to produce
            GarnishType lessAmountOfProduct = GarnishType.RUSSIAN;
            Integer lessAmountOfCount = readyGarnish.get(GarnishType.RUSSIAN);
            for (Map.Entry<GarnishType, Integer> garnish : readyGarnish.entrySet()) {
                if (garnish.getValue() < lessAmountOfCount) {
                    lessAmountOfProduct = garnish.getKey();
                    lessAmountOfCount = garnish.getValue();
                }
            }

            //in case all containers are full
            if (lessAmountOfCount == CAPACITY_OF_GARNISH_CONTAINER) {
                try {
                    lockGarnish.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 0; //0 millis to sleep -> to check again
            }

            //produce a product
            Integer newQuantity = readyGarnish.get(lessAmountOfProduct) + GarnishChief.PRODUCED_QUANTITY_IN_GRAMS;
            readyGarnish.put(lessAmountOfProduct, newQuantity);

            lockGarnish.notifyAll();

            //return time to sleep
            return lessAmountOfProduct.getTimeInSeconds() * 1000;

        }
    }

    void get200GramsGarnish(GarnishType garnishType) {
        synchronized (lockGarnish) {
            //check available
            while (true) {
                if (readyGarnish.get(garnishType) < Cashier.PORTION_GARNISH) {
                    try {
                        lockGarnish.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }

            //reduce quantity
            Integer reducedQuantity = readyGarnish.get(garnishType) - Cashier.PORTION_GARNISH;
            readyGarnish.put(garnishType, reducedQuantity);

            lockGarnish.notifyAll();
        }
    }

    Client getOrder() {//TODO unmodifiable
        Client client = null;
        try {
            client = clients.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return client;
    }

    public void order(Client client) {

        try {
            clients.put(client);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void printCurrentState() {
        while (true) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(
                    "Current grill state: meat - " +
                            readyMeat.get(MeatType.KIUFTE).size() + "/" + CAPACITY_OF_MEAT_CONTAINER + ", " +
                            readyMeat.get(MeatType.PLESKAVICA).size() + "/" + CAPACITY_OF_MEAT_CONTAINER + ", " +
                            readyMeat.get(MeatType.STEAK).size() + "/" + CAPACITY_OF_MEAT_CONTAINER + ", " +
                            "bread - " +
                            readyBread.get(BreadType.WHITE).size() + "/" + CAPACITY_OF_BREAD_CONTAINER + ", " +
                            readyBread.get(BreadType.WHOLE_GRAIN).size() + "/" + CAPACITY_OF_BREAD_CONTAINER + ", " +
                            "garnish - " +
                            readyGarnish.get(GarnishType.RUSSIAN) + "/" + CAPACITY_OF_GARNISH_CONTAINER + ", " +
                            readyGarnish.get(GarnishType.SNOW_WHITE) + "/" + CAPACITY_OF_GARNISH_CONTAINER + ", " +
                            readyGarnish.get(GarnishType.LIUTEBUTSA) + "/" + CAPACITY_OF_GARNISH_CONTAINER + ", " +
                            readyGarnish.get(GarnishType.CABBAGE_ADN_CARROTS) + "/" + CAPACITY_OF_GARNISH_CONTAINER +
                            ". Clients on queue - " + clients.size()
            );

        }
    }
}