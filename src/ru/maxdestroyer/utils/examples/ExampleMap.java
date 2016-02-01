//package ru.maxdestroyer.utils.examples;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.content.res.Resources;
//import android.graphics.Rect;
//import android.graphics.drawable.Drawable;
//import android.location.Location;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.os.IBinder;
//import ru.maxdestroyer.utils.R;
//import ru.maxdestroyer.utils.Util;
//import ru.maxdestroyer.utils.view.UtilActivity;
//
//import java.util.ArrayList;
//import java.util.List;
//

//public class ExampleMap
//{

//	public class MainFrame extends UtilActivity
//	{
//		// gps
//		public static boolean GPS = true;
//		static LocationManager locMgr;
//		//PendingIntent pendingIntent;
//		final int GPS_MIN_DIST = 0; // в помещении = 0 для теста. На улице = 10
//		// не константа! = flush_interval / collect_times * 1000
//		static long GPS_REC_INTERVAL = 5 * 1000; // не факт что именно такой и будет. На
//		// четверке норм, на двойке рандом.
//		static ArrayList<Location> locations = new ArrayList<Location>();
//		static Location curLoc = null;
//		final static int MAX_LOC = 3;
//		protected static long lastGPSRecieve = 0;
//		static LocationService locService;
//		public static boolean locServiceBound;
//		//public static final String LOCATION_UPDATE_ACTION = "net.malahovksy.LOCATION_UPDATE_RECEIVED";
//		private static final String GPS_SERVICE = "ru.moykainfo.app.LocationService";
//		// map
//		MapView mapView;
//		OverlayGeoCode overlayClick;
//		Overlay overlayMarkers;
//		public static OverlayPath overlayRoute;
//		public static MapController controller;
//		public final static int mapType = 2; // 0 = схема, 1 = спутник, 2 = народная
//		public static Marker me;
//		public ArrayList<GeoPoint> path = new ArrayList<>();
//		public static MainFrame mf;
//
//		@Override
//		public void onCreate(Bundle savedInstanceState)
//		{
//			super.onCreate(savedInstanceState);
//			setContentView(R.layout.main);
//
//			mf = this;
//
//
//			// map
//			mapView = (MapView) findViewById(R.id.map);
//			controller = mapView.getMapController();
//			OverlayManager mOverlayManager = controller.getOverlayManager();
//			// Disable determining the user's location
//			mOverlayManager.getMyLocation().setEnabled(false);
//			controller.showZoomButtons(true);
//			controller.showFindMeButton(true);
//			mapView.showBuiltInScreenButtons(true); // пробки, gps...
//			//controller.showJamsButton(false);
//			controller.getOverlayManager().getMyLocation()
//			  .setEnabled(false);
//			controller.getOverlayManager().getMyLocation()
//			  .setVisible(false);
//
//			overlayClick = new OverlayGeoCode(mapView.getMapController(), this);
//			overlayMarkers = new OverlayMarkers(controller, this);
//
//			GeoPoint centerScreen = controller.getMapCenter();
//			Resources res = getResources();
//			Marker.BalloonType t = Marker.BalloonType.BALLON_USER;
//			int r = Marker.GetResByType(t);
//			Drawable d = (res.getDrawable(r));
//			Marker me3 = new Marker(t, centerScreen, d);
//			overlayMarkers.addOverlayItem(me3);
//
//			overlayRoute = new OverlayPath(controller, path);
//
//			me = GetUserMarker(overlayMarkers);
//
//			controller.getOverlayManager().addOverlay(mf.overlayClick);
//			controller.getOverlayManager().addOverlay(mf.overlayMarkers);
//			//controller.getOverlayManager().addOverlay(mf.overlayRoute);
//
//			//		if (GPS && GPS_YANDEX)
//			//			controller.getOverlayManager().getMyLocation()
//			//			  .addMyLocationListener(this);
//
//			// нету питера народной
//			// народная карта по умолчанию. Она более точная
//			//		List<MapLayer> list = controller.getListMapLayer();
//			//		controller.setCurrentMapLayer(list.get(mapType));
//			double lat = cfg.LoadDouble("latitude");
//			double lon = cfg.LoadDouble("longitude");
//			if (lat > 0.0f)
//			{
//				CenterAt(new GeoPoint(lat, lon));
//			}
//
//			// --- end map
//           locMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			beginMonitoringLocation(GPS_MIN_DIST);
//		}
//
//		@Override
//		protected void onResume()
//		{
//			super.onResume();
//
//			if (GPS /*
//				&& !controller.getOverlayManager().getMyLocation().isEnabled()*/)
//			{
//				beginMonitoringLocation(GPS_MIN_DIST);
//			}
//		}
//
//		@Override
//		protected void onPause()
//		{
//			if (GPS)
//			{
//				UnregGPS();
//			}
//			super.onPause();
//		}
//
//		@Override
//		protected void onDestroy()
//		{
//			// unregisterReceiver(mHandleMessageReceiver);
//			if (GPS)
//			{
//				UnregGPS();
//			}
//			super.onDestroy();
//		}
//
//		// map
//		private static void CenterAt(final GeoPoint gp)
//		{
//			controller.setPositionNoAnimationTo(gp);
//			cfg.Save("center_lat", gp.getLat());
//			cfg.Save("center_lon", gp.getLon());
//		}
//
//		public static Marker GetUserMarker(Overlay ov)
//		{
//			List<Marker> fullList = ov.getOverlayItems();
//			for (Marker m : fullList)
//			{
//				if (m.type == Marker.BalloonType.BALLON_USER)
//					return m;
//			}
//
//			return null;
//		}
//
//		public void OnMapClickGeo(GeoCode geoCode)
//		{
//
//		}
//
//		@SuppressWarnings("unchecked")
//		public void OnMapClick(final GeoPoint gp, float x, float y)
//		{
//			List<Marker> l = overlayMarkers.getOverlayItems();
//			for (Marker m : l)
//			{
//				Rect rCur = m.getRectBounds();
//				if (rCur.contains((int) x, (int) y))
//				{
//					CenterAt(gp);
//					return;
//				}
//			}
//
//			//CenterAt(gp);
//		}
//
//		private Location GetBestLoc()
//		{
//			if (locations.size() == 0)
//				return null;
//			//return new GeoPoint(curLoc.getLatitude(), curLoc.getLongitude());
//
//			Location loc = locations.get(0);
//			for (Location l : locations)
//			{
//				if (l.getAccuracy() > loc.getAccuracy())
//					loc = l;
//			}
//			return loc;
//		}
//
//		// GPS
//		public boolean beginMonitoringLocation(int minDistance)
//		{
//			boolean enabled = locMgr
//			  .isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//			if (!enabled)
//			{
//				return false;
//			}
//			Intent intent = new Intent(GPS_SERVICE);
//			locServiceBound = bindService(intent, locCon, Context.BIND_AUTO_CREATE);
//			return true;
//		}
//
//		ServiceConnection locCon = new ServiceConnection()
//		{
//			@Override
//			public void onServiceConnected(ComponentName name, IBinder service)
//			{
//				LocationService.LocalBinder binder = (LocationService.LocalBinder) service;
//				locService = binder.getService();
//				locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//				  GPS_REC_INTERVAL, GPS_MIN_DIST, locService);
//				locServiceBound = true;
//			}
//
//			@Override
//			public void onServiceDisconnected(ComponentName name)
//			{
//				locServiceBound = false;
//			}
//		};
//
//		public void OnLocation(Location location)
//		{
//			if (location != null)
//			{
//				locations.add(location);
//				curLoc = location;
//
//
//				cfg.Save("latitude", location.getLatitude());
//				cfg.Save("longitude", location.getLongitude());
//
//				cfg.Save("zoom", controller.getZoomCurrent());
//
//				location = GetBestLoc();
//
//				if (locations.size() > MAX_LOC)
//					locations.clear();
//
//				me.setGeoPoint(new GeoPoint(location.getLatitude(), location
//				  .getLongitude()));
//
//
//			}
//		}
//
//		private void UnregGPS()
//		{
//			try
//			{
//				if (locServiceBound)
//				{
//					if (locMgr != null && locService != null)
//						locMgr.removeUpdates(locService);
//					if (locCon != null)
//						unbindService(locCon);
//					if (locService != null)
//						locService.Die();
//					stopService(new Intent(
//					  GPS_SERVICE));
//					locServiceBound = false;
//					locService = null;
//				}
//			} catch (Exception e)
//			{
//				e.printStackTrace();
//				Util.log("UnregGPS " + e.toString());
//			}
//		}
//	}
//
//}
