package cur_put_grill;

import cur_put_grill.grill.Client;
import cur_put_grill.grill.Grill;
import cur_put_grill.grill.GrillDAO;

public class Demo {

    public static void main(String[] args) {
        Grill grill = Grill.getInstance();

        Thread clientThread = new Thread(()->generateClients());
        clientThread.start();

        Thread saveStatsToDB = new Thread(()->saveStatsFromFileToDB());
        saveStatsToDB.setDaemon(true);
        saveStatsToDB.start();
    }

    static void generateClients(){
        int counter = 0;

        while(true){

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Client client = new Client(++counter);
            client.start();
        }
    }

    static void saveStatsFromFileToDB(){
        while(true){
            try {
                Thread.sleep(24000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GrillDAO.saveOrderInDB();
        }
    }
}
