package com.bjw.pantechgetupdate;

import com.bjw.pantechgetupdate.R;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddressAcitvity extends Activity
{
	EditText editText;
	Button btnCopy;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_activity);
		editText = (EditText)findViewById(R.id.editText);
		btnCopy = (Button)findViewById(R.id.btnCopy);
		String recdata;
		Bundle extras = getIntent().getExtras();
    	if (extras != null) {
    		recdata = extras.getString("Address");
    		editText.setText(recdata);
    	}
    	btnCopy.setOnClickListener(btnClickListener);
	}

	private Button.OnClickListener btnClickListener = new OnClickListener()
	{	
		@Override
		public void onClick(View v)
		{
			ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
	        ClipData clipUrL = ClipData.newPlainText("URL", editText.getText().toString());
			clip.setPrimaryClip(clipUrL);
			finish();
		}
	};
	
	
}
