package org.addycaddy.service;

import java.util.Map;

public class SmartyStreetsResponse {
    public static final String          NAME_CITY = "city_name";
    public static final String          NAME_LATITUDE = "latitude";
    public static final String          NAME_LONGITUDE = "longitude";
    public static final String          NAME_STATE = "state_abbreviation";
    public static final String          NAME_ZIP = "zipcode";
    public static final String          NAME_ZIP_4 = "plus4_code";

    private int input_index;
    private int candidate_index;
    private String delivery_line_1;
    private String delivery_line_2;
    private String last_line;
    private String delivery_point_barcode;
    private Map<String, String> analysis;
    private Map<String, String> components;
    private Map<String, String> metadata;

    public int getInput_index() {
        return input_index;
    }

    public void setInput_index(int input_index) {
        this.input_index = input_index;
    }

    public int getCandidate_index() {
        return candidate_index;
    }

    public void setCandidate_index(int candidate_index) {
        this.candidate_index = candidate_index;
    }

    public String getDelivery_line_1() {
        return delivery_line_1;
    }

    public void setDelivery_line_1(String delivery_line_1) {
        this.delivery_line_1 = delivery_line_1;
    }

    public String getDelivery_line_2() {
        return delivery_line_2;
    }

    public void setDelivery_line_2(String delivery_line_2) {
        this.delivery_line_2 = delivery_line_2;
    }

    public String getLast_line() {
        return last_line;
    }

    public void setLast_line(String last_line) {
        this.last_line = last_line;
    }

    public String getDelivery_point_barcode() {
        return delivery_point_barcode;
    }

    public void setDelivery_point_barcode(String delivery_point_barcode) {
        this.delivery_point_barcode = delivery_point_barcode;
    }

    public Map<String, String> getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Map<String, String> analysis) {
        this.analysis = analysis;
    }

    public Map<String, String> getComponents() {
        return components;
    }

    public void setComponents(Map<String, String> components) {
        this.components = components;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public String getCity() {
        String result = components.get(NAME_CITY);
        return result;
    }

    public String getState() {
        String result = components.get(NAME_STATE);
        return result;
    }

    public String getZip() {
        StringBuilder sb = new StringBuilder();
        sb.append(components.get(NAME_ZIP));
        sb.append(components.get(NAME_ZIP_4));
        return sb.toString();
    }

    public String getLatitude() {
        String result = metadata.get(NAME_LATITUDE);
        return result;
    }

    public String getLongitude() {
        String result = metadata.get(NAME_LONGITUDE);
        return result;
    }
}
