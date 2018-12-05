
package tradingapplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.apache.poi.ss.usermodel.*;

/**
 *
 * @author Zarkovic
 */
public class ImportExcel {
    
    
     //importing difference percentage from selected path
    public double importDiffPercentageFromPredefinedExcelFile(String path) {
        double differencePercentage = 0;
        try {
            Workbook workbook = WorkbookFactory.create(new File(path));
            //getting the sheet at index zero
            Sheet sheet = workbook.getSheetAt(0);
            //create a dataformatter to format and get each cell'log value as string
            DataFormatter dataFormatter = new DataFormatter();
            //using for-each loop to iterate over the rows and columns
            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = dataFormatter.formatCellValue(cell);
                    if (!cellValue.equals("DiffPercentage")) {
                        double differencePercentage1 = Double.parseDouble(cellValue);
                    }
                }
            }
            CustomLogger log = new CustomLogger();
            log.addToLog("Entry: difference percentage added successfuly");
            log.addToLog(System.getProperty("line.separator"));
        } catch (IOException e) {
            try {
                CustomLogger log = new CustomLogger();
                log.addToLog("Entry: difference percentage is not added");
                log.addToLog(System.getProperty("line.separator"));
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(ImportExcel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return differencePercentage;
    }
    
    //importing excel file with id symbols values as list
    ArrayList<String> allSymbols = new ArrayList<String>();

    public ArrayList<String> importSymbolsFromPredefinedExcelFile(String path) {

        try {
            Workbook workbook = WorkbookFactory.create(new File(path));
            //getting the sheet at index zero
            Sheet sheet = workbook.getSheetAt(0);
            //create a dataformatter to format and get each cell'log value as string
            DataFormatter dataFormatter = new DataFormatter();
            //for-each loop to iterate over the rows and columns
            for (Row row : sheet) {
                for (Cell cell : row) {
                    String cellValue = dataFormatter.formatCellValue(cell).toLowerCase();
                    if (!cellValue.equals("symbol")) {
                        allSymbols.add(cellValue);
                    }
                }
            }
        } catch (IOException e) {
            try {
                CustomLogger log = new CustomLogger();
                log.addToLog("Entry failed");
                log.addToLog(System.getProperty("line.separator"));
            } catch (FileNotFoundException ex) {
                java.util.logging.Logger.getLogger(ImportExcel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return allSymbols;
    }
}
