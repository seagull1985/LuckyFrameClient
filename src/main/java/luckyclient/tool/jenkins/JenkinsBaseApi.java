package luckyclient.tool.jenkins;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Computer;
import com.offbytwo.jenkins.model.Plugin;
import com.offbytwo.jenkins.model.PluginManager;

import luckyclient.utils.LogUtil;

/**
 * 获取 Jenkins 基础信息以及基本操作
 * =================================================================
 * 这是一个受限制的自由软件！您不能在任何未经允许的前提下对程序代码进行修改和用于商业用途；也不允许对程序代码修改后以任何形式任何目的的再发布。
 * 为了尊重作者的劳动成果，LuckyFrame关键版权信息严禁篡改 有任何疑问欢迎联系作者讨论。 QQ:1573584944 Seagull
 * =================================================================
 * @author Seagull
 * @date 2019年10月29日
 */
public class JenkinsBaseApi {
 
    // Jenkins 对象
    private JenkinsServer jenkinsServer;
 
    /**
     * 构造方法中调用连接 Jenkins 方法
     * @param jenkinsConnect
     * 2019年10月29日
     */
    JenkinsBaseApi(JenkinsConnect jenkinsConnect){
        this.jenkinsServer = jenkinsConnect.connection();
    }
    
    /**
     * 获取主机信息
     * @author Seagull
     * @date 2019年10月29日
     */
    public void getComputerInfo() {
        try {
            Map<String, Computer> map = jenkinsServer.getComputers();
            for (Computer computer : map.values()) {
                // 获取当前节点-节点名称
            	LogUtil.APP.info("当前节点-节点名称:{}",computer.details().getDisplayName());
                // 获取当前节点-执行者数量
                LogUtil.APP.info("当前节点-执行者数量:{}",computer.details().getNumExecutors().toString());
                // 获取当前节点-执行者详细信息
                //List<Executor> executorList = computer.details().getExecutors();
                // 查看当前节点-是否脱机
                LogUtil.APP.info("当前节点-是否脱机:{}",computer.details().getOffline().toString());
                // 获得节点的全部统计信息
                //LoadStatistics loadStatistics = computer.details().getLoadStatistics();
                // 获取节点的-监控数据
                //Map<String, Map> monitorData = computer.details().getMonitorData();
                //......
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * 重启 Jenkins
     * @author Seagull
     * @date 2019年10月29日
     */
    public void restart() {
        try {
        	LogUtil.APP.info("准备重启Jenkins...");
            jenkinsServer.restart(true);
            LogUtil.APP.info("重启Jenkins成功！");
        } catch (IOException e) {
        	LogUtil.APP.error("重启Jenkins出现异常",e);
        }
    }
 
    /**
     * 安全重启 Jenkins
     * @author Seagull
     * @date 2019年10月29日
     */
    public void safeRestart() {
        try {
        	LogUtil.APP.info("准备安全重启 Jenkins...");
            jenkinsServer.safeRestart(true);
            LogUtil.APP.info("安全重启Jenkins成功！");
        } catch (IOException e) {
        	LogUtil.APP.error("安全重启Jenkins出现异常",e);
        }
    }
 
    /**
     * 安全结束 Jenkins
     * 
     * @author Seagull
     * @date 2019年10月29日
     */
    public void safeExit() {
        try {
        	LogUtil.APP.info("准备安全结束 Jenkins...");
            jenkinsServer.safeExit(true);
            LogUtil.APP.info("安全结束 Jenkins成功！");
        } catch (IOException e) {
        	LogUtil.APP.error("安全结束Jenkins出现异常",e);
        }
    }
 
    /**
     * 关闭 Jenkins 连接
     * 
     * @author Seagull
     * @date 2019年10月29日
     */
    public void close() {
    	LogUtil.APP.info("准备关闭Jenkins连接");
        jenkinsServer.close();
        LogUtil.APP.info("关闭Jenkins连接成功");
    }
 
    /**
     * 判断 Jenkins 是否运行
     * 
     * @author Seagull
     * @date 2019年10月29日
     */
    public void isRunning() {
        boolean isRunning = jenkinsServer.isRunning();
        LogUtil.APP.info("Jenkins运行状态:{}",isRunning);
    }
 
    /**
     * 获取 Jenkins 插件信息
     * 
     * @author Seagull
     * @date 2019年10月29日
     */
    public void getPluginInfo(){
        try {
            PluginManager pluginManager =jenkinsServer.getPluginManager();
            // 获取插件列表
            List<Plugin> plugins = pluginManager.getPlugins();
            for (Plugin plugin:plugins){
                // 插件 wiki URL 地址
                LogUtil.APP.info(plugin.getUrl());
                // 版本号
                LogUtil.APP.info(plugin.getVersion());
                // 简称
                LogUtil.APP.info(plugin.getShortName());
                // 完整名称
                LogUtil.APP.info(plugin.getLongName());
                // 是否支持动态加载
                LogUtil.APP.info(plugin.getSupportsDynamicLoad());
                // 插件依赖的组件
                //LogUtil.APP.info(plugin.getDependencies());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
}
