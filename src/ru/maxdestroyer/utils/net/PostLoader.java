package net.malahovsky.utils.net;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import net.malahovsky.utils.Convert;
import net.malahovsky.utils.Util;
import net.malahovsky.utils.view.UtilActivity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.FileBody;

// <uses-permission android:name="android.permission.INTERNET" />
public class PostLoader extends Thread
{
	protected String url = "";
	protected String json = null;
	public String answer = null;
	public byte[] answerBytes = new byte[0];
	protected ArrayList<String> vars = new ArrayList<String>();
	protected ArrayList<String> varNames = new ArrayList<String>();
	public ProgressDialog pdial = null;
	public Activity context = null;
	public UtilActivity ua = null;
	public String dTxt = "Загрузка...";
	public boolean show = true;
	public String cookie = "";
	public boolean get = false;
	public String method = "";
	public File file = null;
	public String filename = "";
	public boolean binary = false;
	public int responseCode = 0;
	private HttpResponse response = null; // debug

	public PostLoader(String _url, ArrayList<String> _var, ArrayList<String> _varName)
	{
		url = _url;
		vars = _var;
		varNames = _varName;
	}
	
	public PostLoader(String _url, String _var, String _varName)
	{
		url = _url;
		vars.add(_var);
		varNames.add(_varName);
	}
	
	public PostLoader(String _url, JSONObject _json)
	{
		url = _url;
		if (_json != null)
			json = _json.toString();
	}
	public PostLoader(String _url, JSONArray _json)
	{
		url = _url;
		if (_json != null)
			json = _json.toString();
	}

	public PostLoader(String _url)
	{
		url = _url;
	}

	public PostLoader(String _url, File f)
	{
		url = _url;
		file = f;
	}
	
	@Override
	public void run()
	{
    	try
    	{
    		ShowDial();
    		
    		try
    		{
    			response = JSONPost(url, json, vars, varNames);
    		} catch (Exception e)
    		{
    			Util.LOG("PostLoader ex. url =  " + url + " . Json = " + json + ". e = " + e.toString());
    			CancelDial();
    			answer = "error";
    	        if (ua != null)
    	        {
    	        	ua.OnTaskFinish(new Object[] {answer});
    	        }
    	        return;
    		}

			responseCode = response.getStatusLine().getStatusCode();

			try
			{
				if (!binary)
				{
					answer = EntityUtils.toString(response.getEntity());
				} else
				{
					InputStream is = response.getEntity().getContent();
					answerBytes = Convert.InputStreamToByteArr(is);//EntityUtils.toByteArray(ent);
					answer = "";
				}
			} catch (Exception e)
			{
				Util.LOG(e.getMessage());
				e.printStackTrace();
			}

	        CancelDial();

	        if (ua != null)
	        {
	        	ua.OnTaskFinish(new Object[] {answer});
	        }
		} catch (Exception e)
		{
			CancelDial();
			answer = "error";
	        if (ua != null)
	        {
	        	ua.OnTaskFinish(new Object[] {answer});
	        }
			Log.e("PostLoader error - ", e.toString());
			e.printStackTrace();
		}
    }
	
	private void ShowDial()
	{
		if (context != null && show)
		{
			context.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (pdial.isShowing() || context.isFinishing())
						return;
					
					pdial.setTitle("");
					pdial.setMessage(dTxt);
					pdial.setIndeterminate(true);
					//pdial.setCancelable(false);
					pdial.show();

					{
						TextView tv1 = (TextView) pdial.findViewById(android.R.id.message);  
						tv1.setTextColor(Color.WHITE);
						((android.widget.LinearLayout)tv1.getParent()).setBackgroundColor(Color.BLACK);
					}
				}
			});
		}
	}

	public void CancelDial()
	{
        if (context != null && show)
		{
        	context.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (pdial.isShowing())
						pdial.cancel();
				}
			});
		}
	}
	
	public HttpResponse JSONPost(String link, String json, ArrayList<String> varz, ArrayList<String> namez) throws Exception
	{
		HttpClient httpClient = getNewHttpClient(getPort(link));
		//HttpProtocolParams.setUserAgent(httpClient.getParams(), "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.115 Safari/537.36");
		HttpGet httpGet = new HttpGet(link);
	    HttpPost httpPost = new HttpPost(link);
		HttpPatch httpPatch = new HttpPatch(link);

	    if (json != null)
		{
			if (method.equals("PATCH"))
			{
				httpPatch.setEntity(new StringEntity(json,
				  HTTP.UTF_8));
				httpPatch.setHeader("Accept", "application/json");
				httpPatch.setHeader("Content-type", "application/json; charset=utf-8");
				httpPatch.setHeader("Cache-Control", "no-cache");
				httpPatch.setHeader("Accept-Encoding", "UTF-8");
				if (!cookie.equals(""))
				{
					//httpPost.setHeader("Set-Cookie", cookie);
					httpPatch.setHeader("Cookie", cookie);
				}
			} else if (method.equals("GET"))
			{
				if (!cookie.equals(""))
				{
					//httpGet.setHeader("Set-Cookie", cookie);
					httpGet.setHeader("Cookie", cookie);
				}
				return httpClient.execute(httpGet);
			} else
			{
				httpPost.setEntity(new StringEntity(json,
				  HTTP.UTF_8));
				httpPost.setHeader("Accept", "application/json");
				httpPost.setHeader("Content-type", "application/json; charset=utf-8");
				httpPost.setHeader("Cache-Control", "no-cache");
				httpPost.setHeader("Accept-Encoding", "UTF-8");
				if (!cookie.equals(""))
				{
					//httpPost.setHeader("Set-Cookie", cookie);
					httpPost.setHeader("Cookie", cookie);
				}
			}

			//httpPost.setHeader("Host", getHost(path));
			//httpPost.setURI(new URI("/api/Registration/"));
		} else if (file != null)
		{
			// todo: uncomment if use!!!
//			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//			FileBody fb = new FileBody(file);
//			builder.addPart("file", fb);
//			if (!cookie.equals(""))
//			{
//				//httpPost.setHeader("Set-Cookie", cookie);
//				httpPost.setHeader("Cookie", cookie);
//			}
//			String boundary = "-------------" + System.currentTimeMillis();
//
//			// с этим сервер отвечает ошибкой
//			//httpPost.setHeader("Content-Disposition", "form-data; name=\"file\"; filename=\"" + filename + "\"");
//			//httpPost.setHeader("Content-type", "multipart/form-data; boundary=" + boundary);
//			//httpPost.setHeader("Content-type", "application/octet-stream");
//			httpPost.setEntity(builder.build());
		} else
		{
			if (namez.size() == 0)
			{
				if (!cookie.equals(""))
				{
					//httpGet.setHeader("Set-Cookie", cookie);
					httpGet.setHeader("Cookie", cookie);
				}
				if (binary)
				{
					httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36");
					httpGet.addHeader("Accept", "*/*");
					httpGet.addHeader("Content-Type", "application/octet-stream");
					httpGet.addHeader("DNT", "1");
					httpGet.addHeader("Accept-Encoding", "gzip,deflate,sdch");
					httpGet.addHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
				}
				URL urll = new URL(url);
				//HttpsURLConnection https = (HttpsURLConnection) urll.openConnection();
				return httpClient.execute(httpGet);
			}

			if (!cookie.equals(""))
				httpPost.setHeader("Cookie", cookie);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (int i = 0; i < varz.size(); ++i)
				nameValuePairs.add(new BasicNameValuePair(namez.get(i), varz.get(i)));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
		}

		if (method.equals("PATCH"))
			return httpClient.execute(httpPatch);

	    return httpClient.execute(httpPost);
	}

	// if will have problems, see https://stackoverflow.com/questions/4839447/problem-extracting-port-number-with-url-getport-when-url-contains
	private int getPort(String link) throws MalformedURLException
	{
		return new URL(link).getPort();
	}

	public static String getHost(String url)
	{
	    if (url == null || url.length() == 0)
	        return "";

	    int doubleslash = url.indexOf("//");
	    if (doubleslash == -1)
	        doubleslash = 0;
	    else
	        doubleslash += 2;

	    int end = url.indexOf('/', doubleslash);
	    end = end >= 0 ? end : url.length();

	    return url.substring(doubleslash, end);
	}

	public HttpClient getNewHttpClient(int httpsPort) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(new HostnameVerifier());

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			int timeoutConnection = 3 * 60 * 1000;
			HttpConnectionParams.setConnectionTimeout(params, timeoutConnection);
			int timeoutSocket = 3*60*1000;
			HttpConnectionParams.setSoTimeout(params, timeoutSocket);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, httpsPort));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			e.printStackTrace();
			return new DefaultHttpClient();
		}
	}
}



