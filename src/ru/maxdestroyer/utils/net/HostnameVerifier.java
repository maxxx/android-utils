package ru.maxdestroyer.utils.net;

import java.io.IOException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

public class HostnameVerifier implements org.apache.http.conn.ssl.X509HostnameVerifier
{
	@Override
	public boolean verify(String hostname, SSLSession session)
	{
		return true;
	}

	@Override
	public void verify(String s, SSLSocket sslSocket) throws IOException
	{

	}

	@Override
	public void verify(String s, X509Certificate x509Certificate) throws SSLException
	{

	}

	@Override
	public void verify(String s, String[] strings, String[] strings2) throws SSLException
	{

	}

	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return new java.security.cert.X509Certificate[] {};
	}
}
