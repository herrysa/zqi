package com.zqi.unit;

import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;

public class ExcelUtil {

	public static String getValue(Cell cell) {
		int cellType = cell.getCellType();
		String value = "";
		switch (cellType) {   
		  
        case Cell.CELL_TYPE_FORMULA:  
        	try {
        		value = ""+cell.getNumericCellValue();
			} catch (Exception e) {
				value = ""+cell.getStringCellValue();
			}
            break;   

        case Cell.CELL_TYPE_NUMERIC:   
            if(HSSFDateUtil.isCellDateFormatted(cell)){   
            	Date dateValue = cell.getDateCellValue();
                value = ""  
                    + DateUtil.convertDateToString(dateValue);   
            }else{   
                value = ""  
                        + cell.getNumericCellValue();   
            }   
               
            break;   

        case Cell.CELL_TYPE_STRING:   
            value = ""  
                    + cell.getStringCellValue();   
            break;   
               
        case Cell.CELL_TYPE_BOOLEAN:   
            value = ""  
                    + cell.getBooleanCellValue();   
               
            break;   

        default:   
        }   
		return value;
	}
}
