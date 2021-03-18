package cur_put_grill.grill;

import com.google.gson.Gson;
import cur_put_grill.util.DBConnector;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;
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
        String sql = "INSERT INTO sales(shop_id, bread_type_id, meat_type_id, garnish_type_id, date_created) VALUES(10, ?,?,?,?)";

        try (
             PreparedStatement ps = connection.prepareStatement(sql);
             Scanner sc = new Scanner(file);
             ){
            connection.setAutoCommit(false);

            while(sc.hasNext()){
                String dbEntry = sc.nextLine();
                Gson gson = new Gson();
                Order orderObject = gson.fromJson(dbEntry, Order.class);

                ps.setInt(1, orderObject.breadType.getIdInDB());
                ps.setInt(2, orderObject.meatType.getIdInDB());
                ps.setInt(3, orderObject.garnishType.getIdInDB());
                ps.setTimestamp(4, Timestamp.valueOf(orderObject.time));
                ps.execute();
            }
            connection.commit();

        } catch (FileNotFoundException e) {
            System.out.println("Problem with reading the stats file - " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Problem with writing in DB - " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        //deleting stats file
        file.delete();
    }

    public static void printStatisticsFromDB(){
        try {
            Thread.sleep(24000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Queue<String> sqlQueries = new LinkedList<>();

        System.out.println("************ Statistics from DB *************");

        sqlQueries.offer("SELECT COUNT(id) AS total_sales_from_my_shop FROM sales WHERE shop_id=10;");

        sqlQueries.offer("SELECT COUNT(meat_type_id) AS `count`, meat_type_id FROM sales \n" +
                "WHERE shop_id=10 \n" +
                "GROUP BY meat_type_id \n" +
                "ORDER BY count DESC LIMIT 1;");

        sqlQueries.offer("SELECT ROUND((SUM(b.price) + SUM(m.price) + SUM(g.price)), 2) AS total_sum FROM sales\n" +
                "JOIN bread_types b\n" +
                "ON(bread_type_id=b.id)\n" +
                "JOIN garnish_types g\n" +
                "ON(meat_type_id=g.id)\n" +
                "JOIN meat_types m\n" +
                "ON(garnish_type_id=m.id)\n" +
                "WHERE shop_id=10; ");

        sqlQueries.offer("SELECT COUNT(garnish_type_id)/5 AS consumed_garnish_In_kilos FROM sales \n" +
                "WHERE shop_id=10;");

        sqlQueries.offer("SELECT ROUND((SUM(b.price) + SUM(m.price) + SUM(g.price)), 2) AS most_profitable_shop FROM sales\n" +
                "JOIN bread_types b\n" +
                "ON(bread_type_id=b.id)\n" +
                "JOIN garnish_types g\n" +
                "ON(meat_type_id=g.id)\n" +
                "JOIN meat_types m\n" +
                "ON(garnish_type_id=m.id)\n" +
                "GROUP BY shop_id\n" +
                "ORDER BY most_profitable_shop DESC\n" +
                "LIMIT 1;");

        sqlQueries.offer("SELECT shop.`name` AS shop_name, COUNT(shop_id) AS sales_count FROM sales\n" +
                "JOIN shops shop\n" +
                "ON(shop_id=shop.id)\n" +
                "GROUP BY shop_id\n" +
                "ORDER BY sales_count DESC;");

        sqlQueries.offer("SELECT shop_id, COUNT(bread_type_id) AS soled_breads_id_2 FROM sales \n" +
                "WHERE bread_type_id=2\n" +
                "GROUP BY shop_id\n" +
                "ORDER BY soled_breads_id_2 DESC\n" +
                "LIMIT 1;");

        sqlQueries.offer("SELECT shop_id, COUNT(meat_type_id) AS sold_meat_type_2 FROM sales\n" +
                "WHERE meat_type_id=2\n" +
                "GROUP BY shop_id\n" +
                "ORDER BY shop_id ASC;");

        sqlQueries.offer("SELECT shop_id, COUNT(garnish_type_id) AS most_sold_garnish, garnish_type_id FROM sales\n" +
                "GROUP BY shop_id, garnish_type_id\n" +
                "ORDER BY most_sold_garnish DESC\n" +
                "LIMIT 1;");

        sqlQueries.offer("SELECT COUNT(garnish_type_id) AS sold_garnish, garnish_type_id FROM sales\n" +
                "WHERE shop_id=10\n" +
                "GROUP BY garnish_type_id\n" +
                "ORDER BY sold_garnish DESC\n" +
                "LIMIT 1");

        while(sqlQueries.peek()!=null){
            try(PreparedStatement ps = connection.prepareStatement(sqlQueries.poll())){
                ResultSet resultSet = ps.executeQuery();
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                while (resultSet.next()) {
                    for (int i = 1; i <= columnsNumber; i++) {
                        if (i > 1) System.out.print(",  ");
                        String columnValue = resultSet.getString(i);
                        System.out.print(rsmd.getColumnName(i) + " : " + columnValue);
                    }
                    System.out.println("");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        System.out.println("*******************************************");

    }

}
