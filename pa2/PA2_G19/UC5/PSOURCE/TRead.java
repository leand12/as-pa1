package UC5.PSOURCE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 * Thread responsible to read sensor records from file
 */
public class TRead extends Thread{

    private final BufferedReader sourcefile;

    /** Monitor */
    private MSource mSource;

    public TRead(String file, MSource mSource) throws IOException{
        this.sourcefile = new BufferedReader(new FileReader(file));
        this.mSource = mSource;
    }

    @Override
    public void run() {
        try {
            String line;
            // read sensor data until EOF
            while ((line = this.sourcefile.readLine()) != null) {
                line = line.concat(":").concat(this.sourcefile.readLine());
                line = line.concat(":").concat(this.sourcefile.readLine());
                mSource.putRecord(line);
            }
            
        } catch (IOException e) {
            //TODO: handle exception
        }
        mSource.setFinished();
    }
}
