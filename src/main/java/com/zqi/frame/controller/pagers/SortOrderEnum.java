package com.zqi.frame.controller.pagers;

import java.util.Iterator;

import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang.builder.HashCodeBuilder;

public final class SortOrderEnum {

    public static final SortOrderEnum DESCENDING;

    public static final SortOrderEnum ASCENDING;

    static final SortOrderEnum ALL[];

    private final int enumCode;

    private final String enumName;

    private SortOrderEnum( int code, String name ) {
        enumCode = code;
        enumName = name;
    }

    public int getCode() {
        return enumCode;
    }

    public String getName() {
        return enumName;
    }

    public static SortOrderEnum fromCode( int key ) {
        for ( int i = 0; i < ALL.length; i++ ) {
            if ( key == ALL[i].getCode() ) {
                return ALL[i];
            }
        }

        return null;
    }

    public static SortOrderEnum fromCode( Integer key ) {
        if ( key == null ) {
            return null;
        }
        else {
            return fromCode( key.intValue() );
        }
    }

    /*
     * public static SortOrderEnum fromIntegerCode(Integer key) { return
     * fromCode(key); }
     */

    public static SortOrderEnum fromName( String code ) {
        for ( int i = 0; i < ALL.length; i++ ) {
            if ( ALL[i].getName().equals( code ) ) {
                return ALL[i];
            }
        }

        return null;
    }

    public static Iterator iterator() {
        return new ArrayIterator( ALL );
    }

    public String toString() {
        return getName();
    }

    public String toSqlString() {
        return this.enumCode == 1 ? "desc" : "asc";
    }

    public boolean equals( Object o ) {
        return this == o;
    }

    public int hashCode() {
        return ( new HashCodeBuilder( 0x42fed581, 0xb31ea7f7 ) ).append( enumCode ).toHashCode();
    }

    static {
        DESCENDING = new SortOrderEnum( 1, "descending" );
        ASCENDING = new SortOrderEnum( 2, "ascending" );
        ALL = ( new SortOrderEnum[] { DESCENDING, ASCENDING } );
    }
}
