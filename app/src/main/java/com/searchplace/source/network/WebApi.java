package com.searchplace.source.network;

import com.searchplace.source.Utilties;

public class WebApi {

    private static String BASE_URL = "https://api.foursquare.com/v2/",
                          CLIENT_ID = "P0NN15YXVHTQPEG051KOZBUAZLBOOVF0DXPRLENGW5PXJGHZ",
                          CLIENT_SECRET = "IYPUSPLUN5UUZSCJWZ11UBZRWRA322EPSCZVHBRKVS2Z5DSA";


    public static String URL_SEARCH_VENU = BASE_URL+"venues/search?client_id=" +CLIENT_ID
                                           +"&client_secret="+CLIENT_SECRET+"&v="+ Utilties.getVersionCode()+"&ll=";




}
