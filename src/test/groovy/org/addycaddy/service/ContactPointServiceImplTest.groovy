package org.addycaddy.service

import org.addycaddy.client.dto.ContactPointDto
import org.addycaddy.exception.AddyCaddyException
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

        then: "Should return everything"
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

        and: "An existing billing address"
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

    def "Find by customer ID"() {
        given: "A customer ID and some contact points"
        String customerId = "Don't Panic!"

        List<ContactPoint> contactPointList = new ArrayList<>()

        ContactPoint billingAddress = ContactPointTest.billingAddress
        String billingAddrId = "Who dey?"
        billingAddress.setExternalId(billingAddrId)
        contactPointList.add(billingAddress)

        ContactPoint workEmail = ContactPointTest.workEmail
        String workEmailId = "Go, bucks!"
        workEmail.externalId = workEmailId
        contactPointList.add(workEmail)

        when: "Finding by customer ID"
        List<ContactPointDto> result = service.findByCustomerId(customerId)

        then: "Should return them all"
        1 * contactPointRepository.findByCustomerId(customerId) >> contactPointList
        contactPointList.size() == result.size()

        List<String> foundIds = new ArrayList<>()
        foundIds.add(billingAddrId)
        foundIds.add(workEmailId)

        for (ContactPointDto dto : result) {
            if(foundIds.contains(dto.addressId)) {
                foundIds.remove(dto.addressId)
            }
            else {
                dto.addressId == null
            }
        }
        foundIds.isEmpty()
    }


    def "Find by customer ID with expired"() {
        given: "A customer ID and some contact points"
        String customerId = "Don't Panic!"

        List<ContactPoint> contactPointList = new ArrayList<>()

        ContactPoint billingAddress = ContactPointTest.billingAddress
        String billingAddrId = "Who dey?"
        billingAddress.setExternalId(billingAddrId)
        contactPointList.add(billingAddress)

        ContactPoint workEmail = ContactPointTest.workEmail
        String workEmailId = "Go, Bucks!"
        workEmail.externalId = workEmailId
        contactPointList.add(workEmail)

        ContactPoint expEmail = ContactPointTest.workEmail
        String expEmailId = "Go, Cats!"
        expEmail.externalId = expEmailId
        expEmail.setEndDate()
        contactPointList.add(expEmail)

        when: "Finding by customer ID"
        List<ContactPointDto> result = service.findByCustomerId(customerId)

        then: "Should return them all"
        1 * contactPointRepository.findByCustomerId(customerId) >> contactPointList
        result.size() == 2

        List<String> foundIds = new ArrayList<>()
        foundIds.add(billingAddrId)
        foundIds.add(workEmailId)

        for (ContactPointDto dto : result) {
            if(foundIds.contains(dto.addressId)) {
                foundIds.remove(dto.addressId)
            }
            else {
                dto.addressId == null
            }
        }
        foundIds.isEmpty()
    }

    def "Update billing address"() {
        given: "A billing address"
        ContactPointDto dto = new ContactPointDto()
        dto.contactPointType = ContactPointDto.TYPE_BILLING_ADDR
        String customerId = "Go, Bucks!"
        dto.customerId = customerId
        String externalId = "Who dey?"
        dto.addressId = externalId
        dto.attention = "Billing Department"
        dto.name = "Ford Prefect"
        dto.street1 = "120 S Main St"
        dto.street2 = "Suite 5"
        dto.city = "Columbus"
        dto.state = "OH"
        dto.postalCode = "43215"
        dto.countryCode = "US"
        dto.phoneNumber = "614-555-1212"

        and: "An existing billing address"
        ContactPoint billingAddress = ContactPointTest.billingAddress
        Long oldId = 1776
        billingAddress.id = oldId
        billingAddress.customerId = customerId
        billingAddress.externalId = externalId

        when: "Creating the contact point"
        ContactPointDto result = service.update(dto)

        then: "Should return everything"
        1 * contactPointRepository.findByExternalId(externalId) >> billingAddress
        1 * contactPointRepository.saveAndFlush(billingAddress) >> { ContactPoint cp ->
            cp.id = 42L
            cp.contactPointType == ContactPointType.BillingAddress
            cp.address != null
            return cp
        }

        result.addressId == dto.addressId
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

    def "Update billing address not found"() {
        given: "A billing address"
        ContactPointDto dto = new ContactPointDto()
        dto.contactPointType = ContactPointDto.TYPE_BILLING_ADDR
        String customerId = "Go, Bucks!"
        dto.customerId = customerId
        String externalId = "Who dey?"
        dto.addressId = externalId
        dto.attention = "Billing Department"
        dto.name = "Ford Prefect"
        dto.street1 = "120 S Main St"
        dto.street2 = "Suite 5"
        dto.city = "Columbus"
        dto.state = "OH"
        dto.postalCode = "43215"
        dto.countryCode = "US"
        dto.phoneNumber = "614-555-1212"

        and: "An existing billing address"
        ContactPoint billingAddress = ContactPointTest.billingAddress
        Long oldId = 1776
        billingAddress.id = oldId
        billingAddress.customerId = customerId
        billingAddress.externalId = externalId

        when: "Creating the contact point"
        service.update(dto)

        then: "Should return everything"
        1 * contactPointRepository.findByExternalId(externalId) >> null
        0 * contactPointRepository.saveAndFlush(billingAddress)

        AddyCaddyException ace = thrown()
        ace != null
    }
}
