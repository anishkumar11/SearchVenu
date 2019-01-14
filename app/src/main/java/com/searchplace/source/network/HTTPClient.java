package com.searchplace.source.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class HTTPClient
{
	public static Context _appContext;

    private static boolean haveNetworkConnection()
    {
    	try
    	{
    		ConnectivityManager connectivity = (ConnectivityManager) _appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null)
            {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null)
                    for (int i = 0; i < info.length; i++)
                        if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        {
                            return true;
                        }

            }
    	}
    	catch(Exception e)
        {
            System.out.println("Exception: "+e  + " ::HTTPClient::haveNetworkConnection");
        }

          return false;
    }

    public static OkHttpClient getClient()
	{
		try {
			OkHttpClient client = new OkHttpClient.Builder()
					.connectTimeout(10, TimeUnit.SECONDS)
					.writeTimeout(10, TimeUnit.SECONDS)
					.readTimeout(30, TimeUnit.SECONDS)
					.build();

			return client;
		}
		catch(Exception e)
		{
			System.out.println("Exception: "+e  + " ::HTTPClient::getClient");
		}
		return null;
	}

	public static byte[] getViaHttpPostConnection(String url, RequestBody nameValuePairs, HTTPThread thread)
    {
    	byte[] data = null;

    	try
    	{
    		//url
    		System.out.println("getViaHttpConnection::url = " + url);
			OkHttpClient client = getClient();
			Request request = null;



			if(nameValuePairs != null)
			{
				//System.out.println("getViaHttpPostConnection::nameValuePairs = " + nameValuePairs.toString());
				request = new Request.Builder()
						.addHeader("Content-type", "application/json")
				        .url(url)
				        .post(nameValuePairs)
				        .build();
			}

			Call httpRequest = client.newCall(request);
			if(thread != null)
			{
				thread.setHttpCallRequest(httpRequest);
			}

			Response response = httpRequest.execute();
		    if (response.isSuccessful())
		    {
		    	data = response.body().bytes();
                System.out.println("response = " + new String(data));
		    }
			else
			{
				System.out.println("response = " + response.body().string());
			}
			response.close();
    	}
    	catch(Exception e)
    	{
            System.out.println("Exception: "+e + " ::HTTPClient::getViaHttpPostConnection");
        }
    	return data;
    }

	public static byte[] getViaHttpPostConnection1(String url, RequestBody nameValuePairs, HTTPThread thread)
	{
		byte[] data = null;

		try
		{
			//url
			System.out.println("getViaHttpPostConnection1::url = " + url);
			OkHttpClient client = getClient();
			Request request = null;



			if(nameValuePairs != null)
			{
				/*request = new Request.Builder()
						.addHeader("Content-type", "application/x-www-form-urlencoded")
						.url(url)
						.post(nameValuePairs)
						.build();*/

				request = new Request.Builder()
						.url(url)
						.post(nameValuePairs)
						.build();
			}

			Call httpRequest = client.newCall(request);
			if(thread != null)
			{
				thread.setHttpCallRequest(httpRequest);
			}

			Response response = httpRequest.execute();
			if (response.isSuccessful())
			{
				data = response.body().bytes();
				System.out.println("response = " + new String(data));
			}
			else
			{
				System.out.println("response = " + response.body().string());
			}
			response.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception: "+e + " ::HTTPClient::getViaHttpPostConnection1");
		}
		return data;
	}

	public static byte[] getViaHttpConnection(String url, HTTPThread thread)
    {
    	byte[] data = null;

    	try
    	{
    		//url
			System.out.println("getViaHttpConnection::url = " + url);
			OkHttpClient client = getClient();

			Request request = new Request.Builder()
				    .addHeader("Content-type", "application/json")
				    .url(url)
				    .build();

			Call httpRequest = client.newCall(request);
			if(thread != null)
			{
				thread.setHttpCallRequest(httpRequest);
			}
			Response response = httpRequest.execute();
		    if (response.isSuccessful())
		    {
		    	data = response.body().bytes();
                System.out.println("response = " + new String(data));
		    }
		    else
		    {
		    	System.out.println("response = " + response.body().string());
		    }
			response.close();
    	}
    	catch(Exception e)
    	{
            System.out.println("Exception: "+e + " ::HTTPClient::getViaHttpConnection");
        }
    	return data;
    }



	public static byte[] uploadImageViaHttpPostConnection(String url, RequestBody nameValuePairs)
	{
		byte[] data = null;

    	try
    	{
    		//url
			System.out.println("uploadImageViaHttpPostConnection::url = " + url);
			OkHttpClient client = getClient();
			Request request = null;



			if(nameValuePairs != null)
			{
				System.out.println("uploadImageViaHttpPostConnection::nameValuePairs = " + nameValuePairs.toString());
				request = new Request.Builder()
				        .url(url)
				        .post(nameValuePairs)
				        .build();
			}

			Response response = client.newCall(request).execute();
		    if (response.isSuccessful())
		    {
		    	data = response.body().bytes();
                System.out.println("response = " + new String(data));
		    }
			response.close();
    	}
    	catch(Exception e)
    	{
            System.out.println("Exception: "+e + " ::HTTPClient::uploadImageViaHttpPostConnection");
        }
    	return data;
	}
}