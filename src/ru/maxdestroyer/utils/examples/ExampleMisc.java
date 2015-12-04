package ru.maxdestroyer.utils.examples;

public class ExampleMisc
{

	// edittext - on action
//	editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
//
//	{
//		@Override
//		public boolean onEditorAction (TextView v,int actionId, KeyEvent event){
//		if (actionId == EditorInfo.IME_ACTION_SEARCH)
//		{
//			performSearch();
//			return true;
//		}
//		return false;
//	}
//	}
//
//	);

	// GSON
	//Goal b = new GsonBuilder().create().fromJson(obj.toString(), Goal.class);

	// custom dialog
//	final UtilDial dialog = new UtilDial(this);
//	dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//	dialog.setContentView(R.layout.d_);
//	dialog.setTitle("Title...");
//
//	TextView text = (TextView) dialog.findViewById(R.id.text);
//	text.setText("Android custom dialog example!");
//	ImageView image = (ImageView) dialog.findViewById(R.id.image);
//	image.setImageResource(R.drawable.ic_launcher_def);
//
//	Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);

//	dialogButton.setOnClickListener(new OnClickListener() {
//	@Override
//	public void onClick(View v) {
//		dialog.dismiss();
//	}
//});
//
//	dialog.show();




//	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			switch (which){
//				case DialogInterface.BUTTON_POSITIVE:
//					//Yes button clicked
//					break;
//
//				case DialogInterface.BUTTON_NEGATIVE:
//					//No button clicked
//					break;
//			}
//		}
//	};
//
//	AlertDialog.Builder builder = new AlertDialog.Builder(this);
//	LayoutInflater inflater = getLayoutInflater();
//	View dialoglayout = inflater.inflate(R.layout.d_x, null);
//	builder.setView(dialoglayout);
//	builder.setMessage("Are you sure?").setPositiveButton("OK", dialogClickListener)
//	.setNegativeButton("������", dialogClickListener);
//	AlertDialog ad = builder.create();
//	ad.requestWindowFeature(Window.FEATURE_NO_TITLE);
//	ad.show();
}
