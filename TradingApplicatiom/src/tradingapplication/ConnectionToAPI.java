package tradingapplication;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;

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
            log.addToLog("Exception caught: "+ex);
        } catch (UnknownHostException uhe) {
            System.out.println("No connection!");
            CustomLogger log = new CustomLogger();
            log.addToLog("Exception caught: "+uhe);
        } catch (ProtocolException ex) {
            CustomLogger log = new CustomLogger();
            log.addToLog("Exception caught: "+ex);
        } catch (IOException ex) {
            CustomLogger log = new CustomLogger();
            log.addToLog("Exception caught: "+ex);
        }
        return null;
    }
    
}
