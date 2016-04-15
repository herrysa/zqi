package com.zqi.frame.controller.pagers;

public interface ISearcher {

    String getSearchField();

    void setSearchField( String searchField );

    String getSearchOper();

    void setSearchOper( String searchOper );

    String getSearchFor();

    void setSearchFor( String searchFor );

    String getGroupOp();

    void setGroupOp( String groupOp );

    String[] getSearchFields();

    void setSearchFields( String[] searchFields );

    String[] getSearchOpers();

    void setSearchOpers( String[] searchOper );

    String[] getSearchFors();

    void setSearchFors( String[] searchFor );

    boolean isSearchable();

}
