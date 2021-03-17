package cur_put_grill.grill;

import com.google.gson.Gson;
import cur_put_grill.util.DBConnector;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Scanner;

public class GrillDAO {

    static Connection connection = DBConnector.getInstance().getConnection();

    private static class Order{
         int id;
         MeatType meatType;
         GarnishType garnishType;
         BreadType breadType;
         double price;
         LocalDateTime time;
    }

    static void saveOrderInFile(Client client){

        File dir = new File("dailyOrders");
        if(!dir.exists()) {
            dir.mkdir();
        }

        try(Writer statFile = new BufferedWriter(new FileWriter("dailyOrders/Statistics.txt", true));) {

            Order orderClass = new Order();
            orderClass.id = client.getClientId();
            orderClass.meatType = client.getMeatType();
            orderClass.breadType = client.getBreadType();
            orderClass.garnishType = client.getGarnishType();
            orderClass.price = client.getBreadType().getPrice() + client.getGarnishType().getPrice() + client.getMeatType().getPrice();
            orderClass.time = LocalDateTime.now();

            Gson gson = new Gson();
            String line = gson.toJson(orderClass) + "\n";

            statFile.append(line);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void saveOrderInDB(){
        File file = new File("dailyOrders/Statistics.txt");
        try {
            Scanner sc = new Scanner(file);
            while(sc.hasNext()){
                String dbEntry = sc.nextLine();
                Gson gson = new Gson();
                Order orderObject = gson.fromJson(dbEntry, Order.class);

                String sql = "INSERT INTO sales(shop_id, bread_type_id, meat_type_id, garnish_type_id, date_created) VALUES(10, ?,?,?,?)";

                try(PreparedStatement ps = connection.prepareStatement(sql)){
                    ps.setInt(1, orderObject.breadType.getIdInDB());
                    ps.setInt(2, orderObject.meatType.getIdInDB());
                    ps.setInt(3, orderObject.garnishType.getIdInDB());
                    ps.setTimestamp(4, Timestamp.valueOf(orderObject.time));
                    ps.execute();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Problem with reading the stats file - " + e.getMessage());
        }
    }

}
