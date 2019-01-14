package com.searchplace.source.local_objects;


public class Place {

    private String _id, _name, _address, _distance;

    public void set_distance(String value)
    {
        _distance = value;
    }

    public String get_distance()
    {
        if(_distance == null)
        {
            return "";
        }
        return _distance;
    }

    public void set_address(String value)
    {
        _address = value;
    }

    public String get_address()
    {
        if(_address == null)
        {
            return "";
        }
        return _address;
    }


    public void set_name(String value)
    {
        _name = value;
    }

    public String get_name()
    {
        if(_name == null)
        {
            return "";
        }
        return _name;
    }
    public void set_id(String value)
    {
        _id = value;
    }

    public String get_id()
    {
        if(_id == null)
        {
            return "";
        }
        return _id;
    }
}
