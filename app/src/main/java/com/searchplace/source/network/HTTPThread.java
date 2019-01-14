
package com.searchplace.source.network;

import android.content.Context;
import okhttp3.Call;

public class HTTPThread extends Thread
{
    private boolean hasStarted;
    private Context _tagContext;
    private String _tag = "";
    private Call _httpCall;

    public HTTPThread()
    {
        super();
        hasStarted = false;
    }

    public void set_tag(String value)
    {
        _tag = value;
    }

    public String get_tag()
    {
        return _tag;
    }

    public Call getHttpCallRequest()
    {
    	return _httpCall;
    }

    public void setHttpCallRequest(Call value)
    {
    	_httpCall = value;
    }

    public Context getTag()
    {
    	return _tagContext;
    }

    public void setTag(Context tag)
    {
    	_tagContext = tag;
    }

    public boolean getStatus()
    {
       return hasStarted;
    }

    public void setStatus(boolean s)
    {
        hasStarted = s;
    }
}
