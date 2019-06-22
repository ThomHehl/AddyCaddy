package org.addycaddy.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.addycaddy.client.dto.AddyCaddyConstants
import org.addycaddy.client.dto.ContactPointDto
import org.addycaddy.exception.AddyCaddyException
import org.addycaddy.service.ContactPointService
import spock.lang.Specification

class ContactPointControllerTest extends Specification {
    ContactPointController              controller
    ContactPointService                 contactPointService
    ObjectMapper                        objectMapper = new ObjectMapper()

    ContactPointController getContactPointController() {
        ContactPointController result = new ContactPointController()

        result.contactPointService = contactPointService

        return result
    }

    def setup() {
        contactPointService = Mock(ContactPointService.class)

        controller = getContactPointController()
        controller.objectMapper = objectMapper
    }

    def "Create exception"() {
        given: "A contact point"
        ContactPointDto dto = new ContactPointDto()
        AddyCaddyException ace = new AddyCaddyException("Danger, Will Robinson!")
        String dtoJson = objectMapper.writeValueAsString(dto)

        when: "Creating the contact point"
        String result = controller.create(dtoJson)

        then: "Should be successful"
        1 * contactPointService.create(_) >> {throw ace}

        result.equals(AddyCaddyConstants.RESPONSE_FAILURE)
    }

    def "Create normal"() {
        given: "A contact point"
        ContactPointDto dto = new ContactPointDto()
        String dtoJson = objectMapper.writeValueAsString(dto)

        when: "Creating the contact point"
        String result = controller.create(dtoJson)

        then: "Should be successful"
        1 * contactPointService.create(_)

        result.equals(AddyCaddyConstants.RESPONSE_SUCCESS)
    }

    def "Create normal array"() {
        given: "A contact point"
        ContactPointDto dto = new ContactPointDto()
        String dtoJson = objectMapper.writeValueAsString([dto])

        when: "Creating the contact point"
        String result = controller.createMany(dtoJson)

        then: "Should be successful"
        1 * contactPointService.create(_)

        result.equals(AddyCaddyConstants.RESPONSE_SUCCESS)
    }

    def "Update error"() {
        given: "A contact point"
        ContactPointDto dto = new ContactPointDto()
        String dtoJson = objectMapper.writeValueAsString(dto)

        when: "Creating the contact point"
        String result = controller.update(dtoJson)

        then: "Should fail with no address ID"
        0 * contactPointService.update(_)

        result.equals(AddyCaddyConstants.RESPONSE_FAILURE)
    }

    def "Update exception"() {
        given: "A contact point"
        ContactPointDto dto = new ContactPointDto()
        dto.addressId = "Who dey?"
        AddyCaddyException ace = new AddyCaddyException("Danger, Will Robinson!")
        String dtoJson = objectMapper.writeValueAsString(dto)

        when: "Creating the contact point"
        String result = controller.update(dtoJson)

        then: "Should be successful"
        1 * contactPointService.update(_) >> {throw ace}

        result.equals(AddyCaddyConstants.RESPONSE_FAILURE)
    }

    def "Update normal"() {
        given: "A contact point"
        ContactPointDto dto = new ContactPointDto()
        dto.addressId = "Who dey?"
        String dtoJson = objectMapper.writeValueAsString(dto)

        when: "Updating the contact point"
        String result = controller.update(dtoJson)

        then: "Should be successful"
        1 * contactPointService.update(_)

        result.equals(AddyCaddyConstants.RESPONSE_SUCCESS)
    }

    def "Update normal array"() {
        given: "A contact point"
        ContactPointDto dto = new ContactPointDto()
        dto.addressId = "Who dey?"
        String dtoJson = objectMapper.writeValueAsString([dto])

        when: "Updating the contact points"
        String result = controller.updateMany(dtoJson)

        then: "Should be successful"
        1 * contactPointService.update(_)

        result.equals(AddyCaddyConstants.RESPONSE_SUCCESS)
    }
}
