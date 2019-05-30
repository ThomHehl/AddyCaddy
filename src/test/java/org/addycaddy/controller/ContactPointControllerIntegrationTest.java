package org.addycaddy.controller;

import org.addycaddy.client.dto.ContactPointDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        assertEquals(ContactPointController.SUCCESS, controller.create(dto1));

        List<ContactPointDto> dtoList = controller.findByCustomerId(customerId);
        assertEquals(1, dtoList.size());

        ContactPointDto dto = dtoList.get(0);
        assertEquals(customerId, dto.getCustomerId());
        assertNotNull(dto.getAddressId());
    }

}
