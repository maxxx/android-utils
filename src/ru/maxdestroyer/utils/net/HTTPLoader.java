package net.malahovsky.utils.net;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import net.malahovsky.utils.view.UtilActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// <uses-permission android:name="android.permission.INTERNET" />
// берет по URL данные в чистом формате
// использовать для чтения txt, html кода и т.п.
public class HTTPLoader extends Thread 
{
	String url = "";
	//Handler handler;
	//public ArrayList<String> contentList = new ArrayList<String>(); not used
	public String content = null;
	public UtilActivity ua = null;
	public String dTxt = "Подключение...";
	
	public HTTPLoader(String _url/*, Handler h*/)
	{
		url = _url;
		//handler = h;
	}
	
	@Override
	public void run()
	{
		StringBuilder str = new StringBuilder();
		//Message msg = new Message();
		try
		{
			ShowDialog();
			// utf-8 кодирование здесь не использовать! Использовать до, и только для параметров
			URL addr = new URL(url);
//			String query = URLParamEncoder.encode(addr.getQuery());
//			URI addrURI = new URI(addr.getProtocol(), addr.getUserInfo(), addr.getHost(), addr.getPort(), addr.getPath(), query, addr.getRef());
//			addr = addrURI.toURL();
			
//			try
//			{
//				parameter = URLEncoder.encode(parameter, "UTF-8");
//			} catch (UnsupportedEncodingException e1)
//			{
//				e1.printStackTrace();
//			}
			
			HttpURLConnection tc = (HttpURLConnection) addr.openConnection();
			tc.setConnectTimeout(10000);
			//tc.setRequestMethod("GET");
			//tc.setDoOutput(false);
			//tc.setInstanceFollowRedirects(false);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					tc.getInputStream()));
			String line;
			while ((line = in.readLine()) != null)
			{
				//contentList.add(line);
				str.append(line + "\n");
				if (!in.ready()) // android 2.3-2.3.3 bug
					break;
			}
			in.close();
			
			/*InputStreamReader sr;
			sr = new InputStreamReader(tc.getInputStream(), "UTF-8");
			StringBuilder builder = new StringBuilder();
			for (int bt = 0; (bt = sr.read()) != -1;)
			{
			  builder.append((char)bt);
			}
			sr.close();*/
			content = str.toString();
			CancelDial();
	        // случаи когда без sleep
	        if (ua != null)
	        {
	        	ua.OnTaskFinish(new Object[] {content});
	        }
			//msg.obj = content;//str.toString();
			//handler.sendMessage(msg);
		} catch (Exception e)
		{
			//msg.obj = null;
			//handler.sendMessage(msg);
			//contentList.add("Network error");
			content = "Network error";
			Log.e("HTTPLoader", e.toString());
			e.printStackTrace();
			CancelDial();
	        // случаи когда без sleep
	        if (ua != null)
	        {
	        	ua.OnTaskFinish(new Object[] {content});
	        }
		}
    }

	private void ShowDialog()
	{
		if (ua != null)
		{
			ua.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (ua.pDialog.isShowing() || ua.isFinishing())
						return;

					ua.pDialog.setTitle("");
					ua.pDialog.setMessage(dTxt);
					ua.pDialog.setIndeterminate(true);
					// ua.pDialog.setCancelable(false);
					ua.pDialog.show();

					{
						TextView tv1 = (TextView) ua.pDialog
								.findViewById(android.R.id.message);
						tv1.setTextColor(Color.WHITE);
						((android.widget.LinearLayout) tv1.getParent())
								.setBackgroundColor(Color.BLACK);
					}
				}
			});
		}
	}
	
	public void CancelDial()
	{
        if (ua != null)
		{
        	// всегда с UI потока!
        	ua.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (ua.pDialog.isShowing())
						ua.pDialog.cancel();
				}
			});
		}
	}
}

