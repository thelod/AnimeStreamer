package com.thelod.as;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.webkit.WebView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
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
        buttonPrev = (Button)findViewById(R.id.button1);
        buttonNext = (Button)findViewById(R.id.button2);
        currentPage = (EditText)findViewById(R.id.txtEpisode);
        
        currentPage.setOnEditorActionListener(exampleListener);

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
			test = getEpisodes("132");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			test = e.getMessage();
			e.printStackTrace();
		}

    }
    

    
    public void loadFlash(String flashID)
    {
    	String data = "<embed width=\"480\" height=\"420\" flashvars=\"file=http://www.onepieceofbleach.com/sapo.php?id=" + flashID + "&amp;provider=http&amp;http.startparam=start&amp;bufferlength=5\" allowfullscreen=\"true\" allowscriptaccess=\"always\" bgcolor=\"\"  type=\"application/x-shockwave-flash\" src=\"http://onepieceofbleach.com/player.swf\"/>";
        mWebView = (WebView) findViewById(R.id.webview1);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setPluginsEnabled(true);
        mWebView.loadData(data, "text/html; charset=UTF-8", null);
    }
    
    public String getEpisodes(String episode) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://onepieceofbleach.com/one-piece-"+episode+"/"));
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
            Pattern pattern = Pattern.compile("sapo\\.php\\?id=(\\w+)");
            Matcher matcher = pattern.matcher(page);
            String res = "-1";
            if (matcher.find()){
                res = matcher.toMatchResult().group(1);
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
