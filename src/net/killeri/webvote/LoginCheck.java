/*
 * Copyright (C) 2005 Kalle Kivimaa.  All rights reserved.
 *
 * $Header$
 *
 * $Id$
 *
 */
package net.killeri.webvote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URL;
import java.rmi.RemoteException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Provides a simple example for exterior authentication. Feel free
 * to either modify or extend this. This just checks that the password
 * is at least two characters long.
 * 
 * @author killer
 */
public class LoginCheck {
    public static boolean check(Person person, String url, String uid, String passwd) throws RemoteException {
        if( url == null ) return check(person);
        return check(url + uid + "&password=" + passwd);
    }
    public static boolean check(Person person) throws RemoteException {
        if( person == null ) return false;
        if( person.getPassword() == null || person.getPassword().length() < 2 ) return false;
        return true;
    }
    
    public static boolean check(String url) {
        try {
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    System.out.println("Warning: URL Host: "+urlHostName+" vs. "+session.getPeerHost());
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
            
            URLConnection c = ( new URL( url ) ).openConnection();
            c.connect();
            Object o = c.getContent();
            BufferedReader r = new BufferedReader( new InputStreamReader( (InputStream) o ) );
            return "1".equals( r.readLine() ) ? true : false;
        } catch( IOException e ) {
            e.printStackTrace();
            throw new NullPointerException( "Cannot connect to authentication: " + e );
        }
    }
}

