package org.addycaddy.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.heavyweightsoftware.util.StringHelper
import spock.lang.Specification

class SmartyStreetsResponseTest extends Specification {
    static final String                JSON_FILE_NAME = "/smarty-streets/SmartyStreetsResponse.json"

    ObjectMapper                        mapper = new ObjectMapper()

    void setup() {
    }

    def "Parse JSON"() {
        given: "The JSON response"
        InputStream is = getClass().getResourceAsStream(JSON_FILE_NAME);
        String json = StringHelper.readStream(is)

        when: "Parsing JSON"
        SmartyStreetsResponse[] result = mapper.readValue(json, SmartyStreetsResponse[].class)

        then: "Should return one result";
        1 == result.length

        SmartyStreetsResponse response = result[0]
        "37.96882" == response.getLatitude()
        "-84.4615" == response.getLongitude()
        "Lexington" == response.getCity()
        "KY" == response.getState()
        "405155856" == response.getZip()
    }
}
