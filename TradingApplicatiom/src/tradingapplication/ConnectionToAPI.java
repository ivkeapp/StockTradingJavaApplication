package tradingapplication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Zarkovic
 */
public class ConnectionToAPI {

    private String gettingResponseFromAPI(String symbol) throws FileNotFoundException {

        try {
            //System.out.println("Getting values from IEXTrading API");

            //url which hold path to api filtered by symbol
            String url = "https://api.iextrading.com/1.0/stock/market/batch?symbols=" + symbol + "&types=quote&filter=previousClose,open";

            CustomLogger log = new CustomLogger();
            log.addToLog("Connecting to IEXTrading API: " + url + "\n");

            //System.out.println("attempt: " + (++br));
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //GET from API
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String sResponse = response.toString();
            log.addToLog("Response from server: " + response.toString());
            return sResponse;
        } catch (MalformedURLException ex) {
            CustomLogger log = new CustomLogger();
            log.addToLog("Exception caught: " + ex);
        } catch (UnknownHostException uhe) {
            System.out.println("No connection!");
            CustomLogger log = new CustomLogger();
            log.addToLog("Exception caught: " + uhe);
        } catch (ProtocolException ex) {
            CustomLogger log = new CustomLogger();
            log.addToLog("Exception caught: " + ex);
        } catch (IOException ex) {
            CustomLogger log = new CustomLogger();
            log.addToLog("Exception caught: " + ex);
        }
        return null;
    }
    
    private JSONObject parsingJSON(String symbol, String response) throws ParseException {

        //parsing response in JSON object
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        JSONObject result = (JSONObject) json.get(symbol.toUpperCase());
        return result;
    }

    private double gettingPDCValues(JSONObject result) {

        JSONObject result2 = (JSONObject) result.get("quote");
        String StringPDC = result2.get("previousClose").toString();
        Double PDC = Double.parseDouble(StringPDC);
        return PDC;
    }

    private double gettingSDOValues(JSONObject result) {

        JSONObject result2 = (JSONObject) result.get("quote");
        String StringPDC = result2.get("open").toString();
        Double SDO = Double.parseDouble(StringPDC);
        return SDO;
    }
    
    private Object[] formulaMethod(String symbol, double PDC, double SDO, double DiffPercentage) throws FileNotFoundException {
        if (SDO >= (PDC + (PDC * (DiffPercentage / 100)))) {
            Double difference = (((SDO - PDC) / PDC) * 100);
            //formating difference on two decimals
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            difference = Double.parseDouble(df.format(difference));

            Object[] object = new Object[]{symbol.toUpperCase(), PDC, SDO, difference, "UP"};

            CustomLogger log = new CustomLogger();
            log.addToLog("UP values added successfully!");
            return object;
        } else if (SDO <= (PDC - (PDC * (DiffPercentage / 100)))) {
            Double difference = (((PDC - SDO) / PDC) * 100);
            //formating difference on two decimals
            DecimalFormat df = new DecimalFormat("#.##");
            df.setRoundingMode(RoundingMode.CEILING);
            difference = Double.parseDouble(df.format(difference));

            Object[] object = new Object[]{symbol.toUpperCase(), PDC, SDO, difference, "DOWN"};

            CustomLogger log = new CustomLogger();
            log.addToLog("DOWN values added successfully!");
            log.addToLog(System.getProperty("line.separator"));
            return object;

        } else {
            CustomLogger log = new CustomLogger();
            log.addToLog(System.getProperty("line.separator"));
            return new Object[]{symbol.toUpperCase(), 0, 0, 0, "NO DATA"};
        }
    }
    
    public Object[] connectToAPIAndParseValues(String symbol, double DiffPercentage) throws JSONException, IOException {

        try {
            CustomLogger log = new CustomLogger();
            log.addToLog("Getting values for: " + symbol);
            //calling gettingresponse method and initializing response object
            String response = gettingResponseFromAPI(symbol);
            //calling method for parsing response in JSON object
            JSONObject result = parsingJSON(symbol, response);

            if (result != null) {

                log.addToLog("Adding: " + symbol.toUpperCase());
                Double PDC = gettingPDCValues(result);
                Double SDO = gettingSDOValues(result);
                System.out.println("Data imported");

                if (SDO > PDC) {
                    return formulaMethod(symbol, PDC, SDO, DiffPercentage);
                } else {
                    log.addToLog(System.getProperty("line.separator"));
                    return new Object[]{symbol.toUpperCase(), 0, 0, 0, "NO DATA"};
                }
            } else {
                log.addToLog(System.getProperty("line.separator"));
                return new Object[]{symbol.toUpperCase(), 0, 0, 0, "NO DATA"};
            }

        } 
        catch (ParseException | FileNotFoundException e) {
            CustomLogger log = new CustomLogger();
            log.addToLog("Error! File is not found");
            log.addToLog(e.getLocalizedMessage());
        } 
        //log.addToLog(System.getProperty("line.separator"));
        return new Object[]{symbol.toUpperCase(), 0, 0, 0, "NO DATA"};
    }

}
