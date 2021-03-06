package com.error22.lychee.network.packets;

import java.io.IOException;
import java.util.EnumSet;

import com.error22.lychee.network.ExtensionSet;
import com.error22.lychee.network.INetworkHandler;
import com.error22.lychee.network.IPacket;
import com.error22.lychee.network.NetworkExtension;
import com.error22.lychee.network.PacketBuffer;

public class Handshake implements IPacket {
	private String address, ident;
	private int version, port;
	private ExtensionSet extensionSet;

	public Handshake() {
	}

	public Handshake(int version, String ident, String address, int port, ExtensionSet extensionSet) {
		this.address = address;
		this.ident = ident;
		this.version = version;
		this.port = port;
		this.extensionSet = extensionSet;
	}

	@Override
	public void read(INetworkHandler handler, PacketBuffer buffer) throws IOException {
		version = buffer.readInt();
		address = buffer.readString();
		port = buffer.readInt();
		ident = buffer.readString();
		extensionSet = new ExtensionSet();
		int count = buffer.readInt();
		for (int i = 0; i < count; i++) {
			String name = buffer.readString();
			if (NetworkExtension.isExtensionKnown(name)){
				extensionSet.enable(NetworkExtension.getExtension(name));
			}
		}
	}

	@Override
	public void write(INetworkHandler handler, PacketBuffer buffer) throws IOException {
		buffer.writeInt(version);
		buffer.writeString(address);
		buffer.writeInt(port);
		buffer.writeString(ident);
		NetworkExtension[] extensions = extensionSet.getAllEnabled();
		buffer.writeInt(extensions.length);
		for (NetworkExtension e : extensions) {
			buffer.writeString(e.getName());
		}

	}

	public String getAddress() {
		return address;
	}

	public String getIdent() {
		return ident;
	}

	public int getVersion() {
		return version;
	}

	public int getPort() {
		return port;
	}

	public ExtensionSet getExtensionSet() {
		return extensionSet;
	}

	@Override
	public EnumSet<NetworkExtension> getRequiredExtensions() {
		return EnumSet.of(NetworkExtension.Base);
	}

}
