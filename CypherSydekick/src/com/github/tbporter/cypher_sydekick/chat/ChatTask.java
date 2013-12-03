package com.github.tbporter.cypher_sydekick.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import org.apache.http.protocol.HTTP;

import com.github.tbporter.cypher_sydekick.R;
import com.github.tbporter.cypher_sydekick.crypt.Crypt;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class ChatTask extends AsyncTask<String, Void, String> {
	static final String SERVER_URL_DEFAULT = "http://ayelix.dnsdynamic.com/";
	private ArrayList<ConversationItem> activityConversationItems_;
	private String sender_;
	private String recipient_;
	private ConversationAdapter convAdapter_;
	
	
	// Might have to add an instance of the Crypt class if we want to do encryption and decription in this class
	public ChatTask(){
		activityConversationItems_ = null;
	}
	
	// Only need to make one instance of this class since the conversation items list array ref remains the same
	public ChatTask(ConversationAdapter adapter, ArrayList<ConversationItem> convItems){
		convAdapter_ = adapter;
		activityConversationItems_ = convItems;
	}
	
	// The two following methods are called when chatting with a new user is initiated
	public void setSender(String sender){
		sender_ = sender;
	}
	
	public void setRecipient(String recipient){
		recipient_ = recipient;
	}
	
	@Override
	protected void onPreExecute() {

	}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String url;
		try {
			if(params[0].equals("send-message")){	// send
				String message;
				// Encryption is done here
				byte[] encryptedBytes = Crypt.encrypt(Base64.decode(params[4], Base64.DEFAULT), Base64.decode(params[3], Base64.DEFAULT));
				message = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
				url = SERVER_URL_DEFAULT + "messages?action=send&sender="+params[1].trim() + "&recipient="+params[2].trim() + "&message="+URLEncoder.encode(message, HTTP.UTF_8);
				HttpURLConnection connection = (HttpURLConnection)(new URL(url).openConnection());
	            connection.setRequestMethod("GET");
	            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.114 Safari/537.36");
	    		connection.connect();
	    		String content = getContents(connection);
	    		connection.disconnect();
			}
			else if(params[0].equals("receive-message")){	// receive
				if(!params[1].equals("") && !params[2].equals("")){
					sender_ = params[2].trim();
					url = SERVER_URL_DEFAULT + "messages?action=receive&sender="+params[2].trim() + "&recipient="+params[1].trim();
					HttpURLConnection connection = (HttpURLConnection)(new URL(url).openConnection());
		            connection.setRequestMethod("GET");
		            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
	                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.114 Safari/537.36");
		    		connection.connect();
		    		String message = getContents(connection);
		    		connection.disconnect();
		    		if(message.contains("Error")){
		    			return null;
		    		}
		    		else{
		    			message = message.substring(message.indexOf(".")+1);
		    			
		    			return message;
		    		}
				}
			}
			else{	// add user
				url = SERVER_URL_DEFAULT + "users?username=" + params[0];
				HttpURLConnection connection = (HttpURLConnection)(new URL(url).openConnection());
	            connection.setRequestMethod("GET");
	            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.114 Safari/537.36");
	    		connection.connect();
	    		String content = getContents(connection);
	    		connection.disconnect();
			}
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		        e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if(result != null){
			Log.d("NewMessage", result);
			if(activityConversationItems_ != null){
				ConversationItem newItem = new ConversationItem();
     			newItem.setMessage(result);
     			newItem.setSubtitle("Received from " + sender_);
     			newItem.setIcon(R.drawable.ic_action_person);
				activityConversationItems_.add(newItem);
				convAdapter_.notifyDataSetChanged();
			}
		}
	}
	
	private String getContents(HttpURLConnection connection) throws IOException {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder htmlStringBuilder = new StringBuilder();
        String htmlBuffer;
        while((htmlBuffer = inReader.readLine()) != null){
            htmlStringBuilder.append(htmlBuffer);
        }

        return htmlStringBuilder.toString();
    }
}
