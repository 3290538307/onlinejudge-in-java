import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.io.File;
import com.alibaba.druid.pool.DruidDataSource;

public class ConnectionFactory {
	private static DruidDataSource dataSource=null;
	static{
		Properties prop=new Properties();
		try {
			System.out.println(new File("..").getCanonicalPath());
			prop.load(new FileInputStream(new File("").getCanonicalPath()+"/config/login.properties"));
			//prop.load(new FileInputStream("/usr/local/QOJ/JAVAProject/QOJ2/config/login.properties"));
			String driver=prop.getProperty("driver");
			String url=prop.getProperty("url");
			String username=prop.getProperty("username");
			String password=prop.getProperty("password");
			dataSource=new DruidDataSource();
			dataSource.setDriverClassName(driver);
			dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setInitialSize(5);
            dataSource.setMinIdle(1);
            dataSource.setMaxActive(10);
            dataSource.setPoolPreparedStatements(false);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static synchronized Connection getConnection(){
		Connection conn=null;
		try {
			conn=dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
}
