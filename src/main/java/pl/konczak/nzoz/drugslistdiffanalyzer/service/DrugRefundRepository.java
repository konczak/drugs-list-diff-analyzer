package pl.konczak.nzoz.drugslistdiffanalyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.konczak.nzoz.drugslistdiffanalyzer.dto.DrugRefund;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class DrugRefundRepository {

    public List<DrugRefund> findDrugRefunds(final String filePath) throws Exception {
        File myFile = new File(filePath);

        FileInputStream fis = new FileInputStream(myFile);
        // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook(fis);
        // Return first sheet from the XLSX workbook
        XSSFSheet mySheet = myWorkBook.getSheetAt(0);

        Iterator<Row> rowIterator = mySheet.iterator();

        //skip headings
        rowIterator.next();
        rowIterator.next();
        rowIterator.next();

        log.info("Skip 3 first rows as heading");

        List<DrugRefund> drugRefunds = new ArrayList<>();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            DrugRefund drugRefund = extract(row);

            drugRefunds.add(drugRefund);
        }

        return drugRefunds;
    }

    private DrugRefund extract(final Row row) {
        return DrugRefund.builder()
                .name(row.getCell(2).getStringCellValue())
                .packageContent(row.getCell(3).getStringCellValue())
                .ean(row.getCell(4).getStringCellValue())
                .refundSince(row.getCell(5).getStringCellValue())
                .refundPeriod(row.getCell(6).getStringCellValue())
                .indicationsCoveredByRefund(row.getCell(12).getStringCellValue())
                .offLabelIndicationsCoveredByRefund(row.getCell(13).getStringCellValue())
                .build();
    }
}
