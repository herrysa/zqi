package com.zqi.unit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UUIDGenerator {
    private static UUIDGenerator generator = null;

    private static String lValue = null;

    private static final SecureRandom random = new SecureRandom();

    public static UUIDGenerator getInstance() {
        if ( generator == null )
            generator = new UUIDGenerator();
        return generator;
    }

    public final String getNextValue( String paramString ) {
        StringBuffer sb = new StringBuffer( 16 );
        if ( lValue == null ) {
            InetAddress localObject = null;
            try {
                localObject = InetAddress.getLocalHost();
            }
            catch ( UnknownHostException localUnknownHostException ) {
                localUnknownHostException.printStackTrace();
                return null;
            }
            byte[] arrayOfByte = ( (InetAddress) localObject ).getAddress();
            lValue = getInitString( getInterAddressInt( arrayOfByte ), 8 );
        }
        Object localObject = getInitString( System.identityHashCode( paramString ), 8 );
        sb.append( lValue );
        sb.append( (String) localObject );
        long l = System.currentTimeMillis();
        int i = (int) l & 0xFFFFFFFF;
        int j = random.nextInt();
        StringBuffer localStringBuffer2 = new StringBuffer( 32 );
        localStringBuffer2.append( getInitString( i, 8 ) );
        localStringBuffer2.append( sb.toString() );
        localStringBuffer2.append( getInitString( j, 8 ) );
        return (String) localStringBuffer2.toString();
    }

    public static Connection getConnection() {
        try {
            Class.forName( "net.sourceforge.jtds.jdbc.Driver" );
        }
        catch ( ClassNotFoundException e ) {
            System.out.println( e );
        }
        try {
            Connection conn = DriverManager.getConnection( "jdbc:jtds:sqlserver://localhost:1433/iHOS", "sa", "sa" );
            return conn;
        }
        catch ( SQLException e ) {
            System.out.println( e );
        }
        return null;
    }

    public final String getNextValue() {
        StringBuffer localStringBuffer1 = new StringBuffer( 16 );
        if ( lValue == null ) {
            InetAddress localObject = null;
            try {
                localObject = InetAddress.getLocalHost();
            }
            catch ( UnknownHostException localUnknownHostException ) {
                localUnknownHostException.printStackTrace();
                return null;
            }
            byte[] arrayOfByte = ( (InetAddress) localObject ).getAddress();
            lValue = getInitString( getInterAddressInt( arrayOfByte ), 8 );
        }
        Object localObject = getInitString( System.identityHashCode( this ), 8 );
        localStringBuffer1.append( lValue );
        localStringBuffer1.append( (String) localObject );
        long l = System.currentTimeMillis();
        int i = (int) l & 0xFFFFFFFF;
        int j = random.nextInt();
        StringBuffer localStringBuffer2 = new StringBuffer( 32 );
        localStringBuffer2.append( getInitString( i, 8 ) );
        localStringBuffer2.append( localStringBuffer1.toString() );
        localStringBuffer2.append( getInitString( j, 8 ) );
        return (String) localStringBuffer2.toString();
    }

    public static int getInterAddressInt( byte[] paramArrayOfByte ) {
        int i = 0;
        int j = 24;
        for ( int k = 0; j >= 0; k++ ) {
            int m = paramArrayOfByte[k] & 0xFF;
            i += ( m << j );
            j -= 8;
        }
        return i;
    }

    public static String getInitString( int paramInt1, int paramInt2 ) {
        String str = Integer.toHexString( paramInt1 );
        return appendZero( str, paramInt2 ) + str;
    }

    public static String appendZero( String paramString, int paramInt ) {
        StringBuffer localStringBuffer = new StringBuffer();
        if ( paramString.length() < paramInt )
            for ( int i = 0; i < paramInt - paramString.length(); i++ )
                localStringBuffer.append( '0' );
        return localStringBuffer.toString();
    }

    public final String getNextValue16() {
        long l = System.currentTimeMillis();
        int i = (int) l & 0xFFFFFFFF;
        int j = random.nextInt();
        StringBuffer sb = new StringBuffer( 16 );
        sb.append( getInitString( i, 8 ) );
        sb.append( getInitString( j, 8 ) );
        return sb.toString();
    }

    public static void main( String[] args ) {
        System.out.print( 122 );
        UUIDGenerator gr = UUIDGenerator.getInstance();
        System.out.println( gr.getNextValue() );
        System.out.println( gr.getNextValue( "fdc" ) );

        try {
            InetAddress localObject;
            localObject = InetAddress.getLocalHost();
            byte[] arrayOfByte = ( (InetAddress) localObject ).getAddress();
            System.out.println( arrayOfByte );
            System.out.println( gr.getInterAddressInt( arrayOfByte ) );
            System.out.println( getInitString( getInterAddressInt( arrayOfByte ), 12 ) );

        }
        catch ( UnknownHostException e ) {
            e.printStackTrace();
        }

        System.out.println( gr.appendZero( "fdc", 10 ) );
        System.out.println( gr.getNextValue16() );

    }
}

/* Location:           D:\Java_Working\EclipseWorkSpaces\OldSystemWorkSpace\ecis_lib\foundation2.0.0\
 * Qualified Name:     com.huge.common.generator.UUIDGenerator
 * JD-Core Version:    0.6.0
 */