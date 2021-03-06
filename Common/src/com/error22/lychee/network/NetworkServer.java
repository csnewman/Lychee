package com.error22.lychee.network;

import java.net.InetAddress;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NetworkServer {
	private static NioEventLoopGroup networkGroup = new NioEventLoopGroup(0,
			(new ThreadFactoryBuilder()).setNameFormat("Netty-%d").setDaemon(true).build());

	public void start(InetAddress host, int port, Class<? extends INetworkHandler> handler) {
		new ServerBootstrap().channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel channel) throws Exception {
				ChannelPipeline pipeline = channel.pipeline();

				pipeline.addLast(new ReadTimeoutHandler(130));
				pipeline.addLast("splitter", new PacketSplitter());
				pipeline.addLast("decoder", new PacketDecoder());
				pipeline.addLast("prepender", new PacketPrepender());
				pipeline.addLast("encoder", new PacketEncoder());
				pipeline.addLast("manager", new NetworkManager(handler.getConstructor().newInstance()));
			}
		}).group(networkGroup).childOption(ChannelOption.SO_KEEPALIVE, true).localAddress(host, port).bind().syncUninterruptibly();
	}

}
