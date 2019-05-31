package org.addycaddy.service

import org.addycaddy.client.dto.ContactPointDto
import org.junit.Ignore
import spock.lang.Specification

@Ignore
class SmartyStreetsAddressValidationServiceTest extends Specification {
    SmartyStreetsAddressValidationService   service

    static ContactPointDto getValidAddress() {
        ContactPointDto result = new ContactPointDto()

        result.setStreet1("3447 Woodspring Dr.")
        result.setCity("Lexington")
        result.setState("KY")
        result.setPostalCode("40515")

        return result
    }

    void setup() {
        service = new SmartyStreetsAddressValidationService()
    }

    def "Validate normal"() {
        given: "A valid address"
        ContactPointDto dto = getValidAddress()

        when: "Validating the address"
        ContactPointDto[] result = service.validate(dto)

        then: "Should return one resulting match";
        1 == result.length
    }
}
