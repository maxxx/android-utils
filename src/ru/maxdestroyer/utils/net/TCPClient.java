package net.malahovsky.utils.net;

import android.os.Looper;

import net.malahovsky.utils.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public abstract class TCPClient extends Thread
{
	String host;
	int port;
	protected Socket sock = null;
	protected boolean running = true;
	protected DataOutputStream out;
	protected DataInputStream in;
	public int status = -1; // 0 - не соединился, 1 - соединился
	//TcpListen listenThread;
	
	public TCPClient(String host, int port)
	{
		sock = new Socket();
		this.host = host;
		this.port = port;
	}
	
	private boolean Connect() throws Exception
	{
		SocketAddress serverAddress = new InetSocketAddress(host, port);
        sock.connect(serverAddress, 15*1000);
        sock.setTcpNoDelay(true);
        if (!sock.isConnected())
        {
        	Util.LOG("TCPClient: timeout");
        	status = 0;
        	OnError("TCPClient: timeout");
        	return false;
        }
		out = new DataOutputStream(sock.getOutputStream());
		in = new DataInputStream(sock.getInputStream());
		return true;
	}

	@Override
	public void run()
	{
		try
		{
			if (Connect())
			{
				Looper.prepare();
				runListen();
				Looper.loop();
			}
		} catch (Exception e)
		{
			Util.LOG("TCPClient:Connect error " + e.toString());
			status = 0;
        	OnError(e.toString());
			e.printStackTrace();
		}
	}
	
	public void runListen()
	{
		status = 1;
		OnConnect();
		//(new Thread(listenThread)).start(); -> OnConnect!
	}

	public void sendMessage(byte[] message)
	{
		if (out != null)
		{
			try
			{
				out.write(message);
				out.flush();
			} catch (IOException e)
			{
				Util.LOG("TCPClient:send " + e.toString());
				e.printStackTrace();
				OnError(e.toString());
			}
		}
		else
			Util.LOG("TCPClient:send when out = null!");
	}
	
	public void AppendMessage(byte[] message)
	{
		if (out != null)
		{
			try
			{
				out.write(message);
			} catch (IOException e)
			{
				Util.LOG("TCPClient:AppendMessage " + e.toString());
				e.printStackTrace();
			}
		}
		else
			Util.LOG("TCPClient:AppendMessage when out = null!");
	}
	
	public void Flush()
	{
		if (out != null)
		{
			try
			{
				out.flush();
			} catch (IOException e)
			{
				Util.LOG("TCPClient:Flush " + e.toString());
				e.printStackTrace();
			}
		}
		else
			Util.LOG("TCPClient:Flush when out = null!");
	}

	public void Close()
	{
		running = false;
		//Looper.myLooper().quit();
		try
		{
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (sock != null)
				sock.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		//OnClose();
	}
	
	// http://stackoverflow.com/questions/14795269/parsing-binary-data-in-java-received-over-tcp-socket-server
    public abstract void OnMsg(byte[] buffer);
    public abstract void OnConnect();
    public abstract void OnError(String err);
    //public abstract void OnClose();
}
