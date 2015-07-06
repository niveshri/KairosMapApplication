package io.kairos.maps.context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import io.kairos.maps.utils.HttpWrapper;

/**
 * Created by daniel on 12/7/14.
 */
public class HttpCarStatePredictor implements CarStatePredictor{

    private static boolean valuesContainLabel = true;
    private static String labelColumnName = "Driver State";
//    private static String baseUrl = "http://128.237.168.110:9979/maori-server/kairos/predict?inputFeatures=";
    private static String baseUrl = "http://128.237.200.213:9979/maori-server/kairos/predict?inputFeatures=";
    private static final HashSet<String> columnsToIgnore = new HashSet<String>();

    static{
        boolean b = columnsToIgnore.addAll(Arrays.asList(new String[]{labelColumnName,"time", "lat", "lng"}));
    }

    @Override
    public CarState predict(LinkedHashMap<String, Double> values) {

        //get features as CSV row
        StringBuilder featureBuilder = new StringBuilder();
        for (Map.Entry<String,Double> entry : values.entrySet()) {

            if(columnsToIgnore.contains(entry.getKey())){
                continue;
            }

            if(featureBuilder.length()>0){
                featureBuilder.append(",");
            }
            featureBuilder.append(Double.toString(entry.getValue()));
        }

        //request prediction
        String prediction = HttpWrapper.fetchResultString(baseUrl + featureBuilder.toString()).trim();
        System.out.println("prediction:\""+prediction+"\"");
        return CarState.valueOf(prediction);
    }







    public static boolean isValuesContainLabel() {
        return valuesContainLabel;
    }

    public static String getLabelIndex() {
        return labelColumnName;
    }

    public static void setValuesContainLabel(boolean valuesContainLabel) {
        HttpCarStatePredictor.valuesContainLabel = valuesContainLabel;
    }

    public static void setLabelColumnName(String labelColumnName) {
        HttpCarStatePredictor.labelColumnName = labelColumnName;
    }
}
