package luckyclient.publicclass;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改
 * 有任何疑问欢迎联系作者讨论。 QQ:1573584944  seagull1985
 * =================================================================
 * @ClassName: DBOperation 
 * @Description: 封装自动化过程中，对数据库的部分操作
 * @author： seagull
 * @date 2014年8月24日 上午9:29:40  
 * 
 */
public class DBOperation {
		
	DBToolkit dbt =null;
	/**
	 * 创建链接池，注意此方法不能new多次，会导致多次创建链接池，最好放在任务启动方法中
	 */
	public DBOperation(String urlBase,String usernameBase,String passwordBase) {
	 dbt = new DBToolkit(urlBase,usernameBase,passwordBase);
	}
	
	
	/**
	 * 执行SQL
	 * @param request
	 * @param response
	 * @throws Exception 
	 */
	public String executeSql(String sql) throws Exception{
		Connection conn = null;
		String result;
		try{
			conn = dbt.getBaseConnection();
			int resultnum = DBToolkit.executeSQL(conn, sql);
			if(resultnum>0){
				result= "成功执行SQL,更新数据"+resultnum+"行！";
			}else{
				result= "成功执行SQL,没有更新到数据！";
			}
			return result;
		}catch(Exception e){
			return e.toString();
		}finally{
			DBToolkit.closeConnection(conn);
		}
	}
	
	/**
	 * 执行SQL流水查询
	 * @param request
	 * @param response
	 * @throws SQLException 
	 */
	public String executeQuery(String sql) throws Exception{
		Connection conn = null;
		ResultSet rs=null;
		try{
			conn = dbt.getBaseConnection();
			StringBuffer  sb = new StringBuffer();
			rs = DBToolkit.executeQuery(conn, sql);
			ResultSetMetaData metaData = rs.getMetaData();
			int colum = metaData.getColumnCount(); 
			int count=0;
			//行数
			while(rs.next()){    
				count++;
				if (count > 1){
				    sb.append("#");
					}
				//列数
				for (int i = 1; i <= colum; i++){    
					if(rs.getObject(metaData.getColumnName(i))== null){
						sb.append("null").append("%");
						continue;
					}
					sb.append(rs.getObject(metaData.getColumnName(i)).toString()).append("%");
				}
/*				if(DBOperation.sumString(sb.toString(), "%")>500){
					sb.delete(0,sb.length());
					sb.append("查询出来的数据太多啦(超过100项)！我显示不过来哦。。。。");
					break;
				}*/
			}
			return sb.toString();
		}catch(Exception e){
			throw e;
		}finally{
			if(rs!=null){
				rs.close();
			}
			DBToolkit.closeConnection(conn);
		}		
	}
	
	
    /**
     * 
     * @Title: subString 
     * @Description: 截取字符串
     * @return String 
     * @throws
     */
	public String subString(String str,String begin,String end){
		try{
			return str.substring(str.indexOf(begin)+begin.length(), str.lastIndexOf(end));
		}
		catch (Exception e) {
            return null;
        }
	}
	
	
    /**
     * 
     * @Title: sumString 
     * @Description: 统计字符在字符串中出现的次数
     * @return int
     * @throws
     */
	public static int sumString(String str,String a){
		        char chs[]=a.toCharArray();
				int num = 0;
				char[] chars = str.toCharArray();
				for(int i = 0; i < chars.length; i++){
				    if(chs[0] == chars[i])
				    {
				       num++;
				    }
				}
				return num;
	}

	   /**
	 * @throws InterruptedException 
     * 
     * @Title: Wait 
     * @Description: 等待时间
     * @return int
     * @throws
     */
	public static void stepWait(String s) throws InterruptedException{
		int second = Integer.parseInt(s);
		Thread.sleep(second*1000);
	}
	
}
