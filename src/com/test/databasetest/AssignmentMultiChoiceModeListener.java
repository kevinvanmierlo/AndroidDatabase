package com.test.databasetest;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;

public class AssignmentMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener
{
	TestDatabaseActivity host;
	ActionMode activeMode;
	ListView lv;

	AssignmentMultiChoiceModeListener(TestDatabaseActivity host, ListView lv)
	{
		this.host = host;
		this.lv = lv;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu)
	{
		MenuInflater inflater = host.getMenuInflater();

		inflater.inflate(R.menu.context_menu, menu);
		mode.setTitle(R.string.app_name);
		mode.setSubtitle("1 opdracht(en) geselecteerd");
		activeMode = mode;

		return (true);
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu)
	{
		return (false);
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item)
	{
		boolean result = host.performActions(item);

		updateSubtitle(activeMode);

		if (activeMode != null)
		{
			activeMode.finish();
		}

		return (result);
	}

	@Override
	public void onDestroyActionMode(ActionMode mode)
	{
		activeMode = null;
	}

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked)
	{
		updateSubtitle(mode);
	}

	private void updateSubtitle(ActionMode mode)
	{
		mode.setSubtitle(lv.getCheckedItemCount() + " opdracht(en) geselecteerd");
	}
}