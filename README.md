# StockTradingJavaApplication
Simple Java program for calculating difference percentage for specific companies symbols list inExcel Sheet and with static criteria

It was request from client. I'm copying entire task:

Report Stocks using Finance API – Requirements

Java program which report stocks trading in US exchanges based on their previous day closing and current day opening.
Features are listed below.

Input Files:
InputFile - 1 (RunCriteria.xlsx): 
This is a control file and always has only 1 record. This record has only 1 field named ‘DiffPercentage’. 
DiffPercentage contains a value between 0 and 100 (Example 24.5). See the sample layout below: 
  DiffPercentage
  5.5

InputFile-2 (StockSymbols.xlsx):
This is the main input file and it has 1 to 99999 records. Each record contains only 1 field named ‘Symbol’.
Symbol contains the ticker of the stocks listed in US stock exchanges (Example: AAPL). See layout below:
Symbol
GOOG
MSFT
AAPL

Output File (StockReport_YYYYMMDD.xlsx):
Program is scheduled to run once in everyday and each run should create an output file named StockReport_YYYYMMDD.txt. (Where YYYYMMDD is the Year, Month, Day of the actual run date).
May contain 1 to 99999 records in the output. Each record contains 5 fields.
Symbol	 PDC	SDO	 Difference	 Direction
GOOG	 112.27	 92.59	   -17.53	 DOWN
AAPL	 47.38	 51.70	    9.12	 UP

PDC: Previous Day Closing Price of the Stock (Returned by the API)
SDO: Same Day Opening Price of the Stock (Returned by the API)
Difference: Difference between PDC & SDO in percentage (%)
Direction: UP or DOWN (If SDO > PDC then ‘UP’ else ‘DOWN’)

Main Processing Logic:
1. Read the InputFile-1 (RunCriteria.xlsx file) and get the value of ‘DiffPercentage’.
2. Open the Output File (StockReport_YYYYMMDD.xlsx).
3. Read and process all the records in the InputFile-2 (StockSymbols.txt) as below:
3A. Read the first record in the InputFile-2 
3B. Call the API (See details below) using the ‘Symbol’ read in STEP-2a and get the
 PDC (Previous Day Closing) & SDO (Same Day Open) prices for that stock.
3C. IF the SDO > PDC
       if  SDO >= (PDC + (PDC * (DiffPercentage / 100))) 
3C1.       Then write a new record in the Output File (See details below)
       end-if
    ELSE
       if SDO <= (PDC - (PDC * (DiffPercentage /100)))
3C2.       Then write a new record in the Ouput File (See details below)
       end-if
    END-IF
3D. Read the next record in the InputFile-2
3E. Repeat the STEPS 3B TO 3D until all records in the InputFile-2 is read & processed.
4. Close the Output File (StockReport_YYYYMMDD.xlsx).

Additional Processing Logic:
STEP-3B Details:
One of the best and free APIs currently available for financial data is provided by IEXTRADING.COM. 
Their API development guidelines can be found on their site at https://iextrading.com/developer/docs/
In addition, they are providing a link to the unofficial code libraries (including Java) developed by
individuals as samples. https://iextrading.com/developer/docs/#unofficial-libraries-and-integrations

STEP-3C1 Details:
Needs to populate 5 fields while writing a new Output Record:
1. SYMBOL - The one used as the Input for the API call
2. PDC - Previous Day Closing Price returned by API for that stock
3. SDO - Same Day Open Price returned by API for that stock
4. DIFFERENCE - Calculate as (( (SDO - PDC) / PDC) * 100)
5. DIRECTION - set as ‘UP’

STEP-3C2 Details:
Needs to populate 5 fields while writing a new Output Record:
1.	SYMBOL - The one used as the Input for the API call
2.	PDC - Previous Day Closing Price returned by API for that stock
3.	SDO - Same Day Open Price returned by API for that stock
4.	DIFFERENCE - Calculate as (( (PDC - SDO) / PDC) * 100)
5.	DIRECTION - set as ‘DOWN’

