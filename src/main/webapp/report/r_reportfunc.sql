INSERT INTO `r_reportfunc` VALUES ('findRData', '获取日数据', '日数据', '1', 'period:日期;code:代码;code:字段', 'select %col% from report_daytable where 1=1 and  period=\'%period%\' and code=\'%code%\'', '');
