package org.addycaddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.addycaddy.client.dto.AddyCaddyConstants;
import org.addycaddy.client.dto.ContactPointDto;
import org.addycaddy.service.ContactPointService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
public class ContactPointController implements ErrorController {
    private static final Logger         log = LoggerFactory.getLogger(ContactPointController.class);

    @Autowired
    private ContactPointService         contactPointService;

    @Autowired
    private ObjectMapper                objectMapper;

    @RequestMapping(value = AddyCaddyConstants.PATH_CREATE, method = { RequestMethod.POST })
    @ResponseBody
    public String create(@RequestParam(AddyCaddyConstants.KEY_CONTACT_POINT) String contactPointJson) {
        ContactPointDto contactPointDto = null;
        try {
            contactPointDto = objectMapper.readValue(contactPointJson, ContactPointDto.class);
        } catch (IOException ioe) {
            throw new RuntimeException("Error parsing JSON:" + contactPointJson, ioe);
        }

        String result;

        try {
            contactPointService.create(contactPointDto);
            result = AddyCaddyConstants.RESPONSE_SUCCESS;
        } catch (Throwable tw) {
            log.error("Error creating contact point:" + contactPointDto, tw);
            result = AddyCaddyConstants.RESPONSE_FAILURE;
        }

        return result;
    }

    @RequestMapping(value = AddyCaddyConstants.PATH_FIND_BY_CUST_ID, method = { RequestMethod.GET })
    @ResponseBody
    public List<ContactPointDto> findByCustomerId(@RequestParam(AddyCaddyConstants.KEY_CUSTOMER_ID) String customerId) {
        List<ContactPointDto> result;

        if (StringUtils.isEmpty(customerId)) {
            result = new ArrayList<>();
        }
        else {
            try {
                result = contactPointService.findByCustomerId(customerId);
            } catch (Throwable tw) {
                log.error("Error finding contact points for:" + customerId, tw);
                result = new ArrayList<>();
            }
        }

        return result;
    }

    @RequestMapping(value = AddyCaddyConstants.PATH_SEARCH, method = { RequestMethod.GET })
    @ResponseBody
    public List<ContactPointDto> search(@RequestParam(AddyCaddyConstants.KEY_SEARCH_BY) String searchBy,
                                        @RequestParam(AddyCaddyConstants.KEY_SEARCH_KEY) String searchKey) {
        List<ContactPointDto> result;

        switch (searchBy) {
            case AddyCaddyConstants.SEARCH_BY_EMAIL:
                result = contactPointService.findByEmail(searchKey);
                break;

            case AddyCaddyConstants.SEARCH_BY_PHONE:
                result = contactPointService.findByPhone(searchKey);
                break;

            case AddyCaddyConstants.SEARCH_BY_POSTAL_CODE:
                result = contactPointService.findByPostalCode(searchKey);
                break;

            default:
                result = new ArrayList<>();
        }

        return result;
    }

    @RequestMapping(value = AddyCaddyConstants.PATH_UPDATE, method = { RequestMethod.POST })
    @ResponseBody
    public String update(@RequestParam(AddyCaddyConstants.KEY_CONTACT_POINT) String contactPointJson) {
        ContactPointDto contactPointDto = null;
        try {
            contactPointDto = objectMapper.readValue(contactPointJson, ContactPointDto.class);
        } catch (IOException ioe) {
            String msg = "Error parsing JSON:" + contactPointJson;
            throw new RuntimeException(msg, ioe);
        }

        String result;

        if (StringUtils.isEmpty(contactPointDto.getAddressId())) {
            result = AddyCaddyConstants.RESPONSE_FAILURE;
        }
        else {
            try {
                contactPointService.update(contactPointDto);
                result = AddyCaddyConstants.RESPONSE_SUCCESS;
            } catch (Throwable tw) {
                log.error("Error updating contact point:" + contactPointDto, tw);
                result = AddyCaddyConstants.RESPONSE_FAILURE;
            }
        }

        return result;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @PostConstruct
    public void postInit() {
    }
}
