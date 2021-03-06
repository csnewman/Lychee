package com.error22.lychee.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
		out.ensureWritable(4 + in.readableBytes());
		out.writeInt(in.readableBytes());
		out.writeBytes(in, in.readerIndex(), in.readableBytes());
	}
}
