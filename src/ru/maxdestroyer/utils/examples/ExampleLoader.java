package net.malahovsky.utils.examples;

public class ExampleLoader
{

//	Thread thr = new Thread(new Runnable()
//	{
//		@Override
//		public void run()
//		{
//			PostLoader loader = new PostLoader(MainFrame.URL + "/goals",
//			  null);
//			loader.pdial = MainFrame.pDialog;
//			loader.context = MainFrame.this;
//			loader.dTxt = "Загрузка...";
//			loader.start();
//			while (loader.answer == null)
//				try
//				{
//					Thread.sleep(20);
//				} catch (InterruptedException e)
//				{
//					Log.e("PostLoader", e.getMessage());
//					e.printStackTrace();
//				}
//
//			final String ans = loader.answer;
//			if (!ans.contains("\"error\"") && !ans.equals("error"))
//			{
//				try
//				{
//					JSONArray profile = new JSONArray(ans);
//
//					for (int i = 0; i < profile.length(); ++i)
//					{
//						JSONObject obj = profile.getJSONObject(i);
//						Goal b = new GsonBuilder().create().fromJson(obj.toString(), Goal.class);
//						cache.goals.add(b);
//					}
//
//					runOnUiThread(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//						}
//					});
//				} catch (JSONException e)
//				{
//					Util.LOG(e.toString());
//					e.printStackTrace();
//				}
//			} else
//			{
//				if (!ans.equals("error"))
//				{
//					String exp = "\"message\":\"(.*?)\"";
//					MSG3(Util.RegExp(exp, ans).get(1));
//				} else
//					MSG3("Ошибка соединения с сервером");
//			}
//		}
//	});
//	thr.start();
}
