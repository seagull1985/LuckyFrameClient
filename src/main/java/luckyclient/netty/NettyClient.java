package luckyclient.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import luckyclient.utils.config.SysConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static final String NETTY_SERVER_IP= SysConfig.getConfiguration().getProperty("server.web.ip");
    private static final int  NETTY_SERVER_PORT=Integer.parseInt(SysConfig.getConfiguration().getProperty("netty.server.port"));
    private static final String NETTY_ENCODER= SysConfig.getConfiguration().getProperty("netty.encoder");
    private static final String NETTY_DECODER= SysConfig.getConfiguration().getProperty("netty.decoder");

    protected static Channel channel;

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private static ClientHandler clientHandler;

    public static void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        clientHandler=new ClientHandler();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                        p.addLast("decoder", new StringDecoder(charConvert(NETTY_DECODER)));
                        p.addLast("encoder", new StringEncoder(charConvert(NETTY_ENCODER)));
                        p.addLast(new IdleStateHandler(1,0,0,TimeUnit.SECONDS));
                        p.addLast(clientHandler);
                    }
                });
        //连接服务端
        ChannelFuture connect = b.connect(NETTY_SERVER_IP, NETTY_SERVER_PORT);
        //断线重连
        connect.addListener((ChannelFutureListener) channelFuture -> {
            if (!channelFuture.isSuccess()) {
                final EventLoop loop = channelFuture.channel().eventLoop();
                loop.schedule(() -> {
                    try {
                        log.error("服务端链接不上，开始重连操作...");
                        start();
                    } catch (Exception ignored) {

                    }
                }, 1L, TimeUnit.SECONDS);
            } else {
                channel = channelFuture.channel();
                log.info("服务端链接成功...");
            }
        });
    }

    private static Charset charConvert(String strChar){
        if("utf-8".equals(strChar.toLowerCase())){
            return StandardCharsets.UTF_8;
        }else if("iso_8859_1".equals(strChar.toLowerCase())){
            return StandardCharsets.ISO_8859_1;
        }else if("us_ascii".equals(strChar.toLowerCase())){
            return StandardCharsets.US_ASCII;
        }else if("utf_16".equals(strChar.toLowerCase())){
            return StandardCharsets.UTF_16;
        }else if("utf_16be".equals(strChar.toLowerCase())){
            return StandardCharsets.UTF_16BE;
        }else if("utf_16le".equals(strChar.toLowerCase())){
            return StandardCharsets.UTF_16LE;
        }else if("gbk".equals(strChar.toLowerCase())){
            return Charset.forName("GBK");
        }else{
            return StandardCharsets.UTF_8;
        }
    }
}
