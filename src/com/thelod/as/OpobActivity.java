package com.thelod.as;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.webkit.WebView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.ListView;
import android.view.KeyEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.thelod.as.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OpobActivity extends Activity {
	private WebView mWebView;
	public String currentFlash;
	private Button buttonPrev;
	private Button buttonNext;
	private Spinner currentVideos;
	private ListView listSeries;
	private ArrayAdapter<String> adapterSeries;
	private ArrayAdapter<String> adapterEpisodes;
	
	private int currentView = 0;
	
	public class episodeMirror{
		String MirrorName = "";
		String MirrorData = "";
		public episodeMirror(String inName, String inData)
		{
			MirrorName = inName;
			MirrorData = inData;
		}
		@Override
		public String toString()
		{
			return MirrorName;
		}
	}
	
	private ArrayAdapter<episodeMirror> adapterVideos;
	private int currentEpisode = 0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        adapterSeries = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        adapterEpisodes = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        adapterVideos = new ArrayAdapter<episodeMirror>(this, R.layout.spinnerlayout, android.R.id.text1);

        buttonPrev = (Button)findViewById(R.id.button1);
        buttonNext = (Button)findViewById(R.id.button2);
        currentVideos = (Spinner)findViewById(R.id.videos);
        currentVideos.setAdapter(adapterVideos);
        currentVideos.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				loadFlash(adapterVideos.getItem(position).MirrorData);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
        	
        });
        
        
        listSeries = (ListView)findViewById(R.id.lstSeries);
        listSeries.setAdapter(adapterSeries);
        listSeries.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					Log.d("DEBUG", "CurrentView"+currentView);
					
					if(currentView == 0){
						getEpisodes(adapterSeries.getItem(position));
						listSeries.setAdapter(adapterEpisodes);
			            currentView = 1;
					}
					else if(currentView == 1){
						currentEpisode = position;
						checkNextPrev();
						loadFlash(getVideos(adapterEpisodes.getItem(position)));
						listSeries.setVisibility(View.GONE);
			            currentView = 2;
						
					}
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
          });
        
        
        //listSeries.setVisibility(View.GONE);
        
        mWebView = (WebView) findViewById(R.id.webview1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginsEnabled(true);
        
        buttonPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
					loadFlash(getVideos(adapterEpisodes.getItem(currentEpisode+1)));
					currentEpisode+=1;
					checkNextPrev();
					Log.d("DEBUG", ""+currentEpisode+" max:"+adapterEpisodes.getCount());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
          });
        
        buttonNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	try {
					loadFlash(getVideos(adapterEpisodes.getItem(currentEpisode-1)));
					currentEpisode-=1;
					checkNextPrev();
					Log.d("DEBUG", ""+currentEpisode+" max:"+adapterEpisodes.getCount());
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            }
          });
        String test ="";
        try {
			test = getSeries();
			mWebView.loadData(test, "text/html; charset=UTF-8", null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			test = e.getMessage();
			e.printStackTrace();
		}

    }
    
    
    /**
     * enable/disable next/prev button if possible
     */
    public void checkNextPrev()
    {
    	if(adapterEpisodes.getCount() >= currentEpisode+2)
    	{
    		buttonPrev.setEnabled(true);
    	}
    	else
    	{
    		buttonPrev.setEnabled(false);
    	}
    	
    	if(currentEpisode > 0)
    	{
    		buttonNext.setEnabled(true);
    	}
    	else
    	{
    		buttonNext.setEnabled(false);
    	}
    }
    
    public void loadFlash(String flashID)
    {
    	String data = flashID;
        mWebView.loadData("<script>document.write(unescape('" + data + "'));</script>", "text/html; charset=UTF-8", null);
    }
    
    public void clearWebView()
    {
    	mWebView.loadData("blub", "text/html; charset=UTF-8", null);
    }
    
    
    @Override
    public void onBackPressed() {
    	//close on anime list
    	if(currentView == 0)
    	{
    		finish();
    	}
    	//go back to anime list
    	if(currentView == 1)
    	{
			listSeries.setAdapter(adapterSeries);
            currentView = 0;
    	}
    	//go back to episode list
    	if(currentView == 2)
    	{
			clearWebView();
			listSeries.setVisibility(View.VISIBLE);
            currentView = 1;
    	}
    return;
    }
    
    /**
     * retrieve all episodes of specified series
     */
    public String getEpisodes(String series) throws Exception {
        	//retrieve html code
    		String page = getWebPage("http://www.animeseason.com/"+series+"/");
            
            //create a regex search pattern which finds all episodes
            Pattern pattern = Pattern.compile("<td class=\"text_center\"><a href=\"/([^/]+)/\">([^<]+)</a></td><td><a href=\"/([^\"]+)/\">([^<]+)</a></td>");
            Matcher matcher = pattern.matcher(page);
            String res = "-1";
            adapterEpisodes.clear();
            
            while(matcher.find()){
            	adapterEpisodes.add(matcher.toMatchResult().group(1));
                //res += matcher.toMatchResult().group(0);
            }

            return res;
    }
    
    /**
     * retrieve all video mirrors of specified episode
     */
    public String getVideos(String episode) throws Exception {
    		//retrieve html code
            String page = getWebPage("http://www.animeseason.com/"+episode+"/");
            
            //create a regex search pattern which finds all video mirrors
            Pattern pattern = Pattern.compile("<a href=\"#\" onclick=\"show_player\\('player.', '([^']+)'\\);return false\">([^<]+)</a></li>");
            Matcher matcher = pattern.matcher(page);
            String res = "-1";
            adapterVideos.clear();
            
            while (matcher.find()){
                res = matcher.toMatchResult().group(1);
                Pattern pattern2 = Pattern.compile("(%.)");
                Matcher matcher2 = pattern2.matcher(res);
                res = matcher2.replaceAll("%");
                adapterVideos.add(new episodeMirror(matcher.toMatchResult().group(2), res));
                //Log.d("DEBUG", "pew"+res);
            }
            
            //if multiple video mirrors find return the first one
            if(adapterVideos.getCount() > 0)
            {
            	return adapterVideos.getItem(0).MirrorData;
            }
            else //check if a single video without mirrors exists
            {
            	//create a regex search pattern which finds a single video
            	pattern = Pattern.compile("unescape\\(escapeall\\('([^']+)'");
            	matcher = pattern.matcher(page);
            	if(matcher.find())
            	{
	            	res = matcher.toMatchResult().group(1);
	                Pattern pattern2 = Pattern.compile("(%.)");
	                Matcher matcher2 = pattern2.matcher(res);
	                res = matcher2.replaceAll("%");
	            	adapterVideos.add(new episodeMirror("no mirrors", res));
            	}
            }
            //Log.d("DEBUG", res);
            return res;
            
    }
    
    /**
     * retrieve anime list
     */
    public String getSeries() throws Exception {
        	//retrieve html code
        	String page = getWebPage("http://www.animeseason.com/anime-list/");
            
        	//create a regex search pattern which returns the anime list
            Pattern pattern = Pattern.compile("<a href=\"/([\\w-]+)/\">[^<]+</a>");
            Matcher matcher = pattern.matcher(page);
            String res = "";
            adapterSeries.clear();
            while(matcher.find()){
            	String test = matcher.toMatchResult().group(1);
            	if(!test.startsWith("anime-list"))
            		adapterSeries.add(matcher.toMatchResult().group(1));
                //res += matcher.toMatchResult().group(0);
            }
            return res;
            
        }
    
    /**
     * retrieve html code of the specified address
     */
    public String getWebPage(String address) throws Exception
    {
    	BufferedReader in = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet();
        request.setURI(new URI(address));
        HttpResponse response = client.execute(request);
        in = new BufferedReader
        (new InputStreamReader(response.getEntity().getContent()));
        StringBuffer sb = new StringBuffer("");
        String line = "";
        String NL = System.getProperty("line.separator");
        while ((line = in.readLine()) != null) {
            sb.append(line + NL);
        }
        in.close();
        return sb.toString();
    }
   }
