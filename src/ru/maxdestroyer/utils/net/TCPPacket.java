package net.malahovsky.utils.net;

import java.util.ArrayList;

public class TCPPacket
{
	byte[] header;
	ArrayList<byte[]> body = new ArrayList<byte[]>();
	
	public TCPPacket(byte[] header, ArrayList<byte[]> body)
	{
		this.header = header;
		this.body = body;
	}
	
	public TCPPacket(byte[] header, byte[] _body)
	{
		this.header = header;
		body.add(_body);
	}
}
