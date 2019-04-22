package luckyclient.dblog;

import luckyclient.publicclass.DBOperation;
import luckyclient.serverapi.api.PostServerAPI;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 seagull1985
 * =================================================================
 * 
 * @ClassName: LogOperation
 * @Description: 日志写入数据库 @author： seagull
 * @date 2015年4月15日 上午9:29:40
 * 
 */
public class LogOperation {
	public static DBOperation dbt = DbLink.dbLogLink();
	static int exetype = DbLink.exetype;

	/**
	 * 插入用例执行状态 0通过 1失败 2锁定 3执行中 4未执行
	 */
	public void insertTaskCaseExecute(String taskIdStr, Integer projectId,Integer caseId,  String caseSign,String caseName, Integer caseStatus) {
		if (0 == exetype) {
			Integer taskId=Integer.valueOf(taskIdStr);
			PostServerAPI.clientPostInsertTaskCaseExecute(taskId, projectId, caseId, caseSign, caseName, caseStatus);
		}
	}

	/**
	 * 更新用例执行状态 0通过 1失败 2锁定 3执行中 4未执行
	 */
	public void updateTaskCaseExecuteStatus(String taskIdStr, Integer caseId, Integer caseStatus) {
		if (0 == exetype) {
			Integer taskId=Integer.valueOf(taskIdStr);
			PostServerAPI.clientUpdateTaskCaseExecuteStatus(taskId, caseId, caseStatus);
		}
	}

	/**
	 * 插入用例执行日志
	 */
	public void insertTaskCaseLog(String taskIdStr, Integer caseId, String logDetail, String logGrade, String logStep,
			String imgname) {
		if (0 == exetype) {
			if (logDetail.length()>5000) {
				 luckyclient.publicclass.LogUtil.APP.info("日志明细超过5000字符，无法进入数据库存储，进行日志明细打印...");
				 luckyclient.publicclass.LogUtil.APP.info("第"+logStep+"步，日志级别"+logGrade+",日志明细【"+logGrade+"】...");
				 logDetail="日志明细超过5000字符无法存入数据库，已在LOG4J日志中打印，请前往查看...";
			}
			
			Integer taskId=Integer.valueOf(taskIdStr);
			PostServerAPI.clientPostInsertTaskCaseLog(taskId, caseId, logDetail, logGrade, logStep, imgname);
		}
	}

	/**
	 * 更新本次任务的执行统计情况
	 */
	public static int[] updateTastdetail(String taskid, int casecount) {
		int[] taskcount = null;
		if (0 == exetype) {
			try {
				int id = Integer.parseInt(taskid);
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				String casesucsql = dbt.executeQuery(
						"select count(*) from test_casedetail where taskid = " + id + " and casestatus = 0");
				String casefailsql = dbt.executeQuery(
						"select count(*) from test_casedetail where taskid = " + id + " and casestatus = 1");
				String caselocksql = dbt.executeQuery(
						"select count(*) from test_casedetail where taskid = " + id + " and casestatus = 2");
				String casenoexesql = dbt.executeQuery(
						"select count(*) from test_casedetail where taskid = " + id + " and casestatus = 4");

				int casesuc = Integer.parseInt(casesucsql.substring(0, casesucsql.indexOf("%")));
				int casefail = Integer.parseInt(casefailsql.substring(0, casefailsql.indexOf("%")));
				int caselock = Integer.parseInt(caselocksql.substring(0, caselocksql.indexOf("%")));
				int casenoexec = Integer.parseInt(casenoexesql.substring(0, casenoexesql.indexOf("%")));

				if (casecount == 0) {
					casecount = casesuc + casefail + caselock + casenoexec;
				}
				// 返回本次任务执行情况
				taskcount = new int[5];
				taskcount[0] = casecount;
				taskcount[1] = casesuc;
				taskcount[2] = casefail;
				taskcount[3] = caselock;
				taskcount[4] = casenoexec;

				String sql = "update test_taskexcute set casetotal_count = " + casecount + ",casesucc_count = "
						+ casesuc + ",casefail_count = " + casefail + ",caselock_count = " + caselock
						+ ",casenoexec_count = " + casenoexec + ",finishtime =  str_to_date('" + df.format(new Date())
						+ "','%Y-%m-%d %T'), " + "taskStatus  = 2 where id = " + id;
				dbt.executeSql(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				luckyclient.publicclass.LogUtil.APP.error("执行更新本次任务执行统计情况SQL出现异常，请确认数据库链接是否正常！", e);
				e.printStackTrace();
			}
		}
		return taskcount;
	}

	/**
	 * 更新本次任务的执行状态
	 */
	public static void updateTastStatus(String taskid, int casecount) {
		if (0 == exetype) {
			try {
				int id = Integer.parseInt(taskid);
				String sql = "update test_taskexcute set casetotal_count= " + casecount + ",taskStatus  = 1 where id = "
						+ id;

				dbt.executeSql(sql);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				luckyclient.publicclass.LogUtil.APP.error("执行更新本次任务的执行状态SQL出现异常，请确认数据库链接是否正常！", e);
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除单次任务指定的用例日志明细
	 */
	public static void deleteCaseLogDetail(String caseno, String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql;
		try {
			casesidsql = dbt.executeQuery(
					"select id from test_casedetail t where caseno = '" + caseno + "' and taskid = " + inttaskid);
			int casesid = Integer.parseInt(casesidsql.substring(0, casesidsql.lastIndexOf("%")));
			// 删除原来的日志
			dbt.executeSql("delete from test_logdetail where caseid = " + casesid + " and taskid = " + inttaskid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行删除单次任务指定的用例日志明细SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
		}
	}

	/**
	 * 删除单次任务指定的用例明细
	 */
	public static void deleteCaseDetail(String caseno, String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		try {
			// 删除原来的用例
			dbt.executeSql("delete from test_casedetail where caseno = '" + caseno + "' and taskid = " + inttaskid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行删除单次任务指定的用例明细SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
		}
	}

	/**
	 * 取出指定任务ID中的不属于成功状态的用例编写以及版本号
	 */
	public String unSucCaseUpdate(String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql = null;
		try {
			casesidsql = dbt.executeQuery("select caseno,caseversion from test_casedetail t where t.taskid = "
					+ inttaskid + " and t.casestatus <> 0");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行取出指定任务ID中的不属于成功状态的用例编写以及版本号SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
		}
		return casesidsql;
	}

	/**
	 * 取出指定任务ID中所属的调度是否要发送邮件状态及收件人地址 isSendMail varchar(1) default(0); --0 不发送 1
	 * 发送 eMailer varchar(100) ; --收件人
	 */

	public static String[] getEmailAddress(String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql = null;
		String[] address = null;
		try {
			casesidsql = dbt.executeQuery(
					"select t.issendmail,t.emailer from test_jobs t where id in (select jobid from test_taskexcute t where t.id = "
							+ inttaskid + ")");
			String status = casesidsql.substring(0, casesidsql.indexOf("%"));
			if ("1".equals(status)) {
				String temp = casesidsql.substring(casesidsql.indexOf("%") + 1, casesidsql.length() - 1);
				// 清除最后一个;
				if (temp.indexOf(";") > -1 && temp.substring(temp.length() - 1, temp.length()).indexOf(";") > -1) {
					temp = temp.substring(0, temp.length() - 1);
				}
				// 多个地址
				if (temp.indexOf("null") <= -1 && temp.indexOf(";") > -1) {
					address = temp.split(";", -1);
					// 一个地址
				} else if (temp.indexOf("null") <= -1 && temp.indexOf(";") <= -1) {
					address = new String[1];
					address[0] = temp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行取出指定任务ID中所属的调度是否要发送邮件状态及收件人地址SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
			return address;
		}
		return address;
	}

	/**
	 * 取出指定任务ID中所属的调度是否要自动构建以及构建的项目名称 isBuilding varchar(1) default(0); --0
	 * 不自动构建 1 自动构建 BuildName varchar(100) ; --构建项目名称
	 */
	public static String[] getBuildName(String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql = null;
		String[] buildname = null;
		try {
			casesidsql = dbt.executeQuery(
					"select t.isbuilding,t.buildname from test_jobs t where id in (select jobid from test_taskexcute t where t.id = "
							+ inttaskid + ")");
			if (null == casesidsql || "".equals(casesidsql)) {
				return buildname;
			}
			String status = casesidsql.substring(0, casesidsql.indexOf("%"));
			if ("1".equals(status)) {
				String temp = casesidsql.substring(casesidsql.indexOf("%") + 1, casesidsql.length() - 1);
				// 清除最后一个;
				if (temp.indexOf(";") > -1 && temp.substring(temp.length() - 1, temp.length()).indexOf(";") > -1) {
					temp = temp.substring(0, temp.length() - 1);
				}
				// 多个名称
				if (temp.indexOf("null") <= -1 && temp.indexOf(";") > -1) {
					buildname = temp.split(";", -1);
					// 一个名称
				} else if (temp.indexOf("null") <= -1 && temp.indexOf(";") <= -1) {
					buildname = new String[1];
					buildname[0] = temp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行取出指定任务ID中所属的调度是否要自动构建以及构建的项目名称SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
			return buildname;
		}
		return buildname;
	}

	/**
	 * 取出指定任务ID中所属的调度是否要自动重启TOMCAT isrestart varchar(1) default(0); --0 不自动重启 1
	 * 自动重启 restartcomm varchar(200) ; -- 格式：服务器IP;服务器用户名;服务器密码;ssh端口;Shell命令;
	 * 例：192.168.222.22;pospsettle;pospsettle;22;cd
	 * /home/pospsettle/tomcat-7.0-7080/bin&&./restart.sh;
	 */

	public static String[] getrestartcomm(String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		String casesidsql = null;
		String[] command = null;
		try {
			casesidsql = dbt.executeQuery(
					"select t.isrestart,t.restartcomm from test_jobs t where id in (select jobid from test_taskexcute t where t.id = "
							+ inttaskid + ")");
			if (null == casesidsql || "".equals(casesidsql)) {
				return command;
			}
			String status = casesidsql.substring(0, casesidsql.indexOf("%"));
			if ("1".equals(status)) {
				String temp = casesidsql.substring(casesidsql.indexOf("%") + 1, casesidsql.length() - 1);
				// 清除最后一个;
				if (temp.indexOf(";") > -1 && temp.substring(temp.length() - 1, temp.length()).indexOf(";") > -1) {
					temp = temp.substring(0, temp.length() - 1);
				}
				// 多个名称
				if (temp.indexOf("null") <= -1 && temp.indexOf(";") > -1) {
					command = temp.split(";", -1);
					// 一个名称
				} else if (temp.indexOf("null") <= -1 && temp.indexOf(";") <= -1) {
					command = new String[1];
					command[0] = temp;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行取出指定任务ID中所属的调度是否要自动重启TOMCAT SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
			return command;
		}
		return command;

	}

	/**
	 * 获取测试计划名称
	 */
	public static String getTestPlanName(String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		String testplanname = "NULL";
		try {
			String sql = dbt.executeQuery(
					"select t.testlinkname from test_jobs t where id in (select jobid from test_taskexcute t where t.id = "
							+ inttaskid + ")");
			testplanname = sql.substring(0, sql.lastIndexOf("%"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行获取测试计划名称SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
			return testplanname;
		}
		return testplanname;
	}

	/**
	 * 获取任务测试时长
	 */
	public static String getTestTime(String taskid) {
		int inttaskid = Integer.parseInt(taskid);
		String desTime = "计算测试时长出错！";
		try {
			String sql = dbt.executeQuery(
					"select date_format(t.createtime,'%Y-%m-%d %T'),date_format(t.finishtime,'%Y-%m-%d %T') from test_taskexcute t where t.id= "
							+ inttaskid);
			String starttime = sql.substring(0, sql.indexOf("%"));
			String finishtime = sql.substring(sql.indexOf("%") + 1, sql.length() - 1);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date start = df.parse(starttime);
            if (StringUtils.isNotBlank(finishtime) && !StringUtils.equalsIgnoreCase(finishtime, "null")) {
                Date finish = df.parse(finishtime);
                long l = finish.getTime() - start.getTime();
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
                desTime = "<font color='#2828FF'>" + hour + "</font>小时<font color='#2828FF'>" + min
                        + "</font>分<font color='#2828FF'>" + s + "</font>秒";
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行获取任务测试时长SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
			return desTime;
		}
		return desTime;
	}

	/**
	 * 查询web执行，浏览器类型
	 */
	public static int querydrivertype(String taskid) {
		int taskidtoint = Integer.parseInt(taskid);
		int drivertype = 0;
		try {
			String sqlresult = dbt.executeQuery(
					"select browsertype from test_jobs where id = (select jobid from test_taskexcute where id = "
							+ taskidtoint + ")");
			drivertype = Integer.parseInt(sqlresult.substring(0, sqlresult.lastIndexOf("%")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行查询web执行浏览器类型SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
			return drivertype;
		}
		return drivertype;
	}

	/**
	 * 查询任务中用例步骤日志执行实际结果
	 */
	public static String getLogDetailTestResult(int taskid, String caseno, int casestatus) {
		String sqlresult = "";
		try {
			sqlresult = dbt.executeQuery(
					"select detail from test_logdetail where logid=(select MIN(logid) from test_logdetail "
							+ "where loggrade='error' and taskid=" + taskid
							+ " and caseid=(select id from test_casedetail where taskid=" + taskid + " and caseno='"
							+ caseno + "' and casestatus='" + casestatus + "'))");
			if (sqlresult.indexOf("测试结果：") <= 0 || sqlresult.indexOf("%") <= 0) {
				return sqlresult;
			}
			sqlresult = sqlresult.substring(sqlresult.indexOf("测试结果：") + 5, sqlresult.lastIndexOf("%"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行查询任务中用例步骤日志执行实际结果SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
			return sqlresult;
		}
		return sqlresult;
	}

	/**
	 * 根据任务名称查询任务ID
	 */
	public static int getTaskExcuteTaskid(String taskname) {
		String sqlresult = "";
		try {
			sqlresult = dbt.executeQuery("select id from test_taskexcute t where t.taskid='" + taskname + "'");
			sqlresult = sqlresult.substring(0, sqlresult.lastIndexOf("%"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行根据任务名称查询任务ID SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
			return Integer.parseInt(sqlresult);
		}
		return Integer.parseInt(sqlresult);
	}

	/**
	 * 查询任务中用例步骤日志预期结果 2017-09-16
	 */
	public static String getLogDetailExpectResult(int taskid, String caseno, int casestatus) {
		String sqlresult = "";
		try {
			sqlresult = dbt.executeQuery(
					"select detail from test_logdetail where logid=(select MIN(logid) from test_logdetail "
							+ "where loggrade='error' and taskid=" + taskid
							+ " and caseid=(select id from test_casedetail where taskid=" + taskid + " and caseno='"
							+ caseno + "' and casestatus='" + casestatus + "'))");
			if (sqlresult.indexOf("预期结果：") <= 0 || sqlresult.indexOf("%") <= 0) {
				return sqlresult;
			}
			sqlresult = sqlresult.substring(sqlresult.indexOf("预期结果：") + 5, sqlresult.lastIndexOf("测试结果：") - 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			luckyclient.publicclass.LogUtil.APP.error("执行查询任务中用例步骤日志预期结果 SQL出现异常，请确认数据库链接是否正常！", e);
			e.printStackTrace();
			return sqlresult;
		}
		return sqlresult;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	}

}
