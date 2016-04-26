package com.zqi.frame.controller.pagers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Repository;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

@Repository("pagerFactory")
public class PagerFactory {

    public static final String JQUERYTYPE = "JQUERY";

    public static final String DISPLAYTAGTYPE = "DISPLAYTAG";
    
    public static final String BSTYPE = "BS";
    
    

    public PagerImpl getPager( String type, HttpServletRequest request ) {
        if ( type.equals( JQUERYTYPE ) ) {
            JQueryPager jqp = new JQueryPager();
            makePager( jqp, request );
            return jqp;
        }
        if ( type.equals( BSTYPE ) ) {
            BSPager bs = new BSPager();
            makePager( bs, request );
            return bs;
        }
        /*if ( type.equals( DISPLAYTAGTYPE ) ) {
            DisplayTagPager dtp = new DisplayTagPager();
            makePager( dtp, request );
            return dtp;
        }*/
        return null;

    }

    private void makePager( PagerImpl pager, HttpServletRequest request ) {
        String sortCriterion = null;
        String thePage = null;
        int pageSize = 0;
        if ( request != null ) {
            sortCriterion = request.getParameter( pager.getRequestValueSort() );
            pager.setSortDirection( pager.getRequestValueDesc().equals( request.getParameter( pager.getRequestValueDirection() ) ) ? SortOrderEnum.DESCENDING
                            : SortOrderEnum.ASCENDING );
            thePage = request.getParameter( pager.getRequestValuePage() );
            String ps = request.getParameter( pager.getRequestValuePagesize() );
            if ( ps != null && ps.trim().length() > 0 ) {
                pageSize = new Integer( ps ).intValue();
            }
            else
                pageSize = IPager.DEFAULT_PAGE_SIZE;
            if ( sortCriterion == null || sortCriterion.trim().length() == 0 )
                sortCriterion = null;

            if ( pager.isSearchable() ) {
                String tmp = request.getParameter( pager.getRequestValueSearchField() );
                pager.setSearchField( tmp );
                tmp = request.getParameter( pager.getRequestValueSearchFor() );
                pager.setSearchFor( tmp );
                tmp = request.getParameter( pager.getRequestValueSearchOper() );
                pager.setSearchOper( tmp );

                String filters = request.getParameter( pager.getRequestValueFilters() );
                //	            System.out.println(filters);
                //	            filters.replaceAll("\\", "");
                //	            System.out.println(filters);
                if ( filters != null && !filters.equalsIgnoreCase( "" ) ) {
                    JSONObject jsonFilter = (JSONObject) JSONSerializer.toJSON( filters );
                    String groupOp = jsonFilter.getString( "groupOp" );

                    Object rules_string = jsonFilter.get( "rules" );
                    //	            JSONObject jobj = jsonFilter.getJSONObject("rules");
                    //	            JSONArray rules1 = jsonFilter.optJSONArray("rules");
                    // JSONArray rules = jsonFilter.getJSONArray("rules");
                    JSONArray rules = (JSONArray) JSONSerializer.toJSON( rules_string );

                    int rulesCount = JSONArray.getDimensions( rules )[0];
                    String[] fields = new String[rulesCount];
                    String[] ops = new String[rulesCount];
                    String[] datas = new String[rulesCount];
                    for ( int i = 0; i < rulesCount; i++ ) {
                        JSONObject rule = rules.getJSONObject( i );
                        fields[i] = rule.getString( "field" );
                        ops[i] = rule.getString( "op" );
                        datas[i] = rule.getString( "data" );
                    }
                    pager.setGroupOp( groupOp );
                    pager.setSearchFields( fields );
                    pager.setSearchOpers( ops );
                    pager.setSearchFors( datas );
                }

            }
        }
        pager.setSortCriterion( sortCriterion );
        pager.setPageSize( pageSize );
        // the index is the zero based page number.
        // we correct this via getPageNumber
        if ( thePage != null ) {
            int index = pager == null ? 0 : Integer.parseInt( thePage ) - 1;
            pager.setIndex( index );
        }
        else {
            pager.setIndex( 0 );
        }
    }

}
