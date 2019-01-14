
package com.searchplace.source.network;

import com.searchplace.source.local_objects.Place;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

public final class ResponseParser
{
	public static void parseSearchResult(String response, Vector<Place> vv)
	{
		try
		{
			JSONObject jObj = new JSONObject(response);
			response = jObj.getString("response");
			jObj = new JSONObject(response);
			JSONArray jArr = jObj.getJSONArray("venues");
			for (int i=0; i < jArr.length(); i++)
			{
				JSONObject obj = jArr.getJSONObject(i);

				Place org = new Place();

				org.set_id(obj.getString("id"));
				org.set_name(obj.getString("name"));

				String locStr = obj.getString("location");
				JSONObject objj = new JSONObject(locStr);
				org.set_distance(objj.getString("distance"));
				String address = objj.getString("formattedAddress");
				address = address.replaceAll("]", "");
				address = address.replaceAll("\"", "");
				if(address.length() > 0)
				{
					int index = address.indexOf("[");
					address = address.substring(index+1);
				}
				org.set_address(address);

				vv.addElement(org);

			}
		}
		catch(Exception e)
		{
			System.out.println("Exception: " + e + " ::ResponseParser::parseSearchResult");
		}
	}

}