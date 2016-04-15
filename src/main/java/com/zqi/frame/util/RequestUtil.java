package com.zqi.frame.util;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * Convenience class for setting and retrieving cookies.
 */
public final class RequestUtil {
    private static final Log log = LogFactory.getLog( RequestUtil.class );

    /**
     * Checkstyle rule: utility classes should not have public constructor
     */
    private RequestUtil() {
    }

    /**
     * Convenience method to set a cookie
     *
     * @param response the current response
     * @param name the name of the cookie
     * @param value the value of the cookie
     * @param path the path to set it on
     */
    public static void setCookie( HttpServletResponse response, String name, String value, String path ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Setting cookie '" + name + "' on path '" + path + "'" );
        }

        Cookie cookie = new Cookie( name, value );
        cookie.setSecure( false );
        cookie.setPath( path );
        cookie.setMaxAge( 3600 * 24 * 30 ); // 30 days

        response.addCookie( cookie );
    }

    /**
     * Convenience method to get a cookie by name
     *
     * @param request the current request
     * @param name the name of the cookie to find
     *
     * @return the cookie (if found), null if not found
     */
    public static Cookie getCookie( HttpServletRequest request, String name ) {
        Cookie[] cookies = request.getCookies();
        Cookie returnCookie = null;

        if ( cookies == null ) {
            return returnCookie;
        }

        for ( final Cookie thisCookie : cookies ) {
            if ( thisCookie.getName().equals( name ) && !"".equals( thisCookie.getValue() ) ) {
                returnCookie = thisCookie;
                break;
            }
        }

        return returnCookie;
    }

    /**
     * Convenience method for deleting a cookie by name
     *
     * @param response the current web response
     * @param cookie the cookie to delete
     * @param path the path on which the cookie was set (i.e. /appfuse)
     */
    public static void deleteCookie( HttpServletResponse response, Cookie cookie, String path ) {
        if ( cookie != null ) {
            // Delete the cookie by setting its maximum age to zero
            cookie.setMaxAge( 0 );
            cookie.setPath( path );
            response.addCookie( cookie );
        }
    }

    /**
     * Convenience method to get the application's URL based on request
     * variables.
     * 
     * @param request the current request
     * @return URL to application
     */
    public static String getAppURL( HttpServletRequest request ) {
        if ( request == null )
            return "";

        StringBuffer url = new StringBuffer();
        int port = request.getServerPort();
        if ( port < 0 ) {
            port = 80; // Work around java.net.URL bug
        }
        String scheme = request.getScheme();
        url.append( scheme );
        url.append( "://" );
        url.append( request.getServerName() );
        if ( ( scheme.equals( "http" ) && ( port != 80 ) ) || ( scheme.equals( "https" ) && ( port != 443 ) ) ) {
            url.append( ':' );
            url.append( port );
        }
        url.append( request.getContextPath() );
        return url.toString();
    }

    /**
     * 取得带相同前缀的Request Parameters.
     * 
     * 返回的结果的Parameter名已去除前缀.
     * @throws UnsupportedEncodingException 
     */
    @SuppressWarnings( "unchecked" )
    public static Map<String, Object> getParametersStartingWith( ServletRequest request, String prefix ) {
        Assert.notNull( request, "Request must not be null" );
        Enumeration paramNames = request.getParameterNames();
        Map<String, Object> params = new TreeMap<String, Object>();
        if ( prefix == null ) {
            prefix = "";
        }
        while ( paramNames != null && paramNames.hasMoreElements() ) {
            String paramName = (String) paramNames.nextElement();
            if ( "".equals( prefix ) || paramName.startsWith( prefix ) ) {
                String unprefixed = paramName.substring( prefix.length() );
                String[] values = request.getParameterValues( paramName );
                if ( values == null || values.length == 0 ) {
                    // Do nothing, no values found at all.
                }
                else if ( values.length > 1 ) {
                    params.put( unprefixed, values );
                }
                else {
                    params.put( unprefixed, values[0] );
                }
            }
        }
        return params;
    }
}
