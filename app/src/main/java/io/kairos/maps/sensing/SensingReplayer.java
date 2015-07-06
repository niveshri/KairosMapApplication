package io.kairos.maps.sensing;

import android.content.res.AssetManager;
import android.util.Log;
import android.os.Handler;

import com.google.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import io.kairos.maps.KairosApplication;
import io.kairos.maps.LocationMocker;
import io.kairos.maps.context.HttpCarStatePredictor;
import io.kairos.maps.perf.PerformanceCounter;

/**
 * Mocks actual sensor readings in the system by replaying offline collected sensor and location
 * data.
 *
 * Read data file from assets. Start a background thread to constantly read the file and emit
 * readings every second.
 */
public class SensingReplayer {

    private String TAG = SensingReplayer.class.toString();

    SensingListener sensingListener;
    private static String filePath = "data/d5_withHeaderAndTimeAndGps.csv";
    private static boolean fileContainsHeader = true;
    private static boolean fileContainsLabel = true;
    private static int labelColumnIndex = 0; // negative if non-existing
    private static String labelColumnName;
    private static SensingReplayer instance;
    private static final AtomicBoolean replayFileAdInfinitum = new AtomicBoolean(true);


    private SensingReplayer(){};

    public static SensingReplayer instance() {
        if (instance == null) {
            instance = new SensingReplayer();
        }

        return instance;
    }




    public void start() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                do{
                    replayFile();
                }while(replayFileAdInfinitum.get());
            }
        });

    }



    private void replayFile(){

        //mocking intialization
        boolean sensingReplayerIsMocking = false;
        LocationMocker locationMocker = new LocationMocker();

        //check for connection
        while(!locationMocker.isConnected()){
            Log.i(TAG, "Error: LocationMocker is not connected.");
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //check for other mocking processes
        while(locationMocker.getMockingStatus()){
            Log.i(TAG, "Error: LocationMocker is already mocking.");
            try {
                Thread.currentThread().sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //initialize mocking
        while(!locationMocker.acquireLock(this, 5))
            ;;
        locationMocker.setMocking(true);
        sensingReplayerIsMocking = true;
        Log.i(TAG, "Mocking has started.");


        //file reading & streaming
        BufferedReader br = null;

        try {

            String[] columnNames = null;
            LinkedHashMap<String,Double> currentEntry = new LinkedHashMap<String,Double>();
            LinkedHashMap<String,Double> futureEntry;
            LatLng currentLatLng = null;
            LatLng futureLatLng;

            //open file
            AssetManager am = KairosApplication.getAppContext().getAssets();
            InputStream is = am.open(filePath);
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String currentLine;

            //read headers (first line)
            if(fileContainsHeader){
                if((currentLine = br.readLine()) != null) {
                    columnNames = currentLine.split(",");
                }
                else{
                    Log.i(TAG, "Error: SensingReplayer is reading empty file.");
                }
            }

            //read first entry
            if((currentLine = br.readLine()) != null) {
                if(!fileContainsHeader){
                    columnNames = createNumericColumnNames(currentLine.split(",").length);
                }
                currentEntry = getEntry(columnNames, currentLine);
            }
            else{
                Log.i(TAG, "Error: SensingReplayer is reading file with no entries.");
                return;
            }

            //set label column name
            labelColumnName = columnNames[labelColumnIndex];
            if(sensingListener instanceof HttpCarStatePredictor){
                ((HttpCarStatePredictor) sensingListener).setValuesContainLabel(fileContainsLabel);
                ((HttpCarStatePredictor) sensingListener).setLabelColumnName(labelColumnName);
            }

            //read remaining entries
            while ((currentLine = br.readLine()) != null) {

                //time to sleep until next entry
                long t0 = System.nanoTime();
                futureEntry = getEntry(columnNames, currentLine);
                long deltaT = (System.nanoTime() - t0) / 1000000L;
                long timeToSleepMs = futureEntry.get("time").longValue() - currentEntry.get("time").longValue() - deltaT;

                timeToSleepMs = Math.max(0,timeToSleepMs);

                //send entry to listener
                if(sensingListener!= null){
                    sensingListener.onSense(currentEntry);
                }

                //mock location
                if(sensingReplayerIsMocking && locationMocker.hasLock(this)) {
                    currentLatLng = getLatLng(currentEntry);
                    futureLatLng = getLatLng(futureEntry);
                    if(currentLatLng!=null && futureLatLng!=null){
                        locationMocker.mockLocation(currentLatLng,futureLatLng);

//                        System.out.println("NEXT LOCATION: "+futureLatLng.toString());
                    }
                }

                //sleep
                try {
                    Thread.currentThread().sleep(timeToSleepMs);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

                currentEntry = futureEntry;
            }

            //send last entry
            if(sensingListener!= null){
                sensingListener.onSense(currentEntry);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

//            if(sensingReplayerIsMocking){
//                locationMocker.setMocking(false);
//            }

            // Not thread safe. State can change between check and release. This
            // is just for a temporary fix.
//            while (!locationMocker.hasLock(this))
//                ;;
//            locationMocker.releaseLock(this);
        }


    }



    /**
     * Gets list of column names (in the right order) and the current line of the text file
     * and generates an entry in the format of a LinkedHashMap<String,Double>
     * @param columnNames - string array of the table's column names
     * @param currentLine - string corresponding to one line of the text file
     * @return LinkedHashMap<String,Double>
     */
    private LinkedHashMap<String,Double> getEntry(String[] columnNames, String currentLine){

        String[] values = currentLine.split(",");

        //check if entry has as many elements as number of columns
        if(values.length != columnNames.length){
            Log.i(TAG, "Error: File " + filePath + ": expected " + columnNames.length + " entries, found " + values.length);
        }

        //generate hashmap with entry values
        LinkedHashMap<String,Double> entry = new LinkedHashMap<String,Double>();

        for(int i=0; i< values.length; i++){
            Double value;
            try{
                value = Double.parseDouble(values[i]);
            }
            catch(NumberFormatException e){
                value = null;
            }
            entry.put(columnNames[i], value);
        }

        return entry;
    }

    private String[] createNumericColumnNames(int n){
        String[] columnNames = new String[n];
        for(int i=0; i<n; i++){
            columnNames[i] = Integer.toString(i);
        }
        return columnNames;
    }

    private LatLng getLatLng(LinkedHashMap<String,Double> entry){

        Double lat = entry.get("lat");//kff1006
        Double lng = entry.get("lng");//kff1005

        if(lat==null || lng==null){
            Log.i(TAG, "Error: entry does not contain latitude and longitude information.");
            return null;
        }

        return new LatLng(lat, lng);

    }

    public void requestSensingListener(SensingListener sl){
        this.sensingListener = sl;
    }


    public static void setFilePath(String fp){
        filePath = fp;
    }

    public static void setReplayFileAdInfinitum(boolean b){ replayFileAdInfinitum.set(b);}
}
