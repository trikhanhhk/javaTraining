package com.javateam.mgep.service.excel;

import com.javateam.mgep.entity.Authoritty;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.AuthorityRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

public class ExcelGeneratorListEmployee {
    private List<Employee> employeeList;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public ExcelGeneratorListEmployee(List <Employee> employeeList) {
        this.employeeList = employeeList;
        workbook = new XSSFWorkbook();
    }
    private void writeHeader() {
        sheet = workbook.createSheet("Nhân Viên");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row, 0, "ID", style);
        createCell(row, 1, "Họ", style);
        createCell(row, 2, "Tên", style);
        createCell(row, 3, "Giới tính", style);
        createCell(row, 4, "Ngày sinh", style);
        createCell(row, 5, "Số điện thoại", style);
        createCell(row, 6, "Email", style);
        createCell(row, 7, "Quê quán", style);
        createCell(row, 8, "Phòng ban", style);
        createCell(row, 9, "Chức vụ", style);
    }
    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else {
            cell.setCellValue((Boolean) valueOfCell);
        }
        cell.setCellStyle(style);
    }
    private void write() {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        for (Employee record: employeeList) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;
            createCell(row, columnCount++, record.getId(), style);
            createCell(row, columnCount++, record.getFirstName(), style);
            createCell(row, columnCount++, record.getLastName(), style);
            createCell(row, columnCount++, record.getGender() == "1" ? "Nam" : "Nữ", style);
            createCell(row, columnCount++, simpleDateFormat.format(record.getDateOfBirth()), style);
            createCell(row, columnCount++, record.getPhoneNumber(), style);
            createCell(row, columnCount++, record.getEmail(), style);
            createCell(row, columnCount++, record.getAddress(), style);
            createCell(row, columnCount++, record.getDepartment().getName(), style);
            Set<Authoritty> authoritties = record.getAuthorities();
            String position = "Nhân viên";
            for (Authoritty a: authoritties) {
                if (a.getName().equals("ROLE_MANAGER") || a.getName().equals("ROLE_ADMIN")) position = "Manager";
            }
            createCell(row, columnCount++, position, style);
        }
    }
    public void generateExcelFile(HttpServletResponse response) throws IOException {
        writeHeader();
        write();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
