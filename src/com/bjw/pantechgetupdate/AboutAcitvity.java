package com.bjw.pantechgetupdate;

import com.bjw.pantechgetupdate.R;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class AboutAcitvity extends Activity
{
	TextView txtVersion;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		txtVersion = (TextView)findViewById(R.id.txtVersion);
		String appName = getString(R.string.app_name);
        try {
			PackageInfo pinfo = getPackageManager().getPackageInfo("com.bjw.pantechgetupdate", PackageManager.GET_CONFIGURATIONS);
			String versionName = pinfo.versionName;
//			String versionCode = String.valueOf(pinfo.versionCode);
			txtVersion.setText(appName+" v"+versionName);
        } catch (NameNotFoundException e) {
        	e.printStackTrace();
        }
	}

}
