package com.zqi.frame.controller.pagers;

import java.util.List;
import java.util.Map;

abstract class PagerImpl
    implements IPager, ISearcher, ISearcherParameters, IPagerParameters, ISummer {

    /** current page index, starts at 0 */
    private int index;

    /** number of results per page (number of rows per page to be displayed ) */
    private int pageSize;

    /** list of results (rows found ) in the current page */
    private List list;

    private Map sumData;

    /** default sorting order */
    private SortOrderEnum sortDirection = SortOrderEnum.ASCENDING;

    /** sort criteria (sorting property name) */
    private String sortCriterion;

    private int totalNumberOfRows;

    private int firstRecordIndex;

    public void setList( List resultList ) {
        this.list = resultList;

    }

    public List getList() {
        return list;
    }

    public void setTotalNumberOfRows( int total ) {
        this.totalNumberOfRows = total;
    }

    public int getTotalNumberOfRows() {
        return this.totalNumberOfRows;
    }

    public int getTotalNumberOfPages() {
        return (int) Math.ceil( (double) totalNumberOfRows / (double) pageSize );
    }

    public void setPageSize( int pageSize ) {
        this.pageSize = pageSize;

    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setIndex( int index ) {
        this.index = index;

    }

    public int getIndex() {
        return this.index;

    }

    public int getFirstRecordIndex() {
        return this.firstRecordIndex;
    }

    public void setSortDirection( SortOrderEnum sortOrderEnum ) {
        this.sortDirection = sortOrderEnum;

    }

    public SortOrderEnum getSortDirection() {
        return this.sortDirection;
    }

    public void setSortCriterion( String sortCriterion ) {
        this.sortCriterion = sortCriterion;

    }

    public String getSortCriterion() {
        return this.sortCriterion;
    }

    public int getPageNumber() {
        // pageNumber is a corrected zero based index.
        return this.index + 1;

    }

    public String getSearchId() {
        // Not implemented for now.
        // This is required, if we want the ID to be included in the paginated
        // purpose.
        return null;
    }

    public int getEnd() {
        return getPageSize() * getPageNumber();
    }

    public int getStart() {
        return getEnd() - getPageSize();
    }

    public Map getSumData() {

        return this.sumData;
    }

    public void setInitSumData( Map map ) {
        this.sumData = map;
    }

}
