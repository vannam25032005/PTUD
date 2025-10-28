package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {

	
	private static final String url = "jdbc:sqlserver://localhost:1433;databasename=QLNH;encrypt=false";
	private static final String user = "sa";
	private static final String password = "sapassword";

	private static Connection con = null;
	private static ConnectDB instance = new ConnectDB(); 

	public static ConnectDB getInstance() {
		return instance;
	}

	
	public static void connect() {
		try {
			
			if (con == null || con.isClosed()) {
				con = DriverManager.getConnection(url, user, password);
				
			}
		} catch (SQLException e) {
			System.err.println("Lỗi kết nối CSDL: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void disconnect() {
		if (con != null) {
			try {
				con.close();
				con = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static Connection getConnection() {
		 try {
		        if (con == null || con.isClosed()) {
		            connect();
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		        connect();
		    }
		    return con;
	}

}