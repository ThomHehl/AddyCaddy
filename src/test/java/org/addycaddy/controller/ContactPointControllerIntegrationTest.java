package org.addycaddy.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.addycaddy.client.dto.AddyCaddyConstants;
import org.addycaddy.client.dto.ContactPointDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContactPointControllerIntegrationTest {
    public static final String          COUNTRY_CODE_US = "US";

    @Autowired
    private ContactPointController      controller;

    private String                      customerId;
    private ContactPointDto             dto1;
    private ContactPointDto             dto2;
    private ContactPointDto             dto3;
    private String                      dto1Json;
    private String                      dto2Json;
    private String                      dto3Json;
    private ObjectMapper                objectMapper = new ObjectMapper();

    public static ContactPointDto getBillingEmail() {
        ContactPointDto result = new ContactPointDto();

        result.setContactPointType(ContactPointDto.TYPE_BILLING_EMAIL);
        result.setEmail("zbeeblebrox@galaxy.gov");

        return result;
    }


    public static ContactPointDto getBillingPhone() {
        ContactPointDto result = new ContactPointDto();

        result.setContactPointType(ContactPointDto.TYPE_BILLING_PHONE);
        result.setPhoneNumber("6145551212");
        result.setCountryCode(COUNTRY_CODE_US);

        return result;
    }

    public static ContactPointDto getLocation() {
        ContactPointDto result = new ContactPointDto();

        result.setCountryCode(COUNTRY_CODE_US);
        result.setContactPointType(ContactPointDto.TYPE_LOCATION_ADDR);
        result.setAttention("Billing Dept.");
        result.setName("Zaphod Beeblebrox");
        result.setStreet1("100 N. High St.");
        result.setStreet2("Suite 405");
        result.setCity("Columbus");
        result.setState("OH");
        result.setPostalCode("42315");

        return result;
    }

    @Before
    public void setUp() throws JsonProcessingException {
        customerId = "zbeeblebrox";

        //US Postal address
        dto1 = getLocation();
        dto1.setCustomerId(customerId);
        dto1Json = objectMapper.writeValueAsString(dto1);

        //Email address
        dto2 = getBillingEmail();
        dto2.setCustomerId(customerId);
        dto2Json = objectMapper.writeValueAsString(dto2);

        //US Phone Number
        dto3 = getBillingPhone();
        dto3.setCustomerId(customerId);
        dto3Json = objectMapper.writeValueAsString(dto3);
    }

    @Test
    public void testCrud() throws JsonProcessingException {
        //create address
        assertEquals(AddyCaddyConstants.RESPONSE_SUCCESS, controller.create(dto1Json));

        //create email
        assertEquals(AddyCaddyConstants.RESPONSE_SUCCESS, controller.create(dto2Json));

        //create phone
        assertEquals(AddyCaddyConstants.RESPONSE_SUCCESS, controller.create(dto3Json));

        List<ContactPointDto> dtoList = controller.findByCustomerId(customerId);
        assertEquals(3, dtoList.size());

        boolean foundAddr = false;
        boolean foundEmail = false;
        boolean foundPhone = false;
        for(ContactPointDto dto : dtoList) {
            assertEquals(customerId, dto.getCustomerId());
            assertNotNull(dto.getAddressId());
            switch (dto.getContactPointType()) {
                case ContactPointDto.TYPE_LOCATION_ADDR:
                    foundAddr = true;
                    break;

                case ContactPointDto.TYPE_BILLING_EMAIL:
                    foundEmail = true;
                    break;

                case ContactPointDto.TYPE_BILLING_PHONE:
                    foundPhone = true;
                    break;
            }
        }

        assertTrue(foundAddr);
        assertTrue(foundEmail);
        assertTrue(foundPhone);

        //create new phone number to replace the old one
        dto3 = new ContactPointDto();
        dto3.setCustomerId(customerId);
        dto3.setContactPointType(ContactPointDto.TYPE_BILLING_PHONE);
        String num2 = "6142491212";
        dto3.setPhoneNumber(num2);
        dto3.setCountryCode(COUNTRY_CODE_US);

        dto3Json = objectMapper.writeValueAsString(dto3);
        assertEquals(AddyCaddyConstants.RESPONSE_SUCCESS, controller.create(dto3Json));

        dtoList = controller.findByCustomerId(customerId);
        assertEquals(3, dtoList.size());

        foundAddr = false;
        foundEmail = false;
        foundPhone = false;
        String phoneAddrId = null;
        for(ContactPointDto dto : dtoList) {
            assertEquals(customerId, dto.getCustomerId());
            assertNotNull(dto.getAddressId());
            switch (dto.getContactPointType()) {
                case ContactPointDto.TYPE_LOCATION_ADDR:
                    foundAddr = true;
                    break;

                case ContactPointDto.TYPE_BILLING_EMAIL:
                    foundEmail = true;
                    break;

                case ContactPointDto.TYPE_BILLING_PHONE:
                    foundPhone = true;
                    assertEquals(num2, dto.getPhoneNumber());
                    phoneAddrId = dto.getAddressId();
                    dto3 = dto;
                    break;
            }
        }

        assertTrue(foundAddr);
        assertTrue(foundEmail);
        assertTrue(foundPhone);
        assertNotNull(phoneAddrId);

        String num3 = "6142495411";
        dto3.setPhoneNumber(num3);

        dto3Json = objectMapper.writeValueAsString(dto3);
        assertEquals(AddyCaddyConstants.RESPONSE_SUCCESS, controller.update(dto3Json));

        dtoList = controller.findByCustomerId(customerId);
        assertEquals(3, dtoList.size());

        foundAddr = false;
        foundEmail = false;
        foundPhone = false;
        for(ContactPointDto dto : dtoList) {
            assertEquals(customerId, dto.getCustomerId());
            assertNotNull(dto.getAddressId());
            switch (dto.getContactPointType()) {
                case ContactPointDto.TYPE_LOCATION_ADDR:
                    foundAddr = true;
                    break;

                case ContactPointDto.TYPE_BILLING_EMAIL:
                    foundEmail = true;
                    break;

                case ContactPointDto.TYPE_BILLING_PHONE:
                    foundPhone = true;
                    assertEquals(num3, dto.getPhoneNumber());
                    break;
            }
        }

        assertTrue(foundAddr);
        assertTrue(foundEmail);
        assertTrue(foundPhone);
    }

    @Test
    public void testSearch() throws JsonProcessingException {
        //create address
        assertEquals(AddyCaddyConstants.RESPONSE_SUCCESS, controller.create(dto1Json));

        //create email
        assertEquals(AddyCaddyConstants.RESPONSE_SUCCESS, controller.create(dto2Json));

        //create phone
        assertEquals(AddyCaddyConstants.RESPONSE_SUCCESS, controller.create(dto3Json));

        //search by email
        List<ContactPointDto> dtoList = controller.search(AddyCaddyConstants.SEARCH_BY_EMAIL, "Who dey?");
        assertTrue(dtoList.isEmpty());

        dtoList = controller.search(AddyCaddyConstants.SEARCH_BY_EMAIL, dto2.getEmail());
        assertEquals(1, dtoList.size());
        assertEquals(dto2.getEmail(), dtoList.get(0).getEmail());

        //search by phone
        dtoList = controller.search(AddyCaddyConstants.SEARCH_BY_PHONE, "Who dey?");
        assertTrue(dtoList.isEmpty());

        dtoList = controller.search(AddyCaddyConstants.SEARCH_BY_PHONE, dto3.getPhoneNumber());
        assertEquals(1, dtoList.size());
        assertEquals(dto3.getPhoneNumber(), dtoList.get(0).getPhoneNumber());

        //search by postal code
        dtoList = controller.search(AddyCaddyConstants.SEARCH_BY_POSTAL_CODE, "Who dey?");
        assertTrue(dtoList.isEmpty());

        dtoList = controller.search(AddyCaddyConstants.SEARCH_BY_POSTAL_CODE, dto1.getPostalCode());
        assertEquals(1, dtoList.size());
        assertEquals(dto1.getPostalCode(), dtoList.get(0).getPostalCode());
    }
}
