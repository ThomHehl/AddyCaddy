package org.addycaddy.service

import org.addycaddy.client.dto.ContactPointDto
import org.addycaddy.pojo.ContactPoint
import org.addycaddy.pojo.ContactPointTest
import org.addycaddy.pojo.ContactPointType
import org.addycaddy.repository.ContactPointRepository
import spock.lang.Specification

class ContactPointServiceImplTest extends Specification {
    ContactPointRepository              contactPointRepository
    ContactPointServiceImpl             service

    void setup() {
        contactPointRepository = Mock(ContactPointRepository.class)

        service = new ContactPointServiceImpl()
        service.contactPointRepository = contactPointRepository
    }

    def "Create first"() {
        given: "A contact point"
        ContactPointDto dto = new ContactPointDto()
        dto.contactPointType = ContactPointDto.TYPE_BILLING_ADDR
        String customerId = "Go, Bucks!"
        dto.customerId = customerId
        dto.attention = "Billing Department"
        dto.name = "Ford Prefect"
        dto.street1 = "120 S Main St"
        dto.street2 = "Suite 5"
        dto.city = "Columbus"
        dto.state = "OH"
        dto.postalCode = "43215"
        dto.countryCode = "US"
        dto.phoneNumber = "614-555-1212"

        when: "Creating the contact point"
        ContactPointDto result = service.create(dto)

        then: "Should be return everything"
        1 * contactPointRepository.findByCustomerId(customerId) >> []
        1 * contactPointRepository.saveAndFlush(_) >> { ContactPoint cp ->
            cp.id = 42L
            cp.contactPointType = ContactPointType.BillingAddress
            cp.address != null
            return cp
        }

        result.contactPointType == dto.contactPointType
        result.customerId == dto.customerId
        result.attention == dto.attention
        result.street1 == dto.street1
        result.street2 == dto.street2
        result.city == dto.city
        result.state == dto.state
        result.postalCode == dto.postalCode
        result.countryCode == dto.countryCode
    }

    def "Create second"() {
        given: "A billing address"
        ContactPointDto dto = new ContactPointDto()
        dto.contactPointType = ContactPointDto.TYPE_BILLING_ADDR
        String customerId = "Go, Bucks!"
        dto.customerId = customerId
        dto.attention = "Billing Department"
        dto.name = "Ford Prefect"
        dto.street1 = "120 S Main St"
        dto.street2 = "Suite 5"
        dto.city = "Columbus"
        dto.state = "OH"
        dto.postalCode = "43215"
        dto.countryCode = "US"
        dto.phoneNumber = "614-555-1212"

        and: "An existing business email"
        ContactPoint businessEmail = ContactPointTest.workEmail
        businessEmail.customerId = customerId

        when: "Creating the contact point"
        ContactPointDto result = service.create(dto)

        then: "Should be return everything"
        1 * contactPointRepository.findByCustomerId(customerId) >> [businessEmail]
        1 * contactPointRepository.saveAndFlush(_) >> { ContactPoint cp ->
            cp.id = 42L
            cp.contactPointType = ContactPointType.BillingAddress
            cp.address != null
            return cp
        }

        result.contactPointType == dto.contactPointType
        result.customerId == dto.customerId
        result.attention == dto.attention
        result.street1 == dto.street1
        result.street2 == dto.street2
        result.city == dto.city
        result.state == dto.state
        result.postalCode == dto.postalCode
        result.countryCode == dto.countryCode
    }

    def "Create replacement billing address"() {
        given: "A billing address"
        ContactPointDto dto = new ContactPointDto()
        dto.contactPointType = ContactPointDto.TYPE_BILLING_ADDR
        String customerId = "Go, Bucks!"
        dto.customerId = customerId
        dto.attention = "Billing Department"
        dto.name = "Ford Prefect"
        dto.street1 = "120 S Main St"
        dto.street2 = "Suite 5"
        dto.city = "Columbus"
        dto.state = "OH"
        dto.postalCode = "43215"
        dto.countryCode = "US"
        dto.phoneNumber = "614-555-1212"

        and: "An existing business email"
        ContactPoint billingAddress = ContactPointTest.billingAddress
        Long oldId = 1776
        billingAddress.setId(oldId)
        billingAddress.customerId = customerId
        boolean replacedOld = false
        boolean savedNew = false

        when: "Creating the contact point"
        ContactPointDto result = service.create(dto)

        then: "Should return everything"
        1 * contactPointRepository.findByCustomerId(customerId) >> [billingAddress]
        2 * contactPointRepository.saveAndFlush(_) >> { ContactPoint cp ->
            if (cp.id == oldId) {
                replacedOld = true
                !cp.inPlay
            }
            else {
                savedNew = true
                cp.id = 42L
                cp.contactPointType == ContactPointType.BillingAddress
                cp.address != null
            }
            return cp
        }

        replacedOld
        savedNew

        result.contactPointType == dto.contactPointType
        result.customerId == dto.customerId
        result.attention == dto.attention
        result.street1 == dto.street1
        result.street2 == dto.street2
        result.city == dto.city
        result.state == dto.state
        result.postalCode == dto.postalCode
        result.countryCode == dto.countryCode
    }
}
