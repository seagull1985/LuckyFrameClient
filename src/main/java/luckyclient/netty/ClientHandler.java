package luckyclient.netty;

import java.io.*;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.StrUtil;
import luckyclient.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import luckyclient.utils.config.SysConfig;
import springboot.RunService;


public class ClientHandler extends ChannelHandlerAdapter {

    //从application.properties文件中获取用到的参数;
    private static final Resource resource = new ClassPathResource("application.properties");
    private static Properties props;

    static {
        try {
            props = PropertiesLoaderUtils.loadProperties(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String port = props.getProperty("server.port");

    private String NETTY_HOST = SysConfig.getConfiguration().getProperty("netty.host");

    private static final String CLIENT_NAME = SysConfig.getConfiguration().getProperty("client.name");

    private static final String CLIENT_VERSION = SysConfig.getConfiguration().getProperty("client.verison");

    private static final String SERVER_IP = SysConfig.getConfiguration().getProperty("server.web.ip");

    private static final String SERVER_PORT = SysConfig.getConfiguration().getProperty("server.web.port");

    public static Integer clientId=-1;

    private volatile int lastLength = 0;

    public RandomAccessFile randomAccessFile;


    private static ChannelHandlerContext ctx;

    public ClientHandler() {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        //统一转编码
        //服务端消息处理,如果接收到测试任务方法，则直接产生一个http请求并发送请求到本地
        String jsonStr = msg.toString();
        JSONObject json;
        try {
            json = JSON.parseObject(msg.toString());

        } catch (Exception e) {
            LogUtil.APP.error("收到服务端非Json消息,但是异常：" + msg);
            return;
        }
        LogUtil.APP.info("收到服务端消息：" + json.toString());
        //解析消息
        if ("run".equals(json.get("method"))) {
            //返回请求
            JSONObject re = new JSONObject();
            re.put("method", "return");
            Result result = new Result(1, "同步等待消息返回", json.get("uuid").toString(), null);
            //如果是调度请求，则发起一个HTTP请求
            //获取是get方法还是post方法
            String getOrPost = json.get("getOrPost").toString();
            String urlParam = "http://127.0.0.1:" + port + "/" + json.get("url").toString();
            Integer socketTimeout = Integer.valueOf(json.get("socketTimeout").toString());
            String tmpResult = "";
            if ("get".equals(getOrPost)) {
                @SuppressWarnings("unchecked")
                Map<String, Object> jsonparams = (Map<String, Object>) json.get("data");
                //get方法
                try {
                    tmpResult = HttpRequest.httpClientGet(urlParam, jsonparams, socketTimeout);
                } catch (Exception e) {
                    LogUtil.APP.error("转发服务端GET请求出错");
                }
            } else {
                String jsonparams = json.get("data").toString();
                //post方法
                try {
                    tmpResult = HttpRequest.httpClientPost(urlParam, jsonparams, socketTimeout);
                } catch (Exception e) {
                    LogUtil.APP.error("转发服务端POST请求出错");
                }
            }
            result.setMessage(tmpResult);
            re.put("data", result);
            sendMessage(re.toString());
        } else if ("download".equals(json.get("method"))) {
            String loadpath = json.get("path").toString();
            String path = RunService.APPLICATION_HOME + loadpath;
            String fileName = json.get("fileName").toString();
            //发出http请求下载文件
            try {
                HttpRequest.downLoadFromUrl("http://" + SERVER_IP + ":" + SERVER_PORT + "/common/download?fileName=" + fileName, fileName, path);
                //返回请求
                JSONObject re = new JSONObject();
                Result result = new Result(1, "上传成功", json.get("uuid").toString(), null);
                re.put("method", "return");
                re.put("data", result);
                sendMessage(re.toString());
                LogUtil.APP.info("下载驱动包成功,路径为：" + path + ";文件名为：" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
                //返回请求
                JSONObject re = new JSONObject();
                Result result = new Result(0, "上传失败", json.get("uuid").toString(), null);
                re.put("method", "return");
                re.put("data", result);
                sendMessage(re.toString());
                LogUtil.APP.error("下载驱动包失败,路径为：" + path + ";文件名为：" + fileName);
            }
        } else if ("upload".equals(json.get("method"))) {
            try {
                //上传截图到服务器
                @SuppressWarnings("unchecked")
				Map<String, Object> jsonparams = (Map<String, Object>) json.get("data");
                String fileName = jsonparams.get("imgName").toString();
                String ctxPath = RunService.APPLICATION_HOME + File.separator + "log" + File.separator + "ScreenShot" + File.separator + fileName;
                File file = new File(ctxPath);
                int start = Integer.parseInt(json.get("start").toString());
                FileUploadFile fileUploadFile = new FileUploadFile();
                fileUploadFile.setFile(file);
                if (start != -1) {
                    if (start == 0)
                        lastLength = 1024 * 10;
                    randomAccessFile = new RandomAccessFile(fileUploadFile.getFile(), "r");
                    randomAccessFile.seek(start); //将文件定位到start
                    LogUtil.APP.info("长度：" + (randomAccessFile.length() - start));
                    int a = (int) (randomAccessFile.length() - start);
                    int b = (int) (randomAccessFile.length() / 1024 * 2);
                    if (a < lastLength) {
                        lastLength = a;
                    }
                    LogUtil.APP.info("文件长度：" + (randomAccessFile.length()) + ",start:" + start + ",a:" + a + ",b:" + b + ",lastLength:" + lastLength);
                    byte[] bytes = new byte[lastLength];
                    LogUtil.APP.info("bytes的长度是=" + bytes.length);
                    int byteRead;
                    if ((byteRead = randomAccessFile.read(bytes)) != -1 && (randomAccessFile.length() - start) > 0) {
                        LogUtil.APP.info("byteRead = " + byteRead);
                        fileUploadFile.setEndPos(byteRead);
                        fileUploadFile.setBytes(bytes);
                        //返回请求
                        JSONObject re = new JSONObject();
                        Result result = new Result(1, "上传成功", json.get("uuid").toString(), fileUploadFile);
                        re.put("method", "upload");
                        re.put("data", result);
                        re.put("uuid", json.get("uuid").toString());
                        re.put("imgName", fileName);
                        re.put("start", start);
                        sendMessage(re.toString());
                        try {
                            ctx.writeAndFlush(fileUploadFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        randomAccessFile.close();
                        LogUtil.APP.info("文件已经读完channelRead()--------" + byteRead);
                        //返回请求
                        JSONObject re = new JSONObject();
                        Result result = new Result(1, "上传成功", json.get("uuid").toString(), null);
                        re.put("method", "return");
                        re.put("data", result);
                        sendMessage(re.toString());

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                //返回请求
                JSONObject re = new JSONObject();
                Result result = new Result(0, "异常错误", json.get("uuid").toString(), null);
                re.put("method", "return");
                re.put("data", result);
                sendMessage(re.toString());
            }
        }
        else if("loginReturn".equals(json.get("method")))
        {
            try {
                //设置client标记
                clientId=Integer.valueOf(json.get("clientId").toString());
            } catch (Exception e){
                LogUtil.APP.error("设置ClientID失败，请检查异常",e);
            }
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //发送客户端登录消息
        JSONObject json = new JSONObject();
        if(StrUtil.isEmpty(NETTY_HOST)){
            this.NETTY_HOST=RunService.CLIENT_IP;
        }
        json.put("hostName", NETTY_HOST);
        json.put("clientName", CLIENT_NAME);
        json.put("version", CLIENT_VERSION);
        json.put("ip", RunService.CLIENT_IP);
        json.put("method", "clientUp");
        ClientHandler.ctx = ctx;
        sendMessage(json.toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LogUtil.APP.info("连接已断开，正在尝试重连...");
        //使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(() -> {
            try {
                NettyClient.start();
            } catch (Exception e) {
                LogUtil.APP.error("链接出现异常，正在尝试重连...",e);
            }
        }, 1, TimeUnit.SECONDS);

        ctx.fireChannelInactive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
            throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                //发送心跳,保持长连接
                JSONObject json = new JSONObject();
                json.put("method", "ping");
                json.put("hostName", NETTY_HOST);
                //ctx.channel().writeAndFlush(json.toString() + "$_").sync();
                sendMessage(json.toString());
                //log.info("心跳发送成功!");
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    public static void sendMessage(String json) {
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer((json + "$_").getBytes()));
    }

}