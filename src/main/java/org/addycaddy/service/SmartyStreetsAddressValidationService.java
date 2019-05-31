package org.addycaddy.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heavyweightsoftware.exception.HeavyweightException;
import org.addycaddy.client.dto.ContactPointDto;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

@Service
public class SmartyStreetsAddressValidationService implements AddressValidationService {
    public static final String          AUTH_PROPERTIES_FILE = "/smarty-streets/account.properties";
    public static final String          HOST_NAME = "us-street.api.smartystreets.com";
    public static final String          HOST_PATH = "/street-address";
    public static final String          NAME_CITY = "city";
    public static final String          NAME_STATE = "state";
    public static final String          NAME_STREET = "street";
    public static final String          NAME_STREET2 = "street2";
    public static final String          NAME_ZIP = "zipcode";

    private static final Logger         log = LoggerFactory.getLogger(ContactPointServiceImpl.class);

    private Properties                  authProperties;
    private ObjectMapper                objectMapper = new ObjectMapper();

    @Override
    public ContactPointDto[] validate(ContactPointDto contactPointDto) throws HeavyweightException {
        String jsonResponse = readResponse(contactPointDto);
        SmartyStreetsResponse[] responses = parseJson(jsonResponse);
        ContactPointDto[] result = toDto(responses);

        return result;
    }

    private String readResponse(ContactPointDto contactPointDto) throws HeavyweightException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = buildRequest(contactPointDto);

        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException ioe) {
            String msg = "Error processing request " + httpGet.toString();
            log.error(msg, ioe);
            throw new HeavyweightException(msg, ioe, HeavyweightException.INVALID_STATE);
        }

        StringBuilder sb = new StringBuilder();
        try {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null ) {
                    sb.append(line);
                }
            }
            else {
                String msg = "Invalid response:" + response.getStatusLine().getStatusCode() + ", to:" + httpGet.toString();
                log.error(msg);
                throw new HeavyweightException(msg, HeavyweightException.INVALID_STATE);
            }
        } catch (IOException ioe) {
            String msg = "Error reading request " + httpGet.toString();
            log.error(msg, ioe);
            throw new HeavyweightException(msg, ioe, HeavyweightException.INVALID_STATE);
        } finally {
            try {
                response.close();
            } catch (IOException ioe) {
                String msg = "Error closing request " + httpGet.toString();
                log.error(msg, ioe);
                throw new HeavyweightException(msg, ioe, HeavyweightException.INVALID_STATE);
            }
        }

        return sb.toString();
    }

    private SmartyStreetsResponse[] parseJson(String jsonResponse) throws HeavyweightException {
        SmartyStreetsResponse[] result;
        try {
            result = objectMapper.readValue(jsonResponse, SmartyStreetsResponse[].class);
        } catch (IOException ioe) {
            String msg = "Error parsing JSON:" + jsonResponse;
            log.error(msg, ioe);
            throw new HeavyweightException(msg, ioe, HeavyweightException.INVALID_STATE);
        }
        return result;
    }

    private ContactPointDto[] toDto(SmartyStreetsResponse[] responses) {
        ContactPointDto[] result = new ContactPointDto[responses.length];

        for (int ix = 0; ix < responses.length; ++ix) {
            result[ix] = toDto(responses[ix]);
        }

        return result;
    }

    private ContactPointDto toDto(SmartyStreetsResponse response) {
        ContactPointDto result = new ContactPointDto();


        return result;
    }

    private HttpGet buildRequest(ContactPointDto contactPointDto) throws HeavyweightException {
        URIBuilder builder = new URIBuilder()
                .setScheme("https")
                .setHost(HOST_NAME)
                .setPath(HOST_PATH);
        addIdentityParameters(builder);
        addAddressParameters(builder, contactPointDto);

        URI uri;
        try {
            uri = builder.build();
        } catch (URISyntaxException use) {
            String msg = "Error building URI";
            log.error(msg, use);
            throw new HeavyweightException(msg, use, HeavyweightException.INVALID_STATE);
        }
        HttpGet httpget = new HttpGet(uri);

        return httpget;
    }

    private void addAddressParameters(URIBuilder builder, ContactPointDto contactPointDto) {
        builder.setParameter(NAME_STREET, contactPointDto.getStreet1())
                .setParameter(NAME_STREET2, contactPointDto.getStreet2())
                .setParameter(NAME_CITY, contactPointDto.getCity())
                .setParameter(NAME_STATE, contactPointDto.getState())
                .setParameter(NAME_ZIP, contactPointDto.getPostalCode());
    }

    private void addIdentityParameters(URIBuilder builder)
            throws HeavyweightException {
        for (Map.Entry<Object, Object> entry : getAuthProperties().entrySet()) {
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            builder.setParameter(key, value);
        }
    }

    private synchronized Properties getAuthProperties()
            throws HeavyweightException {
        if(authProperties==null) {
            authProperties = new Properties();
            InputStream is = getClass().getResourceAsStream(AUTH_PROPERTIES_FILE);
            try {
                authProperties.load(is);
            } catch (IOException ioe) {
                String msg = "Error reading " + AUTH_PROPERTIES_FILE;
                log.error(msg, ioe);
                throw new HeavyweightException(msg, ioe, HeavyweightException.INVALID_STATE);
            }
        }

        return authProperties;
    }
}
