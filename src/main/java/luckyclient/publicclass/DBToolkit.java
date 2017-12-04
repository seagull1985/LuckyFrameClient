package luckyclient.publicclass;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.mchange.v2.c3p0.ComboPooledDataSource;
/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: DBToolkit 
 * @Description: 创建与关闭数据库链接
 * @author： seagull
 * @date 2014年8月24日 上午9:29:40  
 * 
 */
public class DBToolkit { 
    /** 
     * 建立数据库链接池
     */ 
	public ComboPooledDataSource cpds=null;
	
	public DBToolkit(String urlBase,String usernameBase,String passwordBase){
		cpds=new ComboPooledDataSource();  
        cpds.setUser(usernameBase);  
        cpds.setPassword(passwordBase);  
        cpds.setJdbcUrl(urlBase);  
        try {  
            cpds.setDriverClass("com.mysql.cj.jdbc.Driver");  
        } catch (PropertyVetoException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        cpds.setInitialPoolSize(20);  
        cpds.setMaxIdleTime(20);  
        cpds.setMaxPoolSize(30);  
        cpds.setMinPoolSize(1);  	
	}
	
    static { 
    	//注册驱动类 
        try { 
                Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) { 
                e.printStackTrace();
        } 
} 
    
	public  Connection getBaseConnection() throws SQLException{
		// TODO Auto-generated method stub       
        Connection conn = cpds.getConnection();
        return conn; 
	}

    /** 
     * 在一个数据库连接上执行一个静态SQL语句查询 
     * 
     * @param conn            数据库连接 
     * @param staticSql 静态SQL语句字符串 
     * @return 返回查询结果集ResultSet对象 
     * @throws SQLException 
     */ 
    public static ResultSet executeQuery(Connection conn, String staticSql) throws SQLException { 
    	//创建执行SQL的对象 
            Statement stmt = conn.createStatement(); 
            
          //执行SQL，并获取返回结果 
            ResultSet rs = stmt.executeQuery(staticSql); 
           // stmt.close();
            return rs; 
    } 

    /** 
     * 在一个数据库连接上执行一个静态SQL语句 
     * 
     * @param conn数据库连接 
     * @param staticSql 静态SQL语句字符串 
     * @throws SQLException 
     */ 
    public static int executeSQL(Connection conn, String staticSql) throws SQLException { 
    	//创建执行SQL的对象 
                    Statement stmt = conn.createStatement(); 
                  //执行SQL，并获取返回结果  
                     stmt.execute(staticSql); 
                     return stmt.getUpdateCount();
           
    } 

    /** 
     * 在一个数据库连接上执行一批静态SQL语句 
     * 
     * @param conn        数据库连接 
     * @param sqlList 静态SQL语句字符串集合 
     */ 
    public static void executeBatchSQL(Connection conn, List<String> sqlList) { 
            try { 
            	 //创建执行SQL的对象 
                    Statement stmt = conn.createStatement(); 
                    for (String sql : sqlList) { 
                            stmt.addBatch(sql); 
                    } 
                  //执行SQL，并获取返回结果 
                    stmt.executeBatch(); 
            } catch (SQLException e) { 
                    e.printStackTrace();
            } 
    } 

    public static void closeConnection(Connection conn) { 
            if (conn == null){
            	return;
            } 
            try { 
                if (!conn.isClosed()) { 
                	  //关闭数据库连接 
                        conn.close(); 
                } 
            } catch (SQLException e) { 
                    e.printStackTrace();
            } 
    } 
}

