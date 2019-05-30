package org.addycaddy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heavyweightsoftware.util.StringHelper;
import org.addycaddy.client.dto.ContactPointDto;
import org.addycaddy.pojo.ContactPoint;
import org.addycaddy.service.ContactPointService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactPointController implements ErrorController {
    private static final Logger         log = LoggerFactory.getLogger(ContactPointController.class);

    public static final String          FAILURE = "failure";
    public static final String          SUCCESS = "success";

    @Autowired
    private ContactPointService         contactPointService;

    @RequestMapping(value = "/create", method = { RequestMethod.POST })
    @ResponseBody
    public String create(ContactPointDto contactPointDto) {
        String result;

        try {
            contactPointService.create(contactPointDto);
            result = SUCCESS;
        } catch (Throwable tw) {
            log.error("Error creating contact point:" + contactPointDto, tw);
            result = FAILURE;
        }

        return result;
    }

    @RequestMapping(value = "/findByCustomerId", method = { RequestMethod.GET })
    @ResponseBody
    public List<ContactPointDto> findByCustomerId(String customerId) {
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

    @RequestMapping(value = "/update", method = { RequestMethod.POST })
    @ResponseBody
    public String update(ContactPointDto contactPointDto) {
        String result;

        if (StringUtils.isEmpty(contactPointDto.getAddressId())) {
            result = FAILURE;
        }
        else {
            try {
                contactPointService.update(contactPointDto);
                result = SUCCESS;
            } catch (Throwable tw) {
                log.error("Error updating contact point:" + contactPointDto, tw);
                result = FAILURE;
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
