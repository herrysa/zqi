package com.zqi.unit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Date Utility Class used to convert Strings to Dates and Timestamps
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *         Modified by <a href="mailto:dan@getrolling.com">Dan Kibler </a>
 *         to correct time pattern. Minutes should be mm not MM (MM is month).
 */
public final class DateUtil {
    private static Log log = LogFactory.getLog( DateUtil.class );

    public static final String TIME_PATTERN = "HH:mm";

    public static final String YEAR_PATTERN = "yyyy";

    public static final String MM_PATTERN = "MM";

    public static final String dd_PATTERN = "dd";

    public static final String ALL_PATTERN = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    
    public static final String TIMESTAMP_PATTERN = "yyyyMMddHHmmss";

    /**
     * Checkstyle rule: utility classes should not have public constructor
     */
    private DateUtil() {
    }

    /**
     * Return default datePattern (MM/dd/yyyy)
     *
     * @return a string representing the date pattern on the UI
     */
    public static String getDatePattern() {
        Locale locale = LocaleContextHolder.getLocale();
        String defaultDatePattern;
        try {
            defaultDatePattern = "yyyy-MM-dd";
        }
        catch ( MissingResourceException mse ) {
            defaultDatePattern = "MM/dd/yyyy";
        }

        return defaultDatePattern;
    }

    public static String getDateTimePattern() {
        return DateUtil.getDatePattern() + " HH:mm:ss";
    }

    /**
     * This method attempts to convert an Oracle-formatted date
     * in the form dd-MMM-yyyy to mm/dd/yyyy.
     *
     * @param aDate date from database as a string
     * @return formatted string for the ui
     */
    public static String getDate( Date aDate ) {
        SimpleDateFormat df;
        String returnValue = "";

        if ( aDate != null ) {
            df = new SimpleDateFormat( getDatePattern() );
            returnValue = df.format( aDate );
        }

        return ( returnValue );
    }

    /**
     * This method generates a string representation of a date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param strDate a string representation of a date
     * @return a converted Date object
     * @throws ParseException when String doesn't match the expected format
     * @see java.text.SimpleDateFormat
     */
    public static Date convertStringToDate( String aMask, String strDate )
        throws ParseException {
        SimpleDateFormat df;
        Date date;
        df = new SimpleDateFormat( aMask );

        if ( log.isDebugEnabled() ) {
            log.debug( "converting '" + strDate + "' to date with mask '" + aMask + "'" );
        }

        try {
            date = df.parse( strDate );
        }
        catch ( ParseException pe ) {
            //log.error("ParseException: " + pe);
            throw new ParseException( pe.getMessage(), pe.getErrorOffset() );
        }

        return ( date );
    }

    /**
     * This method returns the current date time in the format:
     * MM/dd/yyyy HH:MM a
     *
     * @param theTime the current time
     * @return the current date/time
     */
    public static String getTimeNow( Date theTime ) {
        return getDateTime( TIME_PATTERN, theTime );
    }

    public static String getYNow( Date theTime ) {
        return getDateTime( YEAR_PATTERN, theTime );
    }

    public static String getMNow( Date theTime ) {
        return getDateTime( MM_PATTERN, theTime );
    }

    public static String getDNow( Date theTime ) {
        return getDateTime( dd_PATTERN, theTime );
    }

    public static String getALLNow( Date theTime ) {
        return getDateTime( ALL_PATTERN, theTime );
    }
    
    public static String getDateNow(Date theTime){
    	return getDateTime( DATE_PATTERN, theTime );
    }

    public static String getDateTimeNow(){
    	Date today = new Date();
    	SimpleDateFormat df = new SimpleDateFormat( getDateTimePattern() );
    	String todayAsString = df.format( today );
    	return todayAsString;
    }
    public static String getDateNow(){
    	Date today = new Date();
    	SimpleDateFormat df = new SimpleDateFormat( getDatePattern() );
    	String todayAsString = df.format( today );
    	return todayAsString;
    }
    
    /**
     * This method returns the current date in the format: MM/dd/yyyy
     *
     * @return the current date
     * @throws ParseException when String doesn't match the expected format
     */
    public static Calendar getToday()
        throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat( getDatePattern() );

        // This seems like quite a hack (date -> string -> date),
        // but it works ;-)
        String todayAsString = df.format( today );
        Calendar cal = new GregorianCalendar();
        cal.setTime( convertStringToDate( todayAsString ) );

        return cal;
    }

    /**
     * This method generates a string representation of a date's date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     * @see java.text.SimpleDateFormat
     */
    public static String getDateTime( String aMask, Date aDate ) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if ( aDate == null ) {
            log.warn( "aDate is null!" );
        }
        else {
            df = new SimpleDateFormat( aMask );
            returnValue = df.format( aDate );
        }

        return ( returnValue );
    }

    /**
     * This method generates a string representation of a date based
     * on the System Property 'dateFormat'
     * in the format you specify on input
     *
     * @param aDate A date to convert
     * @return a string representation of the date
     */
    public static String convertDateToString( Date aDate ) {
        return getDateTime( getDatePattern(), aDate );
    }
    /**
     * 获取系统时间的SnapCode
     * @return
     */
    public static String getSnapCode(){
    	return convertDateToString(TIMESTAMP_PATTERN, new Date());
    }
    /**
     * 获取凌晨时间的SnapCode
     * @return
     */
    public static String getWeeHoursSnapCode(){
    	Date date=new Date();
    	GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		if ((gc.get(gc.HOUR_OF_DAY) == 0) && (gc.get(gc.MINUTE) == 0)
				&& (gc.get(gc.SECOND) == 0)) {
		} else {
			date = new Date(date.getTime() - gc.get(gc.HOUR_OF_DAY) * 60 * 60
					* 1000 - gc.get(gc.MINUTE) * 60 * 1000 - gc.get(gc.SECOND)
					* 1000);
		}
		return convertDateToString(TIMESTAMP_PATTERN, date);
    }
    public static String convertDateToString( String format,Date aDate ) {
        return getDateTime( format, aDate );
    }

    /**
     * This method converts a String to a date using the datePattern
     *
     * @param strDate the date to convert (in format MM/dd/yyyy)
     * @return a date object
     * @throws ParseException when String doesn't match the expected format
     */
    public static Date convertStringToDate( final String strDate )
        throws ParseException {
        return convertStringToDate( getDatePattern(), strDate );
    }
   /**
    * 判断闰年
    * @param year 年
    * @return
    */
    public static Boolean isLeapYear(int year) {
		if(year % 100 == 0){
			if(year % 400 == 0){
				return true;
			}
		}else{
			if(year % 4 == 0){
				return true;
			}
		}
		return false;
	}
    /**
     * 判断每个月天数
     * @param year 年
     * @param month 月
     * @return
     */
    public static Integer dayNumOfMonth(int year,int month){
    	int dayNum = 0;
    	switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			dayNum = 31;
			break;
		case 2:
			if(isLeapYear(year)){
				dayNum = 29;
			}else{
				dayNum = 28;
			}
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			dayNum = 30;
			break;
		default:
			break;
		}
    	return dayNum;
    }
    /**
     * 判断期间第一天是星期几
     * @param period
     * @return 1=SUNDAY；2=MONDAY；3=TUESDAY；4=WEDNESDAY；5=THURSDAY；6=FRIDAY；7=SATURDAY
     */
    public static Integer peiodFirstDayWeek(String period){
		String year = period.substring(0, 4);
		String month = period.substring(4);
		Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, Integer.parseInt(month)-1);
        cal.set(Calendar.DATE, 1);
        int week = cal.get(Calendar.DAY_OF_WEEK);
        return week;
    }
    /**
     * 获取当年第一个期间
     * @param period
     * @return
     */
    public static String getFirstPeriod(String period){
    	String year = period.substring(0, 4);
    	return year + "01";
    }
}
