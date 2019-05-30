package org.addycaddy.controller

import org.addycaddy.client.dto.ContactPointDto
import org.addycaddy.exception.AddyCaddyException
import org.addycaddy.service.ContactPointService
import spock.lang.Specification

class ContactPointControllerTest extends Specification {
    ContactPointController              controller
    ContactPointService                 contactPointService

    ContactPointController getContactPointController() {
        ContactPointController result = new ContactPointController()

        result.contactPointService = contactPointService

        return result
    }

    def setup() {
        contactPointService = Mock(ContactPointService.class)

        controller = getContactPointController()
    }

    def "Create exception"() {
        given: "A contact point"
        ContactPointDto dto = new ContactPointDto()
        AddyCaddyException ace = new AddyCaddyException("Danger, Will Robinson!")

        when: "Creating the contact point"
        String result = controller.create(dto)

        then: "Should be successful"
        1 * contactPointService.create(dto) >> {throw ace}

        result.equals(ContactPointController.FAILURE)
    }

    def "Create normal"() {
        given: "A contact point"
        ContactPointDto dto = new ContactPointDto()

        when: "Creating the contact point"
        String result = controller.create(dto)

        then: "Should be successful"
        1 * contactPointService.create(dto)

        result.equals(ContactPointController.SUCCESS)
    }
}
