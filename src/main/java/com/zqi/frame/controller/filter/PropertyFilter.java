package com.zqi.frame.controller.filter;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.zqi.frame.util.ConvertUtil;
import com.zqi.frame.util.RequestUtil;

/**
 * 与具体ORM实现无关的属性过滤条件封装类, 主要记录页面中简单的搜索过滤条件.
 * 
 * @author jackie
 */
public class PropertyFilter {
    private static final Log log = LogFactory.getLog( PropertyFilter.class );

    /** 多个属性间OR关系的分隔符. */
    public static final String OR_SEPARATOR = "_OR_";

    public static final String COMMA_SEPARATOR = ",";

    /** 属性比较类型. */
    public enum MatchType {
        EQ( "" ), NE( ".NOT_EQUAL" ), ISNULL( ".IS" ), ISNOTNULL( ".NOT_NULL" ), LIKE( ".LIKE" ), LT( ".LT" ), GT( ".GT" ), LE( ".LT_EQ" ), GE(
            ".GT_EQ" ), IN( ".IN" ) , NI( ".NOT_IN" ),GP(".GROUP"),OA(".ORDER_ASC"),OD(".ORDER_DESC"),SQ(".SQL");;

        private MatchType( String name ) {
            this.name = name;
        }

        private String name;

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }

    }

    /** 属性数据类型. */
    public enum PropertyType {
        S( String.class ), I( Integer.class ), L( Long.class ), N( Double.class ), D( Date.class ), T( Timestamp.class ), B( Boolean.class ), G(
            BigDecimal.class );

        private Class<?> clazz;

        private PropertyType( Class<?> clazz ) {
            this.clazz = clazz;
        }

        public Class<?> getValue() {
            return clazz;
        }
    }

    private MatchType matchType = null;

    private Object matchValue = null;

    private Class<?> propertyClass = null;

    private String[] propertyNames = null;

    public PropertyFilter() {
    }

    /**
     * @param filterName 比较属性字符串,含待比较的比较类型、属性值类型及属性列表. 
     *                   eg. LIKES_NAME_OR_LOGIN_NAME
     * @param value 待比较的值.
     */
    public PropertyFilter( final String filterName, final String value ) {

        String firstPart = StringUtils.substringBefore( filterName, "_" );
        String matchTypeCode = StringUtils.substring( firstPart, 0, firstPart.length() - 1 );
        String propertyTypeCode = StringUtils.substring( firstPart, firstPart.length() - 1, firstPart.length() );

        try {
            matchType = Enum.valueOf( MatchType.class, matchTypeCode );
        }
        catch ( RuntimeException e ) {
            //Log log = LogUtil.getLog(PropertyFilter.class);
            log.error( "filter名称" + filterName + ":" + matchTypeCode + "没有按规则编写,无法得到属性比较类型.", e );
            throw new IllegalArgumentException( "filter名称" + filterName + ":" + matchTypeCode + "没有按规则编写,无法得到属性比较类型.", e );
        }

        try {
            propertyClass = Enum.valueOf( PropertyType.class, propertyTypeCode ).getValue();
        }
        catch ( RuntimeException e ) {
            //Log log = LogUtil.getLog(PropertyFilter.class);
            log.error( "filter名称" + filterName + ":" + propertyTypeCode + "没有按规则编写,无法得到属性值类型.", e );
            throw new IllegalArgumentException( "filter名称" + filterName + ":" + propertyTypeCode + "没有按规则编写,无法得到属性值类型.", e );
        }

        String propertyNameStr = StringUtils.substringAfter( filterName, "_" );
        Assert.isTrue( StringUtils.isNotBlank( propertyNameStr ), "filter名称" + filterName + "没有按规则编写,无法得到属性名称." );
        propertyNames = StringUtils.splitByWholeSeparator( propertyNameStr, PropertyFilter.OR_SEPARATOR );
        if ( matchType == MatchType.IN || matchType == MatchType.NI) {
            String[] valueArr = value.split( COMMA_SEPARATOR );
            this.matchValue = ConvertUtil.convertStringArrToObject( valueArr, propertyClass );
        }
        else {
            this.matchValue = ConvertUtil.convertStringToObject( value, propertyClass );
        }
    }

    public PropertyFilter( final String filterName, final Date value ) {

        String firstPart = StringUtils.substringBefore( filterName, "_" );
        String matchTypeCode = StringUtils.substring( firstPart, 0, firstPart.length() - 1 );
        String propertyTypeCode = StringUtils.substring( firstPart, firstPart.length() - 1, firstPart.length() );

        try {
            matchType = Enum.valueOf( MatchType.class, matchTypeCode );
        }
        catch ( RuntimeException e ) {
            //Log log = LogUtil.getLog(PropertyFilter.class);
            log.error( "filter名称" + filterName + "没有按规则编写,无法得到属性比较类型.", e );
            throw new IllegalArgumentException( "filter名称" + filterName + "没有按规则编写,无法得到属性比较类型.", e );
        }

        try {
            propertyClass = Enum.valueOf( PropertyType.class, propertyTypeCode ).getValue();
        }
        catch ( RuntimeException e ) {
            //Log log = LogUtil.getLog(PropertyFilter.class);
            log.error( "filter名称" + filterName + "没有按规则编写,无法得到属性值类型.", e );
            throw new IllegalArgumentException( "filter名称" + filterName + "没有按规则编写,无法得到属性值类型.", e );
        }

        String propertyNameStr = StringUtils.substringAfter( filterName, "_" );
        Assert.isTrue( StringUtils.isNotBlank( propertyNameStr ), "filter名称" + filterName + "没有按规则编写,无法得到属性名称." );
        propertyNames = StringUtils.splitByWholeSeparator( propertyNameStr, PropertyFilter.OR_SEPARATOR );

        this.matchValue = value;
    }

    /**
     * 从HttpRequest中创建PropertyFilter列表, 默认Filter属性名前缀为filter.
     * 
     * @see #buildFromHttpRequest(HttpServletRequest, String)
     */
    public static List<PropertyFilter> buildFromHttpRequest( final HttpServletRequest request ) {
        return buildFromHttpRequest( request, "filter" );
    }

    /**
     * 从HttpRequest中创建PropertyFilter列表
     * PropertyFilter命名规则为Filter属性前缀_比较类型属性类型_属性名.
     * 
     * eg.
     * filter_EQS_name
     * filter_LIKES_name_OR_email
     */
    public static List<PropertyFilter> buildFromHttpRequest( final HttpServletRequest request, final String filterPrefix ) {
        List<PropertyFilter> filterList = new ArrayList<PropertyFilter>();

        //从request中获取含属性前缀名的参数,构造去除前缀名后的参数Map.
        Map<String, Object> filterParamMap = RequestUtil.getParametersStartingWith( request, filterPrefix + "_" );

        //分析参数Map,构造PropertyFilter列表
        for ( Map.Entry<String, Object> entry : filterParamMap.entrySet() ) {
            String filterName = entry.getKey();
            String value = (String) entry.getValue();
            //如果value值为空,则忽略此filter.
            if ( StringUtils.isNotBlank( value ) ) {
            	// 传递的参数包含&时，在前台做处理，在这里解析  ----Gaozhengyang
            	if(value.indexOf("%26")>0){
            		value = value.replaceAll("%26", "&");
            	}
                PropertyFilter filter = new PropertyFilter( filterName, value );
                filterList.add( filter );
            }
        }

        return filterList;
    }

    /**
     * 获取比较值的类型.
     */
    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    /**
     * 获取比较方式.
     */
    public MatchType getMatchType() {
        return matchType;
    }

    /**
     * 获取比较值.
     */
    public Object getMatchValue() {
        return matchValue;
    }

    /**
     * 获取比较属性名称列表.
     */
    public String[] getPropertyNames() {
        return propertyNames;
    }

    /**
     * 获取唯一的比较属性名称.
     */
    public String getPropertyName() {
        Assert.isTrue( propertyNames.length == 1, "There are not only one property in this filter." );
        return propertyNames[0];
    }

    /**
     * 是否比较多个属性.
     */
    public boolean hasMultiProperties() {
        return ( propertyNames.length > 1 );
    }
}
