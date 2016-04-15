package com.zqi.frame.controller.pagers;

public class JQueryPager
    extends PagerImpl {

    private static final String SORT = "sidx";

    private static final String PAGE = "page";

    private static final String ASC = "asc";

    private static final String DESC = "desc";

    private static final String DIRECTION = "sord";

    private static final String PAGESIZE = "rows";

    private static final String SEARCHFOR = "searchString";

    private static final String SEARCHOPER = "searchOper";

    private static final String SEARCHFIELD = "searchField";

    private static final String FILTERS = "filters";

    private String groupOp;

    private String[] searchFields;

    private String[] searchOpers;

    private String[] searchFors;

    private String searchField;

    private String searchOper;

    private String searchFor;

    public String getRequestValueFilters() {
        return this.FILTERS;
    }

    public String getRequestValueSearchFor() {
        return this.SEARCHFOR;
    }

    public String getRequestValueSearchOper() {
        return this.SEARCHOPER;
    }

    public String getRequestValueSearchField() {
        return this.SEARCHFIELD;
    }

    public String getSearchField() {
        return this.searchField;
    }

    public void setSearchField( String searchField ) {
        this.searchField = searchField;

    }

    public String getSearchOper() {
        return searchOper;
    }

    public void setSearchOper( String searchOper ) {
        this.searchOper = searchOper;

    }

    public String getSearchFor() {
        return this.searchFor;
    }

    public void setSearchFor( String searchFor ) {
        this.searchFor = searchFor;

    }

    public String getGroupOp() {

        return this.groupOp;
    }

    public void setGroupOp( String groupOp ) {
        this.groupOp = groupOp;

    }

    public String[] getSearchFields() {
        return searchFields;
    }

    public void setSearchFields( String[] searchFields ) {
        this.searchFields = searchFields;
    }

    public String[] getSearchOpers() {
        return searchOpers;
    }

    public void setSearchOpers( String[] searchOpers ) {
        this.searchOpers = searchOpers;
    }

    public String[] getSearchFors() {
        return searchFors;
    }

    public void setSearchFors( String[] searchFors ) {
        this.searchFors = searchFors;
    }

    public String getRequestValueSort() {
        return this.SORT;
    }

    public String getRequestValuePage() {
        return this.PAGE;
    }

    public String getRequestValueAsc() {
        return this.ASC;
    }

    public String getRequestValueDesc() {
        return this.DESC;
    }

    public String getRequestValueDirection() {
        return this.DIRECTION;
    }

    public String getRequestValuePagesize() {
        return this.PAGESIZE;
    }

    public boolean isSearchable() {
        return true;
    }

}
