<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/links.jsp"%>
<%@ include file="/common/bootStrapLinks.jsp"%>
<%@ include file="/common/bsgridLinks.jsp"%>
<html>
<head>
</head>
<script>
$(function() {
	 
    $("#demo_grid1").bs_grid({
 
        ajaxFetchDataURL: "${ctx}/primaryData/primaryDataGridList",
        row_primary_key: "customer_id",
 
        columns: [
            {field: "customer_id", header: "Code", visible: "no"},
            {field: "lastname", header: "Lastname"},
            {field: "firstname", header: "Firstname"},
            {field: "email", header: "Email", visible: "no", "sortable": "no"},
            {field: "gender", header: "Gender"},
            {field: "date_updated", header: "Date updated"}
        ],
 
        sorting: [
            {sortingName: "Code", field: "customer_id", order: "none"},
            {sortingName: "Lastname", field: "lastname", order: "ascending"},
            {sortingName: "Firstname", field: "firstname", order: "ascending"},
            {sortingName: "Date updated", field: "date_updated", order: "none"}
        ],
 
        filterOptions: {
            filters: [
                {
                    filterName: "Lastname", "filterType": "text", field: "lastname", filterLabel: "Last name",
                    excluded_operators: ["in", "not_in"],
                    filter_interface: [
                        {
                            filter_element: "input",
                            filter_element_attributes: {"type": "text"}
                        }
                    ]
                },
                {
                    filterName: "Gender", "filterType": "number", "numberType": "integer", field: "lk_genders_id", filterLabel: "Gender",
                    excluded_operators: ["equal", "not_equal", "less", "less_or_equal", "greater", "greater_or_equal"],
                    filter_interface: [
                        {
                            filter_element: "input",
                            filter_element_attributes: {type: "checkbox"}
                        }
                    ],
                    lookup_values: [
                        {lk_option: "Male", lk_value: "1"},
                        {lk_option: "Female", lk_value: "2", lk_selected: "yes"}
                    ]
                },
                {
                    filterName: "DateUpdated", "filterType": "date", field: "date_updated", filterLabel: "Datetime updated",
                    excluded_operators: ["in", "not_in"],
                    filter_interface: [
                        {
                            filter_element: "input",
                            filter_element_attributes: {
                                type: "text",
                                title: "Set the date and time using format: dd/mm/yyyy hh:mm:ss"
                            },
                            filter_widget: "datetimepicker",
                            filter_widget_properties: {
                                dateFormat: "dd/mm/yy",
                                timeFormat: "HH:mm:ss",
                                changeMonth: true,
                                changeYear: true,
                                showSecond: true
                            }
                        }
                    ],
                    validate_dateformat: ["DD/MM/YYYY HH:mm:ss"],
                    filter_value_conversion: {
                        function_name: "local_datetime_to_UTC_timestamp",
                        args: [
                            {"filter_value": "yes"},
                            {"value": "DD/MM/YYYY HH:mm:ss"}
                        ]
                    }
                }
            ]
        }
    });
 
});

</script>
</head>
<body>
	
	<div id="demo_grid1"></div>
</body>
</html>