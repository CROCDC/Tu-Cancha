/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps.tucancha.connector;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author EducaciónIT
 */
public class Connector {
    private static String driver = "com.mysql.jdbc.Driver";
    private static String vendor = "mysql";
    private static String server = "190.61.250.130";
    private static String port = "3306";
    private static String db = "admini16_CamiloRomero";
    private static String params = "";
    private static String user = "admini16_Camilo";
    private static String pass = "Monroe4500";

    private static String url = "jdbc:" + vendor + "://" + server + ":" + port + "/" + db + params;

    private static Connection conn = null;

    private Connector() {

    }


    public synchronized static Connection getConection() {

        if (conn == null) {
            try {
                Class.forName(driver);
                conn = DriverManager.getConnection(url, user, pass);


            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return conn;
    }


}
