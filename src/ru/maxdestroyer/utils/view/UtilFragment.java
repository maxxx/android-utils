package net.malahovsky.utils.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import net.malahovsky.utils.Util;

public class UtilFragment extends Fragment implements View.OnClickListener
{
	protected Context context;
	protected View fv = null;

	public UtilFragment()
	{
	}

	@SuppressLint("ValidFragment")
	public UtilFragment(Context context)
	{
	   this.context = context;
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//	  Bundle savedInstanceState) {
//
//		View rootView = inflater.inflate(R.layout.f_settings, container, false);
//
//		return rootView;
//	}

	@Override
	public void onClick(View v)
	{

	}

	@Override
	public void onDetach()
	{
		super.onDetach();
		Util.HideKeyboard(getActivity());
	}

	protected <T> T f(int id)
	{
		if (fv != null)
			return f(fv, id);
		if (getView() == null)
			return null;
		return (T)getView().findViewById(id);
	}

	protected <T> T f(View fv, int id)
	{
		if (fv == null)
			return f(id);
		return (T)fv.findViewById(id);
	}

	protected void Hide(View fv, int id)
	{
		if (fv != null)
		{
			fv.findViewById(id).setVisibility(View.GONE);
		}
		else
			getView().findViewById(id).setVisibility(View.GONE);
	}

	protected void Hide(int id)
	{
		if (fv != null)
		{
			Hide(fv, id);
		}else
			getView().findViewById(id).setVisibility(View.GONE);
	}

	protected void Hide(View v)
	{
		v.setVisibility(View.GONE);
	}

	protected void Hide2(View fv, int id)
	{
		if (fv != null)
		{
			fv.findViewById(id).setVisibility(View.INVISIBLE);
		}
		else
			getView().findViewById(id).setVisibility(View.INVISIBLE);
	}

	public void Show(View fv, int id)
	{
		if (fv != null)
		{
			fv.findViewById(id).setVisibility(View.VISIBLE);
		}
		else
			getView().findViewById(id).setVisibility(View.VISIBLE);
	}

	public void Show(int id)
	{
		getView().findViewById(id).setVisibility(View.VISIBLE);
	}

	protected boolean isFragmentUIActive()
	{
		return isAdded() && !isDetached() && !isRemoving() && getActivity() != null;
	}

	protected void MSG(String msg)
	{
		Util.MSG(context, msg);
	}
}
