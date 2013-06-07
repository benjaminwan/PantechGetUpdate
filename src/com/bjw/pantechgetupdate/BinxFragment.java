package com.bjw.pantechgetupdate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.bjw.bean.ObjInfBean;
import android.content.Intent;
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
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

public class BinxFragment extends Fragment
{
	private String infAddr = "http://dmfile.curitel.com/self_binary/sky_binary/real/download.inf";
	private Button btnGetAllModel;
	GridView gridViewAllModel;
	ProgressBar	progressBarDLInf;
	View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		rootView = inflater.inflate(R.layout.binx_fragment, container, false);
		btnGetAllModel = (Button)rootView.findViewById(R.id.btnGetAllModel);
		gridViewAllModel = (GridView)rootView.findViewById(R.id.gridViewAllModel);
		progressBarDLInf = (ProgressBar)rootView.findViewById(R.id.progressBarDLInf);
		btnGetAllModel.setOnClickListener(new BtnClickEvent());
		return rootView;
	}
	private class BtnClickEvent implements View.OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if (v==btnGetAllModel)
			{
				progressBarDLInf.setVisibility(View.VISIBLE);
				btnGetAllModel.setEnabled(false);
				GetInfFile mGetInfFile = new GetInfFile(infAddr);
				mGetInfFile.start();
			}
			
		}
	}
	
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 123454321:
				String inf=msg.obj.toString();
				FillGridview(inf);
				progressBarDLInf.setVisibility(View.INVISIBLE);
				btnGetAllModel.setEnabled(true);
				break;
			}
			super.handleMessage(msg);
		}
	};
	ArrayList<ObjInfBean> mListInfo = null;
	private void FillGridview(String inf)
	{
		String[] Lines=inf.split("\n");
		ObjInfBean mInfBean;
		mListInfo = new ArrayList<ObjInfBean>();
		String[] values;
		if (Lines.length==0)
		{
			return;
		}
		
		for (int i = 0; i < Lines.length; i++)
		{
			String Line = Lines[i].trim();
			if (Line.startsWith("[") && Line.endsWith("]"))
            {
                mInfBean = new ObjInfBean();
                mInfBean.Model = Line.replace("[", "").replace("]", "");
                
                if ((i+1) >= Lines.length)
                	return;
                Line = Lines[i+1];
                values = Line.split("=");
                mInfBean.Version = values[1];
                
                if ((i+2) >= Lines.length)
    				return;
                Line = Lines[i+2];
                values = Line.split("=");
                mInfBean.FileName = values[1];
                
                if ((i+3) >= Lines.length)
    				return;
                Line = Lines[i+3];
                values = Line.split("=");
                mInfBean.FSVersion = values[1];

                if ((i+4) >= Lines.length)
    				return;
                Line = Lines[i+4];
                values = Line.split("=");
                mInfBean.NVVersion = values[1];

                if ((i+5) >= Lines.length)
    				return;
                Line = Lines[i+5];
                values = Line.split("=");
                mInfBean.Size = values[1];

                if ((i+6) >= Lines.length)
    				return;
                Line = Lines[i+6];
                values = Line.split("=");
                mInfBean.CRC = values[1];

                mListInfo.add(mInfBean);
            }
		}

		List<Map<String, String>> sData;
		
		sData = new ArrayList<Map<String, String>>();
		for (int i = 0; i < mListInfo.size(); i++) {
			HashMap<String,String> map = new HashMap<String,String>();  
            map.put("Model", mListInfo.get(i).Model);
            map.put("Version", mListInfo.get(i).Version);
            map.put("FileName", mListInfo.get(i).FileName);
            map.put("FSVersion", mListInfo.get(i).FSVersion);
            map.put("NVVersion", mListInfo.get(i).NVVersion);
            map.put("Size", mListInfo.get(i).Size);
            map.put("CRC", mListInfo.get(i).CRC);
            sData.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(rootView.getContext(),sData, R.layout.model_item,new String[] { "Model","Version","FileName","FSVersion","NVVersion","Size","CRC"},new int[] { R.id.text1Model,R.id.textValue,R.id.text3FileName,R.id.text4FSVersion,R.id.text5NVVersion,R.id.text6Size,R.id.text7CRC });

		gridViewAllModel.setAdapter(adapter);

		gridViewAllModel.setOnItemClickListener(ItemClicklistener);
	}

	private GridView.OnItemClickListener ItemClicklistener = new GridView.OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		{
			HashMap<String, String> item=(HashMap<String, String>) arg0.getItemAtPosition(arg2);
			String Model = (String)item.get("Model");
			String FileName = (String)item.get("FileName");
			String infAddr="http://dmfile.curitel.com/self_binary/sky_binary/real/" + Model + "/" + FileName;
			Intent addressIntent = new Intent(rootView.getContext(), AddressAcitvity.class);
			addressIntent.putExtra("Address", infAddr);
			rootView.getContext().startActivity(addressIntent);
		}
	};
	private class GetInfFile extends GetInfThread
	{

		public GetInfFile(String sUrl)
		{
			super(sUrl);
		}

		@Override
		public void onInfDownloadCompleted(String inf)
		{
			Message message = new Message();
			message.what = 123454321;
			message.obj=inf;
			mHandler.sendMessage(message);
		}

		@Override
		public void onInfDownloadFailed()
		{
			progressBarDLInf.setVisibility(View.INVISIBLE);
			btnGetAllModel.setEnabled(true);
		}
	}
	
	
	public abstract class GetInfThread extends Thread
	{
		private String url;
		
		public GetInfThread(String sUrl)
		{
			url = sUrl;
		}
		
		@Override
		public void run()
		{
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			HttpResponse response;
			ByteArrayOutputStream mOutputStream = null;
			try
			{
				response=client.execute(get);
				HttpEntity entity = response.getEntity();
//				long length = entity.getContentLength();
				InputStream inputStream = entity.getContent();
				mOutputStream = new ByteArrayOutputStream();
				int count = 0;
				if (inputStream != null)
				{
					byte[] buffer = new byte[512];
					int ch =-1;
					
					while ((ch=inputStream.read(buffer))>0)
					{
						mOutputStream.write(buffer, 0, ch);
						count += ch;
						Log.d("BJW", "downloading..."+String.valueOf(count));
					}
				}
				
			} catch (ClientProtocolException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			if (mOutputStream!=null)
			{
				String inf= mOutputStream.toString();
				onInfDownloadCompleted(inf);
			}else {
				onInfDownloadFailed();
			}
			
		}
		
		public abstract void onInfDownloadCompleted(String inf);
		public abstract void onInfDownloadFailed();
	}
	
	
	
}
