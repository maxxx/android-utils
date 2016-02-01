package ru.maxdestroyer.utils.net;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class HostChecker extends Thread 
{
	String host = "";
	public int connect = -1;
	
	public HostChecker(String _host)
	{
		host = _host;
	}
	
	@Override
	public void run()
	{
		try
		{
			SocketAddress sockaddr = new InetSocketAddress(host, 80);
			Socket sock = new Socket();
			int timeoutMs = 30000;
			sock.connect(sockaddr, timeoutMs);
			connect = 1;
		} catch (Exception e)
		{
			//Util.log(e.toString());
			e.printStackTrace();
			connect = 0;
		}
    }
}

