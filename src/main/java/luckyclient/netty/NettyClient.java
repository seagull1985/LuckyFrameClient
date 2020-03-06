package luckyclient.netty;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import luckyclient.utils.config.SysConfig;

public class NettyClient {
    private static final String NETTY_SERVER_IP= SysConfig.getConfiguration().getProperty("server.web.ip");

    private static final int  NETTY_SERVER_PORT=Integer.parseInt(SysConfig.getConfiguration().getProperty("netty.server.port"));

    protected static Channel channel;

    private static ChannelFuture connect;

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    private static ClientHandler clientHandler;
    public static void start() throws InterruptedException{
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            clientHandler=new ClientHandler();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            p.addLast("decoder", new StringDecoder(Charset.forName("GBK")));
                            p.addLast("encoder", new StringEncoder(Charset.forName("UTF-8")));
                            p.addLast(new IdleStateHandler(1,0,0,TimeUnit.SECONDS));
                            p.addLast(clientHandler);
                        }
                    });
            //连接服务端
            connect = b.connect(NETTY_SERVER_IP, NETTY_SERVER_PORT);
            //断线重连
            connect.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        final EventLoop loop = channelFuture.channel().eventLoop();
                        loop.schedule(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    log.error("服务端链接不上，开始重连操作...");
                                    start();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 1L, TimeUnit.SECONDS);
                    } else {
                        channel = channelFuture.channel();
                        log.info("服务端链接成功...");
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //不关闭通道
            //group.shutdownGracefully();
        }
    }
}
