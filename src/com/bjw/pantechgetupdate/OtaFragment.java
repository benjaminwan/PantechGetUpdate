package com.bjw.pantechgetupdate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.bjw.bean.ObjCheckRec;
import com.bjw.bean.ObjCheckRecBean;
import com.bjw.bean.ObjCheckSend;
import com.bjw.pantechgetupdate.R;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

public class OtaFragment extends Fragment
{
	View rootView;
	EditText editTextModel,editTextVersion;
	Button btnCheck;
	ProgressBar progressBarCheck;
	GridView gridViewUpdate;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.ota_fragment, container, false);
		btnCheck = (Button)rootView.findViewById(R.id.btnCheck);
		btnCheck.setOnClickListener(new BtnClickEvent());
		
		progressBarCheck=(ProgressBar)rootView.findViewById(R.id.progressBarCheck);
		gridViewUpdate=(GridView)rootView.findViewById(R.id.gridViewUpdate);
		
		editTextModel=(EditText)rootView.findViewById(R.id.editTextModel);
		editTextVersion=(EditText)rootView.findViewById(R.id.editTextVersion);
		editTextModel.setText(Build.MODEL);
		try {
			Class<?> classType = Class.forName("android.os.SystemProperties");
			Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
			String version = (String) getMethod.invoke(classType, new Object[]{"ro.product.software_ver"});
			editTextVersion.setText(version);
		} catch (Exception e) {
		    Log.e("test", e.getMessage(),e);
		}
		
		return rootView;
	}
	
	private class BtnClickEvent implements View.OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (v==btnCheck)
			{
				progressBarCheck.setVisibility(View.VISIBLE);
				btnCheck.setEnabled(false);
				CheckUpdate mCheckUpdate = new CheckUpdate();
				mCheckUpdate.start();
			}
		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 123454321:
				String inf=msg.obj.toString();
				ObjCheckRec mCheckRec = new ObjCheckRec(inf);
				FillGridview(mCheckRec);
				progressBarCheck.setVisibility(View.INVISIBLE);
				btnCheck.setEnabled(true);
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void FillGridview(ObjCheckRec mCheckRec)
	{
		ArrayList<ObjCheckRecBean> mListInfo = mCheckRec.mListInfo;
		List<Map<String, String>> sData;
		
		sData = new ArrayList<Map<String, String>>();
		for (int i = 0; i < mListInfo.size(); i++) {
			HashMap<String,String> map = new HashMap<String,String>();  
            map.put("TagName", mListInfo.get(i).TagName);
            map.put("Value", mListInfo.get(i).Value);
            sData.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(rootView.getContext(),sData, R.layout.info_item,new String[] { "TagName","Value"},new int[] { R.id.textTagName,R.id.textValue});
		gridViewUpdate.setAdapter(adapter);
		gridViewUpdate.setOnItemClickListener(ItemClicklistener);
	}
	
	//表格按下
	private GridView.OnItemClickListener ItemClicklistener = new GridView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			for (int i = 0; i < arg0.getCount(); i++)
			{
				HashMap<String, String> item=(HashMap<String, String>) arg0.getItemAtPosition(i);
				String TagName = (String)item.get("TagName");
				String Value = (String)item.get("Value");
				String otaAddr = "";
				if (TagName.equals("GET_INFO_URL"))
				{
					otaAddr = "http://apkmanager.vegaservice.co.kr/apkmanager/Upload/PKG/" + Value;
					Intent addressIntent = new Intent(rootView.getContext(), AddressAcitvity.class);
					addressIntent.putExtra("Address", otaAddr);
					rootView.getContext().startActivity(addressIntent);
					break;
				}
			}
		}
	};
	
	private class CheckUpdate extends CheckThread
	{

		@Override
		public void onCheckCompleted(String inf)
		{
			Message message = new Message();// 生成消息，并赋予ID值
			message.what = 123454321;
			message.obj=inf;
			mHandler.sendMessage(message);// 投递消息
		}

		@Override
		public void onCheckFail()
		{
			progressBarCheck.setVisibility(View.INVISIBLE);
			btnCheck.setEnabled(true);
		}
		
	}
	
	public abstract class CheckThread extends Thread
	{
		
		@Override
		public void run()
		{
			ObjCheckSend mCheckSend =  new ObjCheckSend(editTextModel.getText().toString(), editTextVersion.getText().toString());

			try{
				String pathUrl = "http://apkmanager.skyservice.co.kr/apkmanager/Process/sky_station_30_server.php";
				URL url=new URL(pathUrl);
				HttpURLConnection httpConn=(HttpURLConnection)url.openConnection();
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				httpConn.setUseCaches(false);
				httpConn.setRequestMethod("POST");
				String requestString = mCheckSend.toString();

				byte[] requestStringBytes = requestString.getBytes("UTF-8");
				httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);
				httpConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=pkgname");
				httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
				httpConn.setRequestProperty("UserAgent", "Dalvik");
				httpConn.setRequestProperty("Accept", "*/*");
				httpConn.setRequestProperty("Charset", "UTF-8");
				 
				OutputStream outputStream = httpConn.getOutputStream();
				outputStream.write(requestStringBytes);
				outputStream.close();

				int responseCode = httpConn.getResponseCode();
				if(HttpURLConnection.HTTP_OK == responseCode)
				{

					StringBuffer sb = new StringBuffer();
					String readLine;
					BufferedReader responseReader;

					responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
					while ((readLine = responseReader.readLine()) != null)
					{
						sb.append(readLine).append("\n");
					}
					onCheckCompleted(sb.toString());

					responseReader.close();
				}else {
					onCheckFail();
				}
				
				}catch(Exception ex){
					ex.printStackTrace();
				}
			
		}
		
		public abstract void onCheckCompleted(String inf);
		public abstract void onCheckFail();
	}
	
}
