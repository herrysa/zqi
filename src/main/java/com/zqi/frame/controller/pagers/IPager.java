package com.zqi.frame.controller.pagers;

import java.util.List;

public interface IPager {

    /** Set the default page size **/
    int DEFAULT_PAGE_SIZE = 25;

    /** set results list */
    void setList( List resultList );

    List getList();

    /**  Set the Total - total number of records or rows (e.g 10,000 rows found with the query) **/
    void setTotalNumberOfRows( int total );

    int getTotalNumberOfRows();

    int getTotalNumberOfPages();

    /**  set the Page Size - to display the required number of rows (e.g 25 rows out of 10,000)  **/
    void setPageSize( int pageSize );

    /**  get page size **/
    int getPageSize();

    /** Set the Index - start with 0 and keep increamenting  **/
    void setIndex( int index );

    /** get the first record index **/
    int getFirstRecordIndex();

    /**  Set the sort Direction  -  asc or dsc **/
    void setSortDirection( SortOrderEnum sortOrderEnum );

    SortOrderEnum getSortDirection();

    /** set sort criterion **/
    void setSortCriterion( String sortCriterion );

    String getSortCriterion();

    int getPageNumber();

    //int getObjectsPerPage();
    String getSearchId();

    int getStart();

    int getEnd();

}