package org.addycaddy.controller;

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

    @Before
    public void setUp() {
        customerId = "zbeeblebrox";

        dto1 = new ContactPointDto();
        dto1.setCustomerId(customerId);
        dto1.setCountryCode(COUNTRY_CODE_US);
        dto1.setContactPointType(ContactPointDto.TYPE_LOCATION);
        dto1.setAttention("Billing Dept.");
        dto1.setName("Zaphod Beeblebrox");
        dto1.setStreet1("100 N. High St.");
        dto1.setStreet2("Suite 405");
        dto1.setCity("Columbus");
        dto1.setState("OH");
        dto1.setPostalCode("42315");

    }

    @Test
    public void testCrud() {
        //create address
        assertEquals(ContactPointController.SUCCESS, controller.create(dto1));

        //create email
        ContactPointDto dto2 = new ContactPointDto();
        dto2.setCustomerId(customerId);
        dto2.setContactPointType(ContactPointDto.TYPE_BILLING_EMAIL);
        dto2.setEmail("zbeeblebrox@galaxy.gov");
        assertEquals(ContactPointController.SUCCESS, controller.create(dto2));

        //create phone
        ContactPointDto dto3 = new ContactPointDto();
        dto3.setCustomerId(customerId);
        dto3.setContactPointType(ContactPointDto.TYPE_BILLING_PHONE);
        dto3.setPhoneNumber("6145551212");
        dto3.setCountryCode(COUNTRY_CODE_US);
        assertEquals(ContactPointController.SUCCESS, controller.create(dto3));

        List<ContactPointDto> dtoList = controller.findByCustomerId(customerId);
        assertEquals(3, dtoList.size());

        boolean foundAddr = false;
        boolean foundEmail = false;
        boolean foundPhone = false;
        for(ContactPointDto dto : dtoList) {
            assertEquals(customerId, dto.getCustomerId());
            assertNotNull(dto.getAddressId());
            switch (dto.getContactPointType()) {
                case ContactPointDto.TYPE_LOCATION:
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
        assertEquals(ContactPointController.SUCCESS, controller.create(dto3));

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
                case ContactPointDto.TYPE_LOCATION:
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
        assertEquals(ContactPointController.SUCCESS, controller.update(dto3));

        dtoList = controller.findByCustomerId(customerId);
        assertEquals(3, dtoList.size());

        foundAddr = false;
        foundEmail = false;
        foundPhone = false;
        for(ContactPointDto dto : dtoList) {
            assertEquals(customerId, dto.getCustomerId());
            assertNotNull(dto.getAddressId());
            switch (dto.getContactPointType()) {
                case ContactPointDto.TYPE_LOCATION:
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
}
