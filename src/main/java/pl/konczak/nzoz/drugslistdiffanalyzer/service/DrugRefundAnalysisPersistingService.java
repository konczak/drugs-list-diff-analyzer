package pl.konczak.nzoz.drugslistdiffanalyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.konczak.nzoz.drugslistdiffanalyzer.dto.Diff;
import pl.konczak.nzoz.drugslistdiffanalyzer.dto.DiffType;
import pl.konczak.nzoz.drugslistdiffanalyzer.dto.DrugRefund;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DrugRefundAnalysisPersistingService {

    public void persist(final String filePath, final Map<DiffType, List<Diff>> diffsByType) throws Exception {
        File myFile = new File(filePath);
        myFile.createNewFile();

        // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook();

        createSheetWithAddDrugRefunds(myWorkBook, diffsByType);
        createSheetWithUpdatedDrugRefunds(myWorkBook, diffsByType);
        createSheetWithDeletedDrugRefunds(myWorkBook, diffsByType);

        FileOutputStream os = new FileOutputStream(myFile);
        myWorkBook.write(os);
        os.close();

        log.info("Saved results to file <{}>", filePath);
    }

    private XSSFCellStyle createHeadingStyle(final XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();

        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        XSSFCellStyle cellStyle = workbook.createCellStyle();

        cellStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        cellStyle.setFont(font);

        return cellStyle;
    }

    private Row createHeadingRow(final XSSFSheet sheet, final int rowNumber, final String[] headers, final XSSFCellStyle cellStyle) {
        Row row = sheet.createRow(rowNumber);

        for (int cellNumber = 0; cellNumber < headers.length; cellNumber++) {
            Cell cell = row.createCell(cellNumber);
            cell.setCellValue(headers[cellNumber]);
            cell.setCellStyle(cellStyle);
        }
        return row;
    }

    private Cell createCellWithContent(final Row row, final int column, final String content) {
        Cell cellName = row.createCell(column);
        cellName.setCellValue(content);
        return cellName;
    }

    private void createSheetWithAddDrugRefunds(final XSSFWorkbook myWorkBook, final Map<DiffType, List<Diff>> diffsByType) {
        List<Diff> diffs = diffsByType.getOrDefault(DiffType.ADD, Collections.emptyList());

        XSSFSheet mySheet = myWorkBook.createSheet("Dodane");

        int rowNumber = 0;

        String[] headers = {
                "Nazwa  postać i dawka",
                "Zawartość opakowania",
                "Kod EAN lub inny kod odpowiadający kodowi EAN",
                "Termin wejścia w życie decyzji",
                "Okres obowiązywania decyzji",
                "Zakres wskazań objętych refundacją",
                "Zakres wskazań pozarejestracyjnych objętych refundacją"
        };
        createHeadingRow(mySheet, rowNumber++, headers, createHeadingStyle(myWorkBook));

        for (Diff diff : diffs) {
            DrugRefund currentDrugRefund = diff.getCurrent();
            Row row = mySheet.createRow(rowNumber++);

            createCellWithContent(row, 0, currentDrugRefund.getName());
            createCellWithContent(row, 1, currentDrugRefund.getPackageContent());
            createCellWithContent(row, 2, currentDrugRefund.getEan());
            createCellWithContent(row, 3, currentDrugRefund.getRefundSince());
            createCellWithContent(row, 4, currentDrugRefund.getRefundPeriod());
            createCellWithContent(row, 5, currentDrugRefund.getIndicationsCoveredByRefund());
            createCellWithContent(row, 6, currentDrugRefund.getOffLabelIndicationsCoveredByRefund());
        }
    }

    private void createSheetWithUpdatedDrugRefunds(final XSSFWorkbook myWorkBook, final Map<DiffType, List<Diff>> diffsByType) {
        List<Diff> diffs = diffsByType.getOrDefault(DiffType.UPDATED, Collections.emptyList());

        XSSFSheet mySheet = myWorkBook.createSheet("Zmienione");

        XSSFCellStyle modifiedCellStyle = myWorkBook.createCellStyle();
        modifiedCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        modifiedCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        int rowNumber = 0;

        String[] headers = {
                "Nazwa  postać i dawka",
                "Zawartość opakowania",
                "Kod EAN lub inny kod odpowiadający kodowi EAN",
                "Termin wejścia w życie decyzji",
                "Okres obowiązywania decyzji",
                "Zakres wskazań objętych refundacją",
                "Zakres wskazań pozarejestracyjnych objętych refundacją",
                "Termin wejścia w życie decyzji (POPRZEDNIO)",
                "Okres obowiązywania decyzji (POPRZEDNIO)",
        };
        createHeadingRow(mySheet, rowNumber++, headers, createHeadingStyle(myWorkBook));

        for (Diff diff : diffs) {
            DrugRefund oldDrugRefund = diff.getOld();
            DrugRefund currentDrugRefund = diff.getCurrent();
            Row row = mySheet.createRow(rowNumber++);

            createCellWithContent(row, 0, currentDrugRefund.getName());
            createCellWithContent(row, 1, currentDrugRefund.getPackageContent());
            createCellWithContent(row, 2, currentDrugRefund.getEan());
            Cell cellRefundSince = createCellWithContent(row, 3, currentDrugRefund.getRefundSince());
            Cell cellRefundPeriod = createCellWithContent(row, 4, currentDrugRefund.getRefundPeriod());
            createCellWithContent(row, 5, currentDrugRefund.getIndicationsCoveredByRefund());
            createCellWithContent(row, 6, currentDrugRefund.getOffLabelIndicationsCoveredByRefund());

            Cell cellRefundSinceBefore = createCellWithContent(row, 7, oldDrugRefund.getRefundSince());
            Cell cellRefundPeriodBefore = createCellWithContent(row, 8, oldDrugRefund.getRefundPeriod());

            if (!Objects.equals(oldDrugRefund.getRefundSince(), currentDrugRefund.getRefundSince())) {
                cellRefundSince.setCellStyle(modifiedCellStyle);
                cellRefundSinceBefore.setCellStyle(modifiedCellStyle);
            }
            if (!Objects.equals(oldDrugRefund.getRefundPeriod(), currentDrugRefund.getRefundPeriod())) {
                cellRefundPeriod.setCellStyle(modifiedCellStyle);
                cellRefundPeriodBefore.setCellStyle(modifiedCellStyle);
            }
        }
    }

    private void createSheetWithDeletedDrugRefunds(final XSSFWorkbook myWorkBook, final Map<DiffType, List<Diff>> diffsByType) {
        List<Diff> diffs = diffsByType.getOrDefault(DiffType.DELETED, Collections.emptyList());

        XSSFSheet mySheet = myWorkBook.createSheet("Usunięte");

        int rowNumber = 0;

        String[] headers = {
                "Nazwa  postać i dawka",
                "Zawartość opakowania",
                "Kod EAN lub inny kod odpowiadający kodowi EAN",
                "Termin wejścia w życie decyzji",
                "Okres obowiązywania decyzji",
                "Zakres wskazań objętych refundacją",
                "Zakres wskazań pozarejestracyjnych objętych refundacją"
        };
        createHeadingRow(mySheet, rowNumber++, headers, createHeadingStyle(myWorkBook));

        for (Diff diff : diffs) {
            DrugRefund oldDrugRefund = diff.getOld();
            Row row = mySheet.createRow(rowNumber++);

            createCellWithContent(row, 0, oldDrugRefund.getName());
            createCellWithContent(row, 1, oldDrugRefund.getPackageContent());
            createCellWithContent(row, 2, oldDrugRefund.getEan());
            createCellWithContent(row, 3, oldDrugRefund.getRefundSince());
            createCellWithContent(row, 4, oldDrugRefund.getRefundPeriod());
            createCellWithContent(row, 5, oldDrugRefund.getIndicationsCoveredByRefund());
            createCellWithContent(row, 6, oldDrugRefund.getOffLabelIndicationsCoveredByRefund());
        }
    }
}
