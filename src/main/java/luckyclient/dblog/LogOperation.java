package luckyclient.dblog;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.PropertyConfigurator;

import luckyclient.publicclass.DBOperation;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 此测试框架主要采用testlink做分层框架，负责数据驱动以及用例管理部分，有任何疑问欢迎联系作者讨论。
 * QQ:24163551 seagull1985
 * =================================================================
 * @ClassName: LogOperation 
 * @Description: 日志写入数据库
 * @author： seagull
 * @date 2015年4月15日 上午9:29:40  
 * 
 */
public class LogOperation {
	public static DBOperation dbt = DbLink.DbLogLink();
	static int exetype = DbLink.exetype;
   
	/*	
	 * 插入用例执行状态
	 * casestatus   pass:0    fail:1   lock:2   unexecute:4
	 */
	public  void AddCaseDetail(String taskid,String caseno,String caseversion,String casename,Integer casestatus){
		if(0 == exetype){
				int taskidtoint = Integer.parseInt(taskid);
				casename = casename.replace("'", "''");
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
				String sql = "Insert into test_casedetail(TASKID,CASENO,CASEVERSION,CASETIME,"
						+ "CASENAME,CASESTATUS) Values ("+taskidtoint+",'"+caseno+"','"+caseversion+"',"
						+ "str_to_date('"+df.format(new Date())+"','%Y-%m-%d %T'),'"+casename+"','"+casestatus+"')";
				try {
					dbt.executeSql(sql);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	/*	
	 * 更新用例执行状态
	 * casestatus   pass:0    fail:1   lock:2   unexecute:4
	 */
	public  void UpdateCaseDetail(String taskid,String caseno,Integer casestatus){
		if(0 == exetype){
				int taskidtoint = Integer.parseInt(taskid);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
				String sql = "update test_casedetail set casestatus = '"+casestatus+"',CASETIME = str_to_date('"+df.format(new Date())+"','%Y-%m-%d %T')"+" where taskid = "+taskidtoint+" and caseno = '"+caseno+"'";
				try {
					dbt.executeSql(sql);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}
	
	/*	
	 * 插入用例执行日志
	 */
	public  void CaseLogDetail(String taskid,String caseno,String detail,String loggrade,String step,String imgname)  {
		if(0 == exetype){
			if(detail.indexOf("'")>-1){
				detail = detail.replaceAll("'", "''");
			}
		int taskidtoint = Integer.parseInt(taskid);
		String sqlresult = null;
		try {
			sqlresult = dbt.executeQuery("select id from test_casedetail where taskid = "+taskidtoint+" and caseno = '"+caseno+"' order by id desc");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
			if (!"".equals(sqlresult) && null != sqlresult) {
				int caseid = Integer.parseInt(sqlresult.substring(0, sqlresult.indexOf("%"))); // 取其中最近一条数据做为CASEID
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 设置日期格式
				String sql = "Insert into test_logDetail(LOGTIME,TASKID,CASEID,DETAIL,LOGGRADE,STEP,IMGNAME)  "
						+ "Values (str_to_date('" + df.format(new Date())
						+ "','%Y-%m-%d %T')," + taskidtoint + "," + caseid + ",'" + detail + "','" + loggrade
						+ "','" + step + "','"+ imgname +"')";
				try {
					String re=dbt.executeSql(sql);
					if(re.indexOf("成功")<0){
						throw new Exception("更新用例："+caseno+"步骤"+step+"日志到数据库中出现异常！！！");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		
		}
	}
	
	/*	
	 *更新本次任务的执行统计情况
	 */
	public static int[] UpdateTastdetail(String taskid,int casecount){
		int[] taskcount = null;
		if(0 == exetype){
			 try {
				 int id = Integer.parseInt(taskid);
				 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
				 
				 String casesucsql = dbt.executeQuery("select count(*) from test_casedetail where taskid = "+id+" and casestatus = 0");
				 String casefailsql = dbt.executeQuery("select count(*) from test_casedetail where taskid = "+id+" and casestatus = 1");
				 String caselocksql = dbt.executeQuery("select count(*) from test_casedetail where taskid = "+id+" and casestatus = 2");
				 String casenoexesql = dbt.executeQuery("select count(*) from test_casedetail where taskid = "+id+" and casestatus = 4");
				 
				 
				int casesuc =  Integer.parseInt(casesucsql.substring(0, casesucsql.indexOf("%")));
				int casefail =  Integer.parseInt(casefailsql.substring(0, casefailsql.indexOf("%")));
				int caselock =  Integer.parseInt(caselocksql.substring(0, caselocksql.indexOf("%")));
				int casenoexec =  Integer.parseInt(casenoexesql.substring(0, casenoexesql.indexOf("%")));
				
				if(casecount==0){
					casecount = casesuc+casefail+caselock+casenoexec;
				}
				
				taskcount = new int[5];       //返回本次任务执行情况
				taskcount[0] = casecount;
				taskcount[1] = casesuc;
				taskcount[2] = casefail;
				taskcount[3] = caselock;
				taskcount[4] = casenoexec;
				
				String sql = "update test_TaskExcute set casetotal_count = "+casecount+",casesucc_count = "+casesuc+",casefail_count = "+casefail
						+",caselock_count = "+caselock+",casenoexec_count = "+casenoexec+",finishtime =  str_to_date('"+df.format(new Date())+"','%Y-%m-%d %T'), "
								+ "taskStatus  = 2 where id = "+id;
				dbt.executeSql(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
		return taskcount;
	}
	
	/*	
	 *更新本次任务的执行状态
	 */
	public  static void UpdateTastStatus(String taskid,int casecount){
		if(0 == exetype){
			 try {	
				 int id = Integer.parseInt(taskid);
				String sql = "update test_TaskExcute set casetotal_count= "+casecount+",taskStatus  = 1 where id = "+id;
				
				dbt.executeSql(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
		}
	}
	
	/*	
	 *更新本次任务的单条用例执行日志
	 */
	public  static void UpdateCaseLogDetail(String caseno,String taskid,String detail,String loggrade,String step) {
		try {
		if(detail.indexOf("'")>-1){
			detail = detail.replaceAll("'", "''");
		}
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql;
		casesidsql = dbt.executeQuery("select id from TEST_CASEDETAIL t where caseno = '"+caseno+"' and taskid = "+inttaskid);
		int casesid =  Integer.parseInt(casesidsql.substring(0, casesidsql.indexOf("%")));
				
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  //设置日期格式
		String sql = "Insert into test_logDetail(LOGTIME,TASKID,CASEID,DETAIL,LOGGRADE,STEP,IMGNAME)  "
				+ "Values (str_to_date('"+df.format(new Date())+"','%Y-%m-%d %T'),"+inttaskid+","
				+casesid+",'"+detail+"','"+loggrade+"','"+step+"','')";
		
		String re = dbt.executeSql(sql);
		if(re.indexOf("成功")<0){
			throw new Exception("更新用例："+caseno+"步骤"+step+"日志到数据库中出现异常！！！");
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*	
	 * 删除单次任务指定的用例日志明细
	 */
	public static void DeleteCaseLogDetail(String caseno,String taskid){
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql;
		try {
		casesidsql = dbt.executeQuery("select id from TEST_CASEDETAIL t where caseno = '"+caseno+"' and taskid = "+inttaskid);
		int casesid =  Integer.parseInt(casesidsql.substring(0, casesidsql.lastIndexOf("%")));
		dbt.executeSql("delete from TEST_LOGDETAIL where caseid = "+casesid+" and taskid = "+inttaskid);     //删除原来的日志
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*	
	 * 删除单次任务指定的用例明细
	 */
	public static void DeleteCaseDetail(String caseno, String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		try {
			dbt.executeSql("delete from TEST_CASEDETAIL where caseno = '" + caseno + "' and taskid = " + inttaskid); // 删除原来的用例
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*	
	 * 取出指定任务ID中的不属于成功状态的用例编写以及版本号
	 */
	public  String UnSucCaseUpdate(String taskid){
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql = null;
		try {
			casesidsql = dbt.executeQuery("select caseno,caseversion from TEST_CASEDETAIL t where t.taskid = "+inttaskid+" and t.casestatus <> 0");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return casesidsql;
	}
	
	/*	
	 * 取出指定任务ID中所属的调度是否要发送邮件状态及收件人地址
	 *  isSendMail varchar(1)  default(0);  --0 不发送 1 发送
         eMailer varchar(100)   ;  --收件人
	 */
	@SuppressWarnings("finally")
	public static String[] GetEmailAddress(String taskid){
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql = null;
		String address[] = null;
		try {
			casesidsql = dbt.executeQuery("select t.issendmail,t.emailer from TEST_JOBS t where id in (select jobid from TEST_TASKEXCUTE t where t.id = "+inttaskid+")");
			String status = casesidsql.substring(0, casesidsql.indexOf("%"));			
			if(status.equals("1")){
				String temp = casesidsql.substring(casesidsql.indexOf("%")+1,casesidsql.length()-1);				
				if(temp.indexOf(";")>-1&&temp.substring(temp.length()-1, temp.length()).indexOf(";")>-1){           //清除最后一个;
					temp = temp.substring(0, temp.length()-1);
				}
				if(temp.indexOf("null")<=-1&&temp.indexOf(";")>-1){   //多个地址
					address=temp.split(";",-1);
				}else if(temp.indexOf("null")<=-1&&temp.indexOf(";")<=-1){   //一个地址
				   address = new String[1];
				   address[0] = temp;
				}				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return address;
		}
	}
	
	/*	
	 * 取出指定任务ID中所属的调度是否要自动构建以及构建的项目名称
	 *  isBuilding varchar(1)  default(0);  --0 不自动构建 1 自动构建
         BuildName varchar(100)   ;  --构建项目名称
	 */
	@SuppressWarnings("finally")
	public static String[] GetBuildName(String taskid){
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql = null;
		String buildname[] = null;
		try {
			casesidsql = dbt.executeQuery("select t.isbuilding,t.buildname from TEST_JOBS t where id in (select jobid from TEST_TASKEXCUTE t where t.id = "+inttaskid+")");
			String status = casesidsql.substring(0, casesidsql.indexOf("%"));			
			if(status.equals("1")){
				String temp = casesidsql.substring(casesidsql.indexOf("%")+1,casesidsql.length()-1);				
				if(temp.indexOf(";")>-1&&temp.substring(temp.length()-1, temp.length()).indexOf(";")>-1){           //清除最后一个;
					temp = temp.substring(0, temp.length()-1);
				}
				if(temp.indexOf("null")<=-1&&temp.indexOf(";")>-1){   //多个名称
					buildname=temp.split(";",-1);
				}else if(temp.indexOf("null")<=-1&&temp.indexOf(";")<=-1){   //一个名称
					buildname = new String[1];
					buildname[0] = temp;
				}				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return buildname;
		}
	}
	
	/*	
	 * 取出指定任务ID中所属的调度是否要自动重启TOMCAT
	 *  isrestart varchar(1)  default(0);  --0 不自动重启 1 自动重启
        restartcomm varchar(200)   ;  --
                                       格式：服务器IP;服务器用户名;服务器密码;ssh端口;Shell命令;      
                                       例：10.211.19.72;pospsettle;pospsettle;22;cd /home/pospsettle/tomcat-7.0-7080/bin&&./restart.sh;
	 */
	@SuppressWarnings("finally")
	public static String[] Getrestartcomm(String taskid){
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql = null;
		String command[] = null;
		try {
			casesidsql = dbt.executeQuery("select t.isrestart,t.restartcomm from TEST_JOBS t where id in (select jobid from TEST_TASKEXCUTE t where t.id = "+inttaskid+")");
			String status = casesidsql.substring(0, casesidsql.indexOf("%"));			
			if(status.equals("1")){
				String temp = casesidsql.substring(casesidsql.indexOf("%")+1,casesidsql.length()-1);				
				if(temp.indexOf(";")>-1&&temp.substring(temp.length()-1, temp.length()).indexOf(";")>-1){           //清除最后一个;
					temp = temp.substring(0, temp.length()-1);
				}
				if(temp.indexOf("null")<=-1&&temp.indexOf(";")>-1){   //多个名称
					command=temp.split(";",-1);
				}else if(temp.indexOf("null")<=-1&&temp.indexOf(";")<=-1){   //一个名称
					command = new String[1];
					command[0] = temp;
				}				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return command;
		}
	}
	
	
	/*
	 * 获取测试计划名称
	 */
	@SuppressWarnings("finally")
	public static String GetTestPlanName(String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		String testplanname = "NULL";
		try {
			String sql = dbt
					.executeQuery("select t.testlinkname from TEST_JOBS t where id in (select jobid from TEST_TASKEXCUTE t where t.id = "
							+ inttaskid + ")");
			testplanname = sql.substring(0, sql.lastIndexOf("%"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return testplanname;
		}
	}
	
	/*
	 * 获取任务测试时长
	 */
	@SuppressWarnings("finally")
	public static String GetTestTime(String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		String Destime = "计算测试时长出错！";
		try {
			String sql = dbt.executeQuery("select date_format(t.createtime,'%Y-%m-%d %T'),date_format(t.finishtime,'%Y-%m-%d %T') from TEST_TASKEXCUTE t where t.id= "+inttaskid);
			String starttime = sql.substring(0, sql.indexOf("%"));
			String finishtime = sql.substring(sql.indexOf("%")+1, sql.length()-1);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = df.parse(starttime);
			Date finish =df.parse(finishtime);
			long l=finish.getTime()-start.getTime();
			long day=l/(24*60*60*1000);
			long hour=(l/(60*60*1000)-day*24);
			long min=((l/(60*1000))-day*24*60-hour*60);
			long s=(l/1000-day*24*60*60-hour*60*60-min*60);
			Destime = "<font color='#2828FF'>"+hour+"</font>小时<font color='#2828FF'>"+min+"</font>分<font color='#2828FF'>"+s+"</font>秒";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			return Destime;
		}
	}
	
	/*	
	 * 查询web执行，浏览器类型
	 */
	public static int Querydrivertype(String taskid)  {
		int taskidtoint = Integer.parseInt(taskid);
		int drivertype=0;
		try {
			String sqlresult = dbt.executeQuery("select browsertype from test_jobs where id = (select jobid from TEST_TASKEXCUTE where id = "+taskidtoint+")");
			drivertype = Integer.parseInt(sqlresult.substring(0, sqlresult.lastIndexOf("%")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return drivertype;
		}
		return drivertype;
	}
	
	/*	
	 * 查询任务中用例步骤日志执行实际结果
	 */
	public static String getlogdetail_testresult(int taskid,String caseno,int casestatus)  {
		String sqlresult="";
		try {
			sqlresult = dbt.executeQuery("select detail from test_logdetail where logid=(select MIN(logid) from test_logdetail "
					+ "where loggrade='error' and taskid="+taskid+" and caseid=(select id from test_casedetail where taskid="+taskid
					+ " and caseno='"+caseno+"' and casestatus='"+casestatus+"'))");
			if(sqlresult.indexOf("测试结果：")<=0||sqlresult.indexOf("%")<=0){
				return sqlresult;
			}
			sqlresult = sqlresult.substring(sqlresult.indexOf("测试结果：")+5,sqlresult.lastIndexOf("%"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return sqlresult;
		}
		return sqlresult;
	}
	
	/*	
	 * 根据任务名称查询任务ID
	 */
	public static int gettaskexcute_taskid(String taskname)  {
		String sqlresult="";
		try {
			sqlresult = dbt.executeQuery("select id from test_taskexcute t where t.taskid='"+taskname+"'");
			sqlresult = sqlresult.substring(0,sqlresult.lastIndexOf("%"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Integer.parseInt(sqlresult);
		}
		return Integer.parseInt(sqlresult);
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	}

}
