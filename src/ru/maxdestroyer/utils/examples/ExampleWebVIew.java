package ru.maxdestroyer.utils.examples;

public class ExampleWebVIew
{
//	WebView wv = (WebView) rootView.findViewById(R.id.webView);
//	wv.getSettings().setJavaScriptEnabled(true);
//	wv.getSettings().setDomStorageEnabled(true);
//	wv.getSettings().setDatabaseEnabled(true);
//	wv.getSettings().setAppCacheEnabled(true);
//	// ускорение
//	// webView.getSettings().setRenderPriority(RenderPriority.HIGH);
//	wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//	// mWebView.getSettings().setLoadWithOverviewMode(true);
//	// mWebView.getSettings().setUseWideViewPort(true);
//	// webVIew.getSettings().setBuiltInZoomControls(true);
//
//	// чтобы редиректы не открывали родной браузер
//	WebViewClient wClient = new WebViewClient()
//	{
//		@Override
//		public boolean shouldOverrideUrlLoading(WebView view, String url)
//		{
//			view.loadUrl(url);
//			//Util.LOG("webview - url - " + url);
//			return true;
//		}
//
//		@Override
//		public void onPageFinished(WebView view, String url)
//		{
//			super.onPageFinished(view, url);
//			//Util.LOG("rl: " + url);
//                    /*if (Util.GetApiLvl() >= 16true)
//                    {
//                        view.loadUrl("javascript:HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
//                    } else
//                        view.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");*/
//		}
//	};
//
//	WebChromeClient wCClient = new WebChromeClient()
//	{
//		@Override
//		public void onProgressChanged(WebView view, int progress)
//		{
//		}
//	};
//
//	class MyJavaScriptInterface
//	{
//		@JavascriptInterface
//		public void processHTML(String html)
//		{
//
//		}
//	}
//
//	wv.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
//
//	wv.setWebViewClient(wClient);
//	wv.setWebChromeClient(wCClient);
//
//	wv.loadUrl("http://startuptravels.com");
}
