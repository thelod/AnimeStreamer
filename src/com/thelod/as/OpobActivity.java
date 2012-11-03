package com.thelod.as;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
	private EditText currentPage;
	private ListView listSeries;
	private ArrayAdapter<String> adapterSeries;
	private int currentView = 0;
	
    TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener(){
    	public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
 		   if (actionId == EditorInfo.IME_ACTION_DONE) { 
				try {
					loadFlash(getEpisodes(exampleView.getText().toString()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//match this behavior to your 'Send' (or Confirm) button
				Log.d("pew", exampleView.getText().toString());
				InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				in.hideSoftInputFromWindow(currentPage.getApplicationWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);
 		   }
 		   return true;
    	}
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        adapterSeries = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        buttonPrev = (Button)findViewById(R.id.button1);
        buttonNext = (Button)findViewById(R.id.button2);
        currentPage = (EditText)findViewById(R.id.txtEpisode);
        listSeries = (ListView)findViewById(R.id.lstSeries);
        listSeries.setAdapter(adapterSeries);
        listSeries.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				try {
					Log.d("DEBUG", "CurrentView"+currentView);
					
					if(currentView == 0){
						getEpisodes(adapterSeries.getItem(position));
					}
					else if(currentView == 1){
						loadFlash(getVideos(adapterSeries.getItem(position)));
						listSeries.setVisibility(View.GONE);
					}
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
          });
        //listSeries.setVisibility(View.GONE);
        currentPage.setOnEditorActionListener(exampleListener);
        
        mWebView = (WebView) findViewById(R.id.webview1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginsEnabled(true);
        
        buttonPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String number = currentPage.getText().toString();
            	int tmp = Integer.parseInt(number)-1;
            	currentPage.setText(String.valueOf(tmp));
            	try {
					loadFlash(getEpisodes(currentPage.getText().toString()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            }
          });
        
        buttonNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	String number = currentPage.getText().toString();
            	int tmp = Integer.parseInt(number)+1;
            	currentPage.setText(String.valueOf(tmp));
            	try {
					loadFlash(getEpisodes(currentPage.getText().toString()));
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
    

    
    public void loadFlash(String flashID)
    {
    	String data = flashID;
        mWebView.loadData("<script>document.write(unescape('" + data + "'));</script>", "text/html; charset=UTF-8", null);
    }
    
    public String getEpisodes(String series) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://www.animeseason.com/"+series+"/"));
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
            String page = sb.toString();
            Pattern pattern = Pattern.compile("<td class=\"text_center\"><a href=\"/([^/]+)/\">([^<]+)</a></td><td><a href=\"/([^\"]+)/\">([^<]+)</a></td>");
            Matcher matcher = pattern.matcher(page);
            String res = "-1";
            adapterSeries.clear();
            while(matcher.find()){
            	adapterSeries.add(matcher.toMatchResult().group(1));
                //res += matcher.toMatchResult().group(0);
            }
            currentView = 1;
            return res;
            } finally {
            if (in != null) {
                try {
                    in.close();
                    } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        }
            
    }
    
    public String getVideos(String episode) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://www.animeseason.com/"+episode+"/"));
            Log.d("DEBUG", "http://www.animeseason.com/"+episode+"/");
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
            String page = sb.toString();
            Pattern pattern = Pattern.compile("<a href=\"#\" onclick=\"show_player\\('player.', '([^']+)'\\);return false\">([^<]+)</a></li>");
            Matcher matcher = pattern.matcher(page);
            String res = "-1";
            
            if (matcher.find()){
                res = matcher.toMatchResult().group(1);
                pattern = Pattern.compile("(%.)");
                matcher = pattern.matcher(res);
                res = matcher.replaceAll("%");
                //Log.d("DEBUG", "pew"+res);
            }
            //Log.d("DEBUG", res);
            return res;
            } finally {
            if (in != null) {
                try {
                    in.close();
                    } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        }
            
    }
    
    public String getSeries() throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://www.animeseason.com/anime-list/"));
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
            String page = sb.toString();
            Pattern pattern = Pattern.compile("<a href=\"/([\\w-]+)/\">[^<]+</a>");
            Matcher matcher = pattern.matcher(page);
            String res = "";
            adapterSeries.clear();
            while(matcher.find()){
            	adapterSeries.add(matcher.toMatchResult().group(1));
                //res += matcher.toMatchResult().group(0);
            }
            return res;
            } finally {
            if (in != null) {
                try {
                    in.close();
                    } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        }
    }
   }
