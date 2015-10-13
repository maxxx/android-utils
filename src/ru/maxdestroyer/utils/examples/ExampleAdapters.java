package ru.maxdestroyer.utils.examples;

/**
 * Created by Jack on 18.06.2014.
 */
public abstract class ExampleAdapters
{

	// spinner - string array
//	ArrayList<String> spinnerArray = new ArrayList<String>();
//
//
//	Spinner spinner = new Spinner(this);
//	ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
//	  android.R.layout.simple_spinner_item,
//	  spinnerArray);
//	spinner.setAdapter(spinnerArrayAdapter);




	// listview - str array
//	final ListView listview = (ListView) findViewById(R.id.listview);
//	String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
//	  "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
//	  "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
//	  "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
//	  "Android", "iPhone", "WindowsMobile" };

//	final ArrayList<String> list = new ArrayList<String>();
//	for(
//	int i = 0;
//	i<values.length;++i)
//
//	{
//		list.add(values[i]);
//	}
//
//	final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//	  android.R.layout.simple_list_item_1, list);
//	listview.setAdapter(adapter);
//
//	listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
//
//	{
//
//		@Override
//		public void onItemClick (AdapterView < ? > parent,final View view,
//		int position, long id){
//		final String item = (String) parent.getItemAtPosition(position);
//		view.animate().setDuration(2000).alpha(0)
//		  .withEndAction(new Runnable()
//		  {
//			  @Override
//			  public void run()
//			  {
//				  list.remove(item);
//				  adapter.notifyDataSetChanged();
//				  view.setAlpha(1);
//			  }
//		  });
//	}
//	}
//
//	);


	// listview - simple adapter
//	ListView lv = (ListView) findViewById(R.id.listthings);
//	String[] from = new String[] { "row_1", "row_2" };
//	int[] to = new int[] { R.id.row1, R.id.row2 };
//	ArrayList<HashMap<String, Object>> painItems = new ArrayList<>();
//	HashMap<String, String> map = new HashMap<String, String>();
//	map.put("row_1", row1);
//	painItems.add(map);
//
//	CoursesAdapter adapter = new CoursesAdapter(this, painItems, R.layout.mylistlayout,
//  from, to);
//
//	lv.setAdapter(adapter);
//
//
//	public class CoursesAdapter extends SimpleAdapter
//	{
//		public ArrayList<? extends Map<String, Object>> data =  new ArrayList<>();
//		LayoutInflater inflater;
//		int row;
//		String[] from; int[] to;
//
//		public CoursesAdapter(Activity context, ArrayList<? extends Map<String, Object>> data,
//		  int resource, String[] from, int[] to)
//		{
//			super(context, data, resource, from, to);
//			this.data = data;
//			inflater = context.getLayoutInflater();
//			row = resource;
//			this.from = from;
//			this.to = to;
//		}
//
//		@Override
//		public int getCount()
//		{
//			return data.size();
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent)
//		{
//			//ViewParent row = super.getView(position, convertView, parent);
//			ViewGroup view = (ViewGroup) inflater.inflate(row, parent, false);
//
//			for (int i = 0; i < view.getChildCount(); i++)
//			{
//				View v = view.getChildAt(i);
//				if (v instanceof TextView)
//				{
//					for (int z = 0; z < to.length; z++)
//						if (v.getId() == to[z])
//							((TextView) v).setText(data.get(position).get(from[z]) + "");
//				}
//			}
//
//
//
//			//			TextView rw1 = (TextView)findViewById(ru.maxdestroyer.utils.R.id.row1);
//			//			rw1.setText(map.get(position));
//			return view;
//		}
//
//	}

	// viewpager
	//final ViewPager vp = f(R.id.vpPhoto);
	//							for (int i = 0; i < list.size(); i++)
	//							{
	//								String src = list.get(i).get("avatar").toString();
	//
	//								View page = getActivity().getLayoutInflater().inflate(R.layout.page_iv,
	//								  null);
	//								ImageView iv = (ImageView) page.findViewById(R.id.ivFoto);
	//
	//								ImageLoader.getInstance().displayImage(src, iv);
	//								SamplePagerAdapter ad = (SamplePagerAdapter) vp.getAdapter();
	//								ad.pages.add(page);
	//								ad.notifyDataSetChanged();
	//							}
}
