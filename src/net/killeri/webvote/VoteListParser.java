/*
    Copyright (C) 2003-2005 Kalle Kivimaa (kalle.kivimaa@iki.fi)

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation; either version 2.1 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.killeri.webvote;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * @author Kalle Kivimaa
 */
public class VoteListParser
{
    public static final String INSERT_STATEMENT = "insert into person (hasvoted, votestyle, password, birthdate, lastname, firstname, address, zipcode, city, personnumber) values (false, 0, ?,?,?,?,?,?,?,?)";

    /**
     * 
     */
    public VoteListParser()
    {
        super();
    }

    protected String randomText()
    {
        StringBuffer sb = new StringBuffer();
        for( int i = 0; i < 10; i++ )
        {
            sb.append( Math.round( Math.random() * 10) );
            i++;
        }
        return sb.toString();
    }
    
    protected void storeList( String url, List entries ) throws SQLException, ClassNotFoundException
    {
        Class.forName( "org.postgresql.Driver" );
        Connection conn = DriverManager.getConnection( url );
        PreparedStatement statement = conn.prepareStatement( INSERT_STATEMENT );
        Iterator i = entries.iterator();
        while( i.hasNext() )
        {
            statement.setString( 1, randomText() );
            List entry = (List) i.next();
            Iterator j = entry.iterator();
            String o = null;
            int k = 2;
            while( j.hasNext() )
            {
                o = (String) j.next();
                statement.setString( k, o );
                k++;
            }
            //System.out.println( "Storing " + o);
            try
            {
                statement.execute();
            }
            catch( SQLException e )
            {
                System.out.println( "Possible duplicate key " + o );
            }
        }
    }
    
    protected List parseStream( InputStream is ) throws IOException
    {
        List parsedList = new ArrayList();
        BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
        for( String line = reader.readLine(); line != null; line = reader.readLine() )
        {
            parsedList.add( parseLine( line ) );
        }
        return parsedList;
    }

    protected List parseLine( String line )
    {
        List tokens = new ArrayList();
        StringTokenizer st = new StringTokenizer( line, ";", true );
        int i = 1;
        boolean rpt = false;
        while( st.hasMoreTokens() )
        {
            String entry = st.nextToken();
            //System.out.println( "Entry: " + entry + ", rpt: " + rpt );
            if( entry.equals( ";" ) )
            {
                if( rpt && i != 1 && i != 8 ) 
                {
                    tokens.add( "" );
                    i++;
                }
                rpt = true;
            }
            else
            {
                while( entry.startsWith( "\"" ) ) entry = entry.substring(1);
                while( entry.endsWith( "\"" ) ) entry = entry.substring( 0, entry.length() - 1 );
                if( i != 1 && i != 8 ) tokens.add( entry );
                i++;
                rpt = false;
            }
        }
        //System.out.println( "Length: " + tokens.size() + " "+ tokens );
        return tokens;
    }

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException
    {
        VoteListParser parser = new VoteListParser();
        List entries = parser.parseStream( System.in );
        parser.storeList( args[0], entries );
    }
}
