package ru.billiard.roundSystem.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.billiard.roundSystem.models.Player;
import ru.billiard.roundSystem.repositories.PlayerRepository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Component
public class Schedule {
    private static final Resource XLSX_FILE = new ClassPathResource("static/list.xlsx");
    @Autowired
    PlayerRepository playerRepository;

    public void load() {
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
                playerRepository.save(player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printTable() {
        int[][][] gameTable = generateTable();
        List<Player> playerList = playerRepository.all();
        int amountPlayers = playerList.size();
        int amountTours = amountPlayers - 1;
        int amountGamesInTour = amountPlayers / 2;
        for (int i = 0; i < amountTours; i++) {
            System.out.printf("       %d  Тур.%n", i + 1);
            for (int j = 0; j < amountGamesInTour; j++) {
                int home = gameTable[i][j][0];
                int guest = gameTable[i][j][1];
                System.out.printf("%d. %s - %s%n", j + 1,
                        playerList.get(home - 1),
                        playerList.get(guest - 1));
            }
        }
    }

    /**
     * Формирование листа пар игроков по всем играм
     * @return List пар игроков по играм
     */
    public List<List<Player>> getTableAsList() {
        int[][][] gameTable = generateTable();
        List<Player> playerList = playerRepository.all();
        List<List<Player>> schedule = new ArrayList<>();
        int amountPlayers = playerList.size();
        int amountTours = amountPlayers - 1;
        int amountGamesInTour = amountPlayers / 2;
        for (int i = 0; i < amountTours; i++) {

            for (int j = 0; j < amountGamesInTour; j++) {
                int home = gameTable[i][j][0];
                int guest = gameTable[i][j][1];
                schedule.add(new ArrayList<>(List.of(
                        playerList.get(home - 1), playerList.get(guest - 1))));
            }
        }
        return schedule;
    }

    /**
     * Составление расписания турнира по круговой системе 1 круг,
     * с учетом домашних и гостевых встреч.
     * Сделано с помощью 3-х мерного массива.
     * Алгоритм заполнение пустых граф в таблице,
     * см. <a href="http://chess.sainfo.ru/tabl/tablei.html">
     * как провести круговой турнир
     * </a>
     */
    private int[][][] generateTable() {
        if (playerRepository.all().size() % 2 != 0) {
            playerRepository.save(new Player("пропуск", 0));
        }
        int amountPlayers = playerRepository.all().size();
        int amountTours = amountPlayers - 1;
        int amountGamesInTour = amountPlayers / 2;
        int[][][] gameTable = new int[amountTours][amountGamesInTour][2];

        //1 этап: записываем последнего.
        for (int i = 0; i < amountTours; i++) {
            gameTable[i][0][(i + 1) % 2] = amountPlayers;
        }
        // 2 этап заполняем 1234567 итд по строкам
        int count = 1;
        for (int i = 0; i < amountTours; i++) {
            for (int j = 0; j < amountGamesInTour; j++) {
                if (gameTable[i][j][0] == 0) {
                    gameTable[i][j][0] = count;
                } else {
                    gameTable[i][j][1] = count;
                }
                count++;
                count = count > amountTours ? 1 : count;
            }
        }
        //третий этап заполняем оставшиеся места 7654321 по строкам
        count = amountTours;
        for (int i = 0; i < amountTours; i++) {
            for (int j = 0; j < amountGamesInTour; j++) {
                if (gameTable[i][j][0] == 0) {
                    gameTable[i][j][0] = count;
                    count--;
                } else if (gameTable[i][j][1] == 0) {
                    gameTable[i][j][1] = count;
                    count--;
                } else {
                    continue;
                }
                count = count > 0 ? count : amountTours;
            }
        }
        return gameTable;
    }
    //todo Алгоритм пересмотреть

    /**
     * 1 всегда дома, простая схема
     * Составление расписания турнира по круговой системе 1 круг,
     * без учета домашних и гостевых встреч.
     * Сделано с помощью 3-х мерного массива и Deque.
     * Алгоритм поворот по часовой стрелке, см.
     * <a href="https://b2b.partcommunity.com/community/
     * knowledge/ru/detail/9893/%D0%9A%D1%80%D1%83%D0%B3%D0%BE%D0%B2%D0%B0%D1%8F
     * +%D1%81%D0%B8%D1%81%D1%82%D0%B5%D0%BC%D0%B0#knowledge_article">круговая система</a>
     */
    public int[][][] generateSimpleTable() {
        if (playerRepository.all().size() % 2 != 0) {
            playerRepository.save(new Player("пропуск", 0));
        }
        int amountPlayers = playerRepository.all().size();
        int amountTours = amountPlayers - 1;
        int amountGameInTour = amountPlayers / 2;
        int[][][] gameTable = new int[amountGameInTour][amountTours][2];
        Deque<Integer> deque = new ArrayDeque<>();

        for (int i = amountTours + 1; i >= 2; i--) {
            deque.addLast(i);
        }

        for (int j = 0; j < amountTours; j++) {
            for (int i = 0; i < amountTours; i++) {
                int next = deque.pollFirst();
                if (i == 0) {
                    gameTable[i][j][0] = (i + 1);
                    gameTable[i][j][1] = next;
                } else if (i < amountGameInTour) {
                    gameTable[i][j][1] = next;
                } else {
                    gameTable[amountTours - i][j][0] = next;
                }
                deque.offerLast(next);
                if (i == amountTours - 1) {
                    int offset = deque.pollLast();
                    deque.offerFirst(offset);
                }
            }
        }
        return gameTable;
    }

    //todo сохранить таблицу игр в файл Excel
    public void write() {
        var workbook = new XSSFWorkbook();
        var sheet = createSheet(workbook);
        createHeader(workbook, sheet);
        createCells(workbook, sheet);
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

    private void createCells(XSSFWorkbook workbook, Sheet sheet) {
        var style = workbook.createCellStyle();

        var schedule = getTableAsList();
        var games = playerRepository.all().size() / 2;

        var offset = 1;
        for (var i = 0; i < schedule.size(); i++) {

            if (i % games == 0) {
                var row = sheet.createRow(i + offset++);
                var cell = row.createCell(0);
                cell.setCellValue((i / games + 1) + " Тур");
                sheet.addMergedRegion(CellRangeAddress.valueOf(
                        String.format("A%d:H%d", i + offset, i + offset)));
            }
            style.setAlignment(HorizontalAlignment.GENERAL);
            var client = schedule.get(i).get(0);
            var row = sheet.createRow(i + offset);

            var cell = row.createCell(0);
            cell.setCellStyle(style);
            cell.setCellValue(i + 1);

            cell = row.createCell(1);
            cell.setCellStyle(style);
            cell.setCellValue(client.getName());

            cell = row.createCell(2);
            cell.setCellStyle(style);
            cell.setCellValue(client.getRate());

            cell = row.createCell(3);
            cell.setCellStyle(style);
            cell.setCellValue("-");

            client = schedule.get(i).get(1);

            cell = row.createCell(4);
            cell.setCellStyle(style);
            cell.setCellValue(client.getName());

            cell = row.createCell(5);
            cell.setCellStyle(style);
            cell.setCellValue(client.getRate());
        }
    }
}
