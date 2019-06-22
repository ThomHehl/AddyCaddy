package org.addycaddy.service

import org.addycaddy.client.dto.ContactPointDto
import org.addycaddy.exception.AddyCaddyException
import org.addycaddy.pojo.ContactPoint
import org.addycaddy.pojo.ContactPointTest
import org.addycaddy.pojo.ContactPointType
import org.addycaddy.repository.ContactPointRepository
import org.junit.Assert
import spock.lang.Specification

class ContactPointServiceImplTest extends Specification {
    static final String                 CUSTOMER_ID = "Go, Bucks!"
    ContactPointRepository              contactPointRepository
    ContactPointServiceImpl             service

    void setup() {
        contactPointRepository = Mock(ContactPointRepository.class)

        service = new ContactPointServiceImpl()
        service.contactPointRepository = contactPointRepository
    }

    static ContactPointDto getBillingAddress() {
        ContactPointDto dto = new ContactPointDto()
        dto.contactPointType = ContactPointDto.TYPE_BILLING_ADDR
        dto.customerId = CUSTOMER_ID
        dto.attention = "Billing Department"
        dto.name = "Ford Prefect"
        dto.street1 = "120 S Main St"
        dto.street2 = "Suite 5"
        dto.city = "Columbus"
        dto.state = "OH"
        dto.postalCode = "43215"
        dto.countryCode = "US"
        dto.phoneNumber = "614-555-1212"

        return dto
    }

    def "Create first"() {
        given: "A contact point"
        ContactPointDto dto = getBillingAddress()

        when: "Creating the contact point"
        ContactPointDto result = service.create(dto)

        then: "Should return everything"
        1 * contactPointRepository.findByCustomerId(CUSTOMER_ID) >> []
        1 * contactPointRepository.saveAndFlush(_) >> { ContactPoint cp ->
            cp.id = 42L
            Assert.assertEquals(ContactPointType.BillingAddress, cp.contactPointType)
            Assert.assertNotNull(cp.address)
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
        ContactPointDto dto = getBillingAddress()

        and: "An existing business email"
        ContactPoint businessEmail = ContactPointTest.workEmail
        businessEmail.customerId = CUSTOMER_ID

        when: "Creating the contact point"
        ContactPointDto result = service.create(dto)

        then: "Should be return everything"
        1 * contactPointRepository.findByCustomerId(CUSTOMER_ID) >> [businessEmail]
        1 * contactPointRepository.saveAndFlush(_) >> { ContactPoint cp ->
            cp.id = 42L
            Assert.assertEquals(ContactPointType.BillingAddress, cp.contactPointType)
            Assert.assertNotNull(cp.address)
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
        ContactPointDto dto = getBillingAddress()

        and: "An existing billing address"
        ContactPoint billingAddress = ContactPointTest.billingAddress
        Long oldId = 1776
        billingAddress.setId(oldId)
        billingAddress.customerId = CUSTOMER_ID
        boolean replacedOld = false
        boolean savedNew = false

        when: "Creating the contact point"
        ContactPointDto result = service.create(dto)

        then: "Should return everything"
        1 * contactPointRepository.findByCustomerId(CUSTOMER_ID) >> [billingAddress]
        2 * contactPointRepository.saveAndFlush(_) >> { ContactPoint cp ->
            if (cp.id == oldId) {
                replacedOld = true
                Assert.assertFalse(cp.inPlay)
            }
            else {
                savedNew = true
                cp.id = 42L
                Assert.assertEquals(ContactPointType.BillingAddress, cp.contactPointType)
                Assert.assertNotNull(cp.address)
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

    def "Create array"() {
        given: "A contact point"
        ContactPointDto dto = getBillingAddress()
        ContactPointDto dto2 = getBillingAddress()
        dto2.setContactPointType(ContactPointDto.TYPE_BUSINESS_PHONE)
        ContactPointDto[] dtos = new ContactPointDto[2];
        dtos[0] = dto
        dtos[1] = dto2

        List<ContactPointType> foundTypes = new ArrayList<>()
        foundTypes.add(dto.getContactPointType())
        foundTypes.add(dto2.getContactPointType())

        when: "Creating the contact point"
        ContactPointDto[] result = service.create(dtos)

        then: "Should return everything"
        1 * contactPointRepository.findByCustomerIdIn(_) >> []
        1 * contactPointRepository.saveAll(_) >> { contactPointList ->
            //something screwy here, have to unroll the list
            List<ContactPoint> contactPoints = contactPointList.get(0)
            Assert.assertEquals(contactPoints.toString(), 2, contactPoints.size())

            for (ContactPoint contactPoint : contactPoints) {
                Assert.assertNotNull(contactPoint.externalId)
                Assert.assertTrue(foundTypes.remove(contactPoint.getContactPointType().toString()))
            }
            return contactPoints
        }

        result.length == 2
        foundTypes.isEmpty()
    }

    def "Create array replacing existing"() {
        given: "A contact point"
        ContactPointDto dto = getBillingAddress()
        ContactPointDto dto2 = getBillingAddress()
        dto2.setContactPointType(ContactPointDto.TYPE_BUSINESS_PHONE)
        ContactPointDto[] dtos = new ContactPointDto[2];
        dtos[0] = dto
        dtos[1] = dto2

        List<ContactPointType> foundTypes = new ArrayList<>()
        foundTypes.add(dto.getContactPointType())
        foundTypes.add(dto2.getContactPointType())

        and: "An existing email"
        ContactPoint workEmail = ContactPointTest.businessPhone
        workEmail.setCustomerId(CUSTOMER_ID)
        final long oldId = 1215
        workEmail.setId(oldId)

        when: "Creating the contact point"
        ContactPointDto[] result = service.create(dtos)

        then: "Should return everything"
        1 * contactPointRepository.findByCustomerIdIn(_) >> [workEmail]
        1 * contactPointRepository.saveAll(_) >> { contactPointList ->
            //something screwy here, have to unroll the list
            List<ContactPoint> contactPoints = contactPointList.get(0)
            Assert.assertEquals(contactPoints.toString(), 3, contactPoints.size())

            for (ContactPoint contactPoint : contactPoints) {
                if (oldId == contactPoint.getId()) {
                    Assert.assertEquals(ContactPointType.BusinessPhone, contactPoint.contactPointType);
                    Assert.assertNotNull(contactPoint.getEndDate())
                    Assert.assertFalse(contactPoint.isInPlay())
                }
                else {
                    Assert.assertTrue(foundTypes.remove(contactPoint.getContactPointType().toString()))
                }
            }
            return contactPoints
        }

        result.length == 2
        foundTypes.isEmpty()
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
        ContactPointDto dto = getBillingAddress()
        final String externalId = "Who dey?"
        dto.addressId = externalId

        and: "An existing billing address"
        ContactPoint billingAddress = ContactPointTest.billingAddress
        Long oldId = 1776
        billingAddress.id = oldId
        billingAddress.customerId = CUSTOMER_ID
        billingAddress.externalId = externalId

        when: "Creating the contact point"
        ContactPointDto result = service.update(dto)

        then: "Should return everything"
        1 * contactPointRepository.findByExternalId(externalId) >> billingAddress
        1 * contactPointRepository.saveAndFlush(billingAddress) >> { ContactPoint cp ->
            cp.id = 42L
            Assert.assertEquals(ContactPointType.BillingAddress, cp.contactPointType)
            Assert.assertNotNull(cp.address)
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
        ContactPointDto dto = getBillingAddress()
        String externalId = "Who dey?"
        dto.addressId = externalId

        and: "An existing billing address"
        ContactPoint billingAddress = ContactPointTest.billingAddress
        Long oldId = 1776
        billingAddress.id = oldId
        billingAddress.customerId = CUSTOMER_ID
        billingAddress.externalId = externalId

        when: "Creating the contact point"
        service.update(dto)

        then: "Should return everything"
        1 * contactPointRepository.findByExternalId(externalId) >> null
        0 * contactPointRepository.saveAndFlush(billingAddress)

        AddyCaddyException ace = thrown()
        ace != null
    }

    def "Update array"() {
        given: "Some contact point dtos"
        ContactPointDto dto = getBillingAddress()
        final String addrId1 = "Who dey?"
        dto.addressId = addrId1

        ContactPointDto dto2 = getBillingAddress()
        dto2.setContactPointType(ContactPointDto.TYPE_BUSINESS_PHONE)
        final String addrId2 = "Don't Panic!"
        dto2.addressId = addrId2

        ContactPointDto[] dtos = new ContactPointDto[2];
        dtos[0] = dto
        dtos[1] = dto2

        List<String> updatedIds = [addrId1, addrId2]

        and: "Some contact points"
        ContactPoint cp1 = ContactPointTest.billingAddress
        cp1.setExternalId(addrId1)

        ContactPoint cp2 = ContactPointTest.businessPhone
        cp2.setExternalId(addrId2)

        when: "Creating the contact point"
        ContactPointDto[] result = service.update(dtos)

        then: "Should return everything"
        1 * contactPointRepository.findByExternalIdIn(_) >> [cp1, cp2]
        1 * contactPointRepository.saveAll(_) >> { contactPointList ->
            //something screwy here, have to unroll the list
            List<ContactPoint> contactPoints = contactPointList.get(0)
            Assert.assertEquals(contactPoints.toString(), 2, contactPoints.size())

            for (ContactPoint contactPoint : contactPoints) {
                Assert.assertTrue(updatedIds.remove(contactPoint.getExternalId()))
            }
            return contactPoints
        }

        result.length == 2
        updatedIds.isEmpty()
    }

    def "Update array replacing existing"() {
        given: "A contact point"
        ContactPointDto dto = getBillingAddress()
        final String addrId1 = "Who dey?"
        dto.addressId = addrId1

        ContactPointDto dto2 = getBillingAddress()
        dto2.setContactPointType(ContactPointDto.TYPE_BUSINESS_PHONE)
        final String addrId2 = "Don't Panic!"
        dto2.addressId = addrId2

        ContactPointDto[] dtos = new ContactPointDto[2];
        dtos[0] = dto
        dtos[1] = dto2

        and: "Some contact points"
        ContactPoint cp1 = ContactPointTest.billingAddress
        cp1.setExternalId(addrId1)
        final long id1 = 31
        cp1.setId(id1)

        ContactPoint cp2 = ContactPointTest.businessPhone
        cp2.setExternalId(addrId2)
        final long id2 = 32
        cp2.setId(id2)

        List<Long> idList = new ArrayList<>()
        idList.add(id1)
        idList.add(id2)

        when: "Creating the contact point"
        ContactPointDto[] result = service.update(dtos)

        then: "Should update everything"
        1 * contactPointRepository.findByExternalIdIn(_) >> [cp1, cp2]
        1 * contactPointRepository.saveAll(_) >> { contactPointList ->
            //something screwy here, have to unroll the list
            List<ContactPoint> contactPoints = contactPointList.get(0)
            Assert.assertEquals(contactPoints.toString(), 2, contactPoints.size())

            for (ContactPoint contactPoint : contactPoints) {
                Assert.assertNotNull(contactPoint.getExternalId())
                Assert.assertTrue(idList.remove(contactPoint.getId()))
            }
            return contactPoints
        }

        result.length == 2
        idList.isEmpty()
    }
}
