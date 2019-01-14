
package com.searchplace.source.network;

import java.util.Vector;

import android.content.Context;

import okhttp3.Call;

public class ThreadMananger extends Thread
{
    private static ThreadMananger _this = null;
    private Vector<HTTPThread> _ThreadsV;
    private int _MAX_LIMIT;
    private boolean isThRunning, HOLD_FOR_MOMENT;

    private ThreadMananger()
    {
        _ThreadsV = new Vector<HTTPThread>();
        isThRunning = false;
        //HOLD_FOR_MOMENT = false;
        _MAX_LIMIT = 26;
    }

    public static ThreadMananger getInstance()
    {
        if(_this == null)
        {
            _this = new ThreadMananger();
        }
        return _this;
    }

    public static void destroyObj()
    {
        _this = null;
    }

    public boolean getThreadState()
    {
        return isThRunning;
    }

    public void setThreadState(boolean val)
    {
        isThRunning = val;
    }

    public void cancelThreadViaTag(String tag)
    {
        HOLD_FOR_MOMENT = true;
        Vector<String> indexStore = new Vector<String>();
        for(int  i = 0; i < _ThreadsV.size(); i++)
        {
            try
            {
                HTTPThread th = (HTTPThread)_ThreadsV.elementAt(i);
                if(tag.equalsIgnoreCase(th.get_tag()))
                {
                    if(th.getStatus())
                    {
                        if(th.isAlive())
                        {
                            try
                            {
                                Call httpCallRequest = th.getHttpCallRequest();
                                if( httpCallRequest!= null)
                                {
                                    httpCallRequest.cancel();
                                }
                            }
                            catch(Exception e)
                            {}
                            try
                            {
                                th.interrupt();
                            }
                            catch(Exception e)
                            {

                            }
                            indexStore.addElement(""+i);
                        }
                    }
                    break;
                }

            }
            catch(Exception e)
            {
            }
        }
        while(indexStore.size() > 0)
        {
            try
            {
                int index = 0;
                _ThreadsV.removeElementAt(Integer.parseInt(indexStore.elementAt(index)));
                indexStore.removeElementAt(index);
            }
            catch(Exception e)
            {

            }
        }

        HOLD_FOR_MOMENT = false;
    }

    public void cancelThreads(Context mContext)
    {
        HOLD_FOR_MOMENT = true;
        Vector<String> indexStore = new Vector<String>();
        for(int  i = 0; i < _ThreadsV.size(); i++)
        {
            try
            {
                HTTPThread th = (HTTPThread)_ThreadsV.elementAt(i);
                if(th.getStatus())
                {
                    if(th.isAlive() && mContext.equals(th.getTag()))
                    {
                    	try
                    	{
                    	    Call httpCallRequest = th.getHttpCallRequest();
                    		if( httpCallRequest!= null)
                    		{
                    			httpCallRequest.cancel();
                    		}
                    	}
                    	catch(Exception e)
                    	{}
                    	try
                    	{
                    		th.interrupt();
                    	}
                    	catch(Exception e)
                    	{

                    	}
                    	indexStore.addElement(""+i);
                    }
                }
            }
            catch(Exception e)
            {
            }
        }
        while(indexStore.size() > 0)
        {
        	try
        	{
        		int index = 0;
        		_ThreadsV.removeElementAt(Integer.parseInt(indexStore.elementAt(index)));
        		indexStore.removeElementAt(index);
        	}
        	catch(Exception e)
        	{

        	}
        }

        HOLD_FOR_MOMENT = false;
    }

    public void killAllThreads()
    {
        HOLD_FOR_MOMENT = true;
        for(int  i = 0; i < _ThreadsV.size(); i++)
        {
            try
            {
                HTTPThread th = (HTTPThread)_ThreadsV.elementAt(i);
                if(th.getStatus())
                {
                    if(th.isAlive())
                    {
                    	try
                    	{
                    		/*Call httpCallRequest = th.getHttpCallRequest();
                    		if( httpCallRequest!= null)
                    		{
                    			httpCallRequest.cancel();
                    		}*/
                    	}
                    	catch(Exception e)
                    	{}
                    	try
                    	{
                    		th.interrupt();
                    	}catch(Exception e){}

                    }
                }
            }
            catch(Exception e)
            {
            }
        }
        _ThreadsV.removeAllElements();
        HOLD_FOR_MOMENT = false;
    }

    public void run()
    {
        boolean isRunnng = true;
        int nCounter = 0;
        while(isRunnng)
        {
            //if(!HOLD_FOR_MOMENT)
            {
                for(int i = 0;i < _ThreadsV.size();i++)
                {
                    HTTPThread th = (HTTPThread)_ThreadsV.elementAt(i);
                    if(th.getStatus())
                    {
                        if(!th.isAlive())
                        {
                            _ThreadsV.removeElementAt(i);
                            _ThreadsV.setSize(_ThreadsV.size());
                            nCounter++;
                        }
                    }
                }

                if(nCounter == _MAX_LIMIT)
                {
                    int value = 0;
                    for(int j = 0; j <_ThreadsV.size(); j++)
                    {
                        value++;
                        try
                        {
                            HTTPThread _th = (HTTPThread)_ThreadsV.elementAt(j);
                            if(!_th.getStatus())
                            {
                                _th.start();
                                _th.setStatus(true);
                                _ThreadsV.setElementAt(_th, j);
                            }

                            Thread.sleep(2);
                        }
                        catch(Exception e1)
                        {
                            System.out.println(e1 + ": ThreadManager run restart");
                        }
                    }
                    nCounter = 0;
                }
            }
            try
            {
                if(_ThreadsV.size() == 0)
                {
                    nCounter = 0;
                    break;
                }
                Thread.sleep(25);
            }
            catch(Exception e)
            {
                System.out.println(e + ": ThreadManager run sleep");
            }
         }
         setThreadState(false);
    }

    public void add(HTTPThread element)
    {
        if(!HOLD_FOR_MOMENT)
        {
            _ThreadsV.addElement(element);
            if(_ThreadsV.size() <= this._MAX_LIMIT)
            {
                int index = _ThreadsV.size() - 1;
                HTTPThread _HTTPThread = (HTTPThread)_ThreadsV.elementAt(index);
                _HTTPThread.start();
                _HTTPThread.setStatus(true);
                //_ThreadsV.setElementAt(_HTTPThread, index);
            }
        }
    }
}
