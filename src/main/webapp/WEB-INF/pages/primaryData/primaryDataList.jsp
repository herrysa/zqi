<!DOCTYPE html>
<%@page contentType="text/html; charset=UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
</head>
<script>
$(function() {
    $("#demo_grid1").bs_grid({
 
        ajaxFetchDataURL: "${ctx}/primaryData/primaryDataGridList?gpCode=${gpCode}&period=${period}",
        row_primary_key: "period",
 
        columns: [
            {field: "period", header: "日期"},
            {field: "code", header: "编码"},
            {field: "name", header: "名称"},
            {field: "open", header: "开盘价"},
            {field: "close", header: "收盘价"},
            {field: "high", header: "最高价"}
        ],
 
        sorting: [
            {sortingName: "period", field: "period", order: "descending"},
            {sortingName: "code", field: "code", order: "ascending"},
            {sortingName: "open", field: "open", order: "none"},
            {sortingName: "close", field: "close", order: "none"}
        ]
 
        /* filterOptions: {
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
        } */
    });
 
});

</script>
</head>
<body>
	
	<div id="demo_grid1"></div>
</body>
</html>