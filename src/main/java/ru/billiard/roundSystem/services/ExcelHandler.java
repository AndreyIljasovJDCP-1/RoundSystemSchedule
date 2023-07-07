package ru.billiard.roundSystem.services;

import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.billiard.roundSystem.FileHandler;
import ru.billiard.roundSystem.models.Player;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@NoArgsConstructor
public class ExcelHandler implements FileHandler {

    private static final Resource XLSX_FILE = new ClassPathResource("static/list.xlsx");

    @Override
    public List<Player> read() {
        List<Player> list = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(XLSX_FILE.getFile())) {
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Iterator<Cell> cells = row.iterator();
                Player player = new Player();
                while (cells.hasNext()) {
                    Cell cell = cells.next();
                    CellType cellType = cell.getCellType();

                    if (cellType == CellType.NUMERIC) {
                        player.setRate((int) cell.getNumericCellValue());
                    } else if (cellType == CellType.STRING) {
                        player.setName(cell.getStringCellValue());
                    }
                }
                list.add(player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void write(List<List<Player>> schedule, int gamesInTour) {
        var workbook = new XSSFWorkbook();
        var sheet = createSheet(workbook);
        createHeader(workbook, sheet);
        createCells(workbook, sheet, schedule, gamesInTour);
        try (var outputStream = new FileOutputStream("schedule.xlsx")) {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Sheet createSheet(XSSFWorkbook workbook) {
        var sheet = workbook.createSheet("Расписание");
        sheet.autoSizeColumn(0, true);
        sheet.autoSizeColumn(1, true);
        sheet.autoSizeColumn(2, true);
        sheet.autoSizeColumn(3, true);
        sheet.autoSizeColumn(4, true);
        sheet.autoSizeColumn(5, true);
        return sheet;
    }

    private void createHeader(XSSFWorkbook workbook, Sheet sheet) {
        var header = sheet.createRow(0);
        var headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        var font = workbook.createFont();
        font.setFontName("Calibri");
        font.setBold(true);
        headerStyle.setFont(font);

        var headerCell = header.createCell(0);
        headerCell.setCellValue("N п/п");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("ФИО");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue("Рейтинг");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue("VS");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue("ФИО");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue("Рейтинг");
        headerCell.setCellStyle(headerStyle);
    }

    private void createCells(XSSFWorkbook workbook, Sheet sheet, List<List<Player>> schedule, int gamesInTour) {
        var style = workbook.createCellStyle();
        var offset = 1;
        for (var i = 0; i < schedule.size(); i++) {

            if (i % gamesInTour == 0) {
                var row = sheet.createRow(i + offset++);
                var cell = row.createCell(0);
                cell.setCellValue((i / gamesInTour + 1) + " Тур");
                sheet.addMergedRegion(CellRangeAddress.valueOf(
                        String.format("A%d:H%d", i + offset, i + offset)));
            }
            style.setAlignment(HorizontalAlignment.GENERAL);
            var player = schedule.get(i).get(0);
            var row = sheet.createRow(i + offset);

            var cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue(i + 1);

            cell = row.createCell(1);
            cell.setCellStyle(style);
            cell.setCellValue(player.getName());

            cell = row.createCell(2);
            cell.setCellStyle(style);
            cell.setCellValue(player.getRate());

            cell = row.createCell(3);
            cell.setCellStyle(style);
            cell.setCellValue("-");

            player = schedule.get(i).get(1);

            cell = row.createCell(4);
            cell.setCellStyle(style);
            cell.setCellValue(player.getName());

            cell = row.createCell(5);
            cell.setCellStyle(style);
            cell.setCellValue(player.getRate());
        }
    }
}
