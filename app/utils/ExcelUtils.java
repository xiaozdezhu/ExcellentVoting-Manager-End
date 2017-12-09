package utils;

import models.Department;
import models.Employee;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Felix
 * Date :  16/3/21.
 * Desc :
 */
public final class ExcelUtils {



    public static boolean export(List<List<Employee>> cityCompanyList) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        for (List<Employee> companyList : cityCompanyList) {
            // -- city sheet
            if (null == companyList || 0 == companyList.size()) {
                continue;
            }
            Department city = companyList.get(0).department;
            HSSFSheet citySheet = workbook.createSheet(city.name);

            HSSFRow row = null;
            HSSFCell cell = null;
            HSSFCellStyle style = workbook.createCellStyle();
            style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
            style.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);

            row = citySheet.createRow(0);

            // -- 标题栏
            cell = row.createCell(0);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);// 设置列中写入内容为String类型
            cell.setCellValue("候选人名称");// 写入列名
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);// 设置列中写入内容为String类型
            cell.setCellValue("得票数");// 写入列名
            cell.setCellStyle(style);

            int rowNum = 1;
            for (Employee company : companyList) {
                row = citySheet.createRow(rowNum++);

                cell = row.createCell(0);
                cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(company.name);
                cell = row.createCell(1);
                cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                cell.setCellValue(company.votes);
            }

        }

        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream("public/download/votes.xls");
            workbook.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != workbook) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }
}
