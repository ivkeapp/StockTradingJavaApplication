/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tradingapplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;

/**
 *
 * @author Zarkovic
 */
public class TradingApplication extends javax.swing.JFrame {

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JLabel mainLabel;
    private String stringDate;
    private String path1 = "";
    private String path2 = "";
    CustomLogger log = new CustomLogger();

    public TradingApplication() {
        this.setLocationRelativeTo(null);
        //this.setVisible(true);
        initComponents();
    }

    private String stringDate() throws FileNotFoundException {
        //getting date and time from local machine
        LocalTime time = LocalTime.now();
        LocalDate date = LocalDate.now();
        //setting date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYYMMdd");
        //string as fileoutputs parameter
        stringDate = formatter.format(date);
        log.addToLog("Date: " + stringDate + "  Time: " + time.toString());
        return stringDate;
    }

    private ArrayList<String> listOfStockSymbols() {
        ImportExcel excelread = new ImportExcel();
        //manual path selection of import files, to be done
        String path2 = jTextField2.getText();
        //importing id symbols and putting it to list of strings
        ArrayList<String> listOfStockSymbols = excelread.importSymbolsFromPredefinedExcelFile(path2);
        return listOfStockSymbols;
    }

    private double diffPercentage() {
        ImportExcel excelread = new ImportExcel();
        //manual path selection of import files, to be done
        String path1 = jTextField2.getText();
        //assuming that the difference percentage is fixed
        double diffPercentage = excelread.importDiffPercentageFromPredefinedExcelFile(path1);

        return diffPercentage;
    }

    private Object[][] addingValuesToArrays(ArrayList<String> listOfStockSymbols) throws JSONException, IOException {
        ConnectionToAPI connectionToAPI = new ConnectionToAPI();
        Object[][] mainDataArrays = new Object[listOfStockSymbols.size() + 1][5];
        //defining rows header - could be in another method
        mainDataArrays[0][0] = "Symbol";
        mainDataArrays[0][1] = "PDC";
        mainDataArrays[0][2] = "SDO";
        mainDataArrays[0][3] = "Difference";
        mainDataArrays[0][4] = "Direction";
        int br = 1;
        //System.out.println("Size of a symbols list: " + listOfStockSymbols.size());
        //adding values to 2d array
        //making an object which hold data to be exported as Excel file
        //using jdbctoapi class methods for getting response and parsing it to an objects
        //passing id symbol and fixed difference percentage(?)
        for (String s : listOfStockSymbols) {
            Object[] helpArray;
            helpArray = connectionToAPI.connectToAPIAndParseValues(s, diffPercentage());
            if (helpArray[4] != "NO DATA") {
                for (int i = 0; i < 5; i++) {
                    mainDataArrays[br][i] = helpArray[i];
                }
                br++;
            }
        }
        return mainDataArrays;
    }

    private XSSFWorkbook makingWorkbook(Object[][] mainDataArrays) throws FileNotFoundException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Daily report from IEXTrading");

        //init cell style
        ExcelSheetCellStylesLibrary style = new ExcelSheetCellStylesLibrary();
        Map<String, CellStyle> styles = style.createStyles(workbook);

        int rowNum = 0;
        System.out.println("Writing Excel file...");
        log.addToLog("Creating Excel file." + "\n");
        //looping through 2d array and adding values to the excel sheet
        if (mainDataArrays != null) {
            for (int i = 0; i < mainDataArrays.length; i++) {
                Row row = sheet.createRow(rowNum++);
                int colNum = 0;
                for (int j = 0; j < 5; j++) {
                    Cell cell = row.createCell(colNum++);
                    if (i == 0) {
                        cell.setCellStyle(styles.get("header"));
                    }
                    if (mainDataArrays[i][j] instanceof String) {
                        cell.setCellValue((String) mainDataArrays[i][j]);
                    } else if (mainDataArrays[i][j] instanceof Double) {
                        cell.setCellValue((Double) mainDataArrays[i][j]);
                    }
                }
            }
        }
        return workbook;
    }

    private void writtingExcelFile(XSSFWorkbook workbook) throws FileNotFoundException {

        try {
            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Zarkovic\\Desktop\\StockReport_" + stringDate() + ".xlsx");
            workbook.write(outputStream);
            workbook.close();
            System.out.println("File written successfully!");
            log.addToLog("File written successfully!");
            jButton3.setText("Done!");
            jButton3.setEnabled(false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("FAILED! The process cannot access the file because it is being used by another process or file is missing.");
            log.addToLog("FAILED! The process cannot access the file because it is being used by another process or file is missing.");
        } catch (IOException e) {
            e.printStackTrace();
            log.addToLog(e.getLocalizedMessage());
        }

    }

    void mainMethod() throws FileNotFoundException {

        try {
            //help string
            String APIUrl = "https://api.iextrading.com/1.0/stock/market/batch?symbols=goog,aapl&types=quote&filter=previousClose,open";

            ArrayList<String> listOfStockSymbols = listOfStockSymbols();
            //instantiate 2d array for values which size is equal to a size of symbols list plus rows header
            Object[][] mainDataArrays = addingValuesToArrays(listOfStockSymbols);
            log.addToLog("Values have been successfully loaded from IEXTrading API.");
            //apache poi libraries for creating excel file
            XSSFWorkbook workbook = makingWorkbook(mainDataArrays);

            //writing output .xlsx file to the disk with current date parrameter
            writtingExcelFile(workbook);

        } catch (FileNotFoundException ex) {
            log.addToLog("Exception cought:" + ex);
        } catch (JSONException ex) {
            log.addToLog("Exception cought:" + ex);
        } catch (IOException ex) {
            log.addToLog("Exception cought:" + ex);
        }

    }

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        mainLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setForeground(new java.awt.Color(51, 51, 51));

        jLabel1.setText("HEADER");

        jLabel2.setFont(new java.awt.Font("Roboto", 0, 11)); // NOI18N
        jLabel2.setText("Select RunCriteria.xlsx path:");

        jTextField1.setFont(new java.awt.Font("Roboto", 0, 11)); // NOI18N
        jTextField1.setText("Choose path of a file");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Roboto", 0, 11)); // NOI18N
        jButton1.setText("Choose");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Roboto", 0, 11)); // NOI18N
        jLabel3.setText("Select StockSymbols.xlsx path:");
        jLabel3.setToolTipText("");

        jTextField2.setFont(new java.awt.Font("Roboto", 0, 11)); // NOI18N
        jTextField2.setText("Choose path of a file");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Roboto", 0, 11)); // NOI18N
        jButton2.setText("Choose");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Roboto", 0, 11)); // NOI18N
        jButton3.setText("Get report");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        mainLabel.setFont(new java.awt.Font("Roboto", 0, 18)); // NOI18N
        mainLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jTextField1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton1))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jTextField2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton2))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGap(284, 284, 284)
                                                                .addComponent(jLabel1))
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(235, 235, 235)
                                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                .addGap(249, 249, 249))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(mainLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addGap(40, 40, 40)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton1))
                                .addGap(24, 24, 24)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton2))
                                .addGap(18, 18, 18)
                                .addComponent(mainLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {

    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser jfc = new JFileChooser();
        jfc.showDialog(null, "Select path");
        jfc.setVisible(true);
        File filename = jfc.getSelectedFile();
        if (filename.getName().equals("RunCriteria.xlsx")) {
            path1 = filename.getPath();
            jTextField1.setText(filename.getPath());
            try {
                log.addToLog("File name " + filename.getName() + " " + filename.getPath());
            } catch (FileNotFoundException ex) {
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Please find RunCriteria.xlsx",
                    "Wrong file selected",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {
        //to do
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        JFileChooser jfc = new JFileChooser();
        jfc.showDialog(null, "Select path");
        jfc.setVisible(true);
        File filename = jfc.getSelectedFile();
        if (filename.getName().equals("StockSymbols.xlsx")) {
            path2 = filename.getPath();
            jTextField2.setText(filename.getPath());
            try {
                log.addToLog("File name " + filename.getName() + " " + filename.getPath());
            } catch (FileNotFoundException ex) {
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Please find StockSymbols.xlsx",
                    "Wrong file selected",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        //to do
    }

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TradingApplication().setVisible(true);
            }
        });
    }
}
