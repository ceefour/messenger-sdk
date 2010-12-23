/**
 * Copyright (c) 2009-2010, Yahoo! Inc. All rights reserved.
 * Code licensed under the BSD License:
 * http://searchmarketing.yahoo.com/developer/docs/license.txt
 */

package com.yahoo.messenger.util;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.yahoo.messenger.exception.HttpException;


public class HttpUtils {

    public static final HttpClient hcli = new DefaultHttpClient();
	
    public static String performHttpGet(String cs)
        throws IOException, HttpException
    {
        return performHttpGet(cs, null);

    }

    public static String performHttpGet(String cs, YahooMessengerAuthentication authentication)
        throws IOException, HttpException
    {

        if (YahooMessengerConstants.debugHttpRequestResponse == 1) {
            System.out.println("HTTP GET SENT:");
            System.out.println(cs);
        }

        String s = new String();
        HttpGet httpGet = new HttpGet(cs);
        
        if (authentication != null) {

            if (!authentication.isUsingOAuth())
            	httpGet.setHeader("Cookie", authentication.getCookie());
            else
            	httpGet.setHeader("Authorization", "OAuth");

        }

        try {
        	HttpResponse response = hcli.execute(httpGet);
        	
            int rc = response.getStatusLine().getStatusCode();

            s = EntityUtils.toString(response.getEntity());

            if (rc != HttpStatus.SC_OK) {

                if (YahooMessengerConstants.debugHttpRequestResponse == 1) {
                    System.err.println("ERROR HTTP RETURN CODE IS: " + rc);
                    System.err.println("ERROR HTTP BODY IS: " + s);
                }

                throw new HttpException(HttpException.HTTP_OK_NOT_RECEIVED, rc);
            }

        } finally {
            //hcli.close();
        }

        if (YahooMessengerConstants.debugHttpRequestResponse == 1) {
            System.out.println("HTTP RESPONSE:");
            System.out.println(s);
        }
        
        return s;

    }

    public static String performHttpPost(String cs, YahooMessengerAuthentication authentication, String content)
        throws IOException, HttpException
    {

        String s = new String();

        HttpPost httpPost = new HttpPost(cs);

        if (authentication == null) {
            throw new HttpException(HttpException.NO_AUTHENTICATION_GIVEN);
        }

        if (!authentication.isUsingOAuth())
            httpPost.setHeader("Cookie", authentication.getCookie());
        else
        	httpPost.setHeader("Authorization", "OAuth");


        if (content != null) {

        	httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        	httpPost.setHeader("Content-Length", ""+content.length());

        	httpPost.setEntity(new StringEntity(content));
            
        }
        
        HttpResponse response = hcli.execute(httpPost);

        if (YahooMessengerConstants.debugHttpRequestResponse == 1) {
            System.out.println("HTTP POST SENT:");
            System.out.println(cs);
            System.out.println("HTTP BODY:");
            System.out.println(content);
        }

        try {

            int rc = response.getStatusLine().getStatusCode();

            s = EntityUtils.toString(response.getEntity());

            if (rc != HttpStatus.SC_OK) {

                if (YahooMessengerConstants.debugHttpRequestResponse == 1) {
                    System.err.println("ERROR HTTP RETURN CODE IS: " + rc);
                    System.err.println("ERROR HTTP BODY IS: " + s);
                }

                throw new HttpException(HttpException.HTTP_OK_NOT_RECEIVED, rc);
            }

        } finally {
//            hc.close();
        }

        if (YahooMessengerConstants.debugHttpRequestResponse == 1) {
            System.out.println("HTTP RESPONSE:");
            System.out.println(s);
        }

        return s;
    }


}
