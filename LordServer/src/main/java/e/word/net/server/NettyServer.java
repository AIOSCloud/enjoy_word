package e.word.net.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

public class NettyServer {
    private final Logger logger = Logger.getLogger(NettyServer.class);

    public void Init() {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, work);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new NioWebSocketChannelInitializer());
            Channel channel = bootstrap.bind(8090).sync().channel();
            logger.info("websocket服务器启动成功:" + channel);
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("运行出错:" + e);
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
            logger.info("webSocket服务器已关闭");
        }
    }
}
