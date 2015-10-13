package ru.maxdestroyer.utils.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.maxdestroyer.utils.R;
import ru.maxdestroyer.utils.Util;

public class IphoneDialog extends Dialog
{
	private final Activity activity;

	public IphoneDialog(Activity context)
	{
		super(context);
		activity = context;
	}

	public void show(String title, String msg)
	{
		init(activity, title, msg);
	}


//	protected IphoneDialog(Context context, int theme)
//	{
//		super(context, theme);
//		init(context);
//	}
//
//	protected IphoneDialog(Context context, boolean cancelable, OnCancelListener cancelListener)
//	{
//		super(context, cancelable, cancelListener);
//		init(context);
//	}

	private void init(Activity context, String title, String msg)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.d_iphone);
		WindowManager.LayoutParams wmlp = getWindow().getAttributes();
		getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		//dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		wmlp.gravity = Gravity.CENTER;
		wmlp.width = Util.GetScreenWidth(context) - 100;

		super.show();

		TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
		TextView tvMsg = (TextView) findViewById(R.id.tvMsg);
		TextView tvCancel = (TextView) findViewById(R.id.tvCancel);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.rlPopup);

		if (!title.equals(""))
			tvTitle.setText(title);
		else
			tvTitle.setVisibility(View.GONE);
		tvMsg.setText(msg);
		tvCancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				dismiss();
			}
		});
		Util.SetOpacity(rl, 0.8f);
	}
}
