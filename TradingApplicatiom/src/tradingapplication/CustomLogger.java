package tradingapplication;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author Zarkovic
 */
public class CustomLogger {
    
    static StringBuilder sb = new StringBuilder();
    
    public void addToLog(String parameter) throws FileNotFoundException {
        sb.append(parameter).append(System.getProperty("line.separator"));
    }
    
    public void writeLogToDisk (){
    try {
            String desktopPath = javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory().toString();
            FileOutputStream out = new FileOutputStream(desktopPath+"\\loggg.txt");
            OutputStreamWriter w = new OutputStreamWriter(out, "UTF-8");
            w.append(sb);
            w.flush();
            w.close();
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Cought" + ex);
        } catch (IOException ex) {
            System.out.println("Cought" + ex);
        }
        
    }
}
