package org.addycaddy.pojo

import org.addycaddy.exception.AddyCaddyException
import spock.lang.Specification

import java.time.LocalDate

class ContactPointTest extends Specification {
    private ContactPoint                contactPoint;

    static ContactPoint getBillingAddress() {
        ContactPoint result = new ContactPoint()

        result.contactPointType = ContactPointType.BillingAddress
        result.startDate = new LocalDate(1970, 01, 01)
        result.address = AddressTest.address

        return result
    }

    static ContactPoint getHomePhone() {
        ContactPoint result = new ContactPoint()

        result.contactPointType = ContactPointType.HomePhone
        result.startDate = new LocalDate(1970, 01, 01)
        result.phone = PhoneTest.phone

        return result
    }

    static ContactPoint getLocation() {
        ContactPoint result = new ContactPoint()

        result.contactPointType = ContactPointType.Location
        result.startDate = new LocalDate(1970, 01, 01)
        result.address = AddressTest.address2

        return result
    }

    static ContactPoint getLocation2() {
        ContactPoint result = new ContactPoint()

        result.contactPointType = ContactPointType.Location
        result.startDate = new LocalDate(1970, 01, 01)
        result.address = AddressTest.address

        return result
    }

    static ContactPoint getWorkEmail() {
        ContactPoint result = new ContactPoint()

        result.contactPointType = ContactPointType.BillingEmail
        result.startDate = new LocalDate(1970, 01, 01)
        result.emailAddress = EmailAddressTest.emailAddress

        return result
    }

    void setup() {
        contactPoint = getBillingAddress()
    }

    def "isAddress BillingAddress"() {
        when: "When testing for address"
        boolean result = contactPoint.isAddress()

        then: "Should be true"
        result
    }

    def "isAddress HomePhone"() {
        given: "A home phone number"
        ContactPoint homePhone = getHomePhone()

        when: "When testing for address"
        boolean result = homePhone.isAddress()

        then: "Should be false"
        !result
    }

    def "isAddress Location"() {
        given: "A location"
        ContactPoint location = getLocation()

        when: "When testing for address"
        boolean result = location.isAddress()

        then: "Should be true"
        result
    }

    def "isAddress WorkEmail"() {
        given: "A WorkEmail"
        ContactPoint workEmail = getWorkEmail()

        when: "When testing for address"
        boolean result = workEmail.isAddress()

        then: "Should be false"
        !result
    }

    def "isEmail BillingAddress"() {
        when: "When testing for email"
        boolean result = contactPoint.isEmail()

        then: "Should be false"
        !result
    }

    def "isEmail HomePhone"() {
        given: "A home phone number"
        ContactPoint homePhone = getHomePhone()

        when: "When testing for email"
        boolean result = homePhone.isEmail()

        then: "Should be false"
        !result
    }

    def "isEmail Location"() {
        given: "A location"
        ContactPoint location = getLocation()

        when: "When testing for email"
        boolean result = location.isEmail()

        then: "Should be false"
        !result
    }

    def "isEmail WorkEmail"() {
        given: "A WorkEmail"
        ContactPoint workEmail = getWorkEmail()

        when: "When testing for email"
        boolean result = workEmail.isEmail()

        then: "Should be true"
        result
    }

    def "isPhone BillingAddress"() {
        when: "When testing for phone"
        boolean result = contactPoint.isPhone()

        then: "Should be false"
        !result
    }

    def "isPhone HomePhone"() {
        given: "A home phone number"
        ContactPoint homePhone = getHomePhone()

        when: "When testing for phone"
        boolean result = homePhone.isPhone()

        then: "Should be true"
        result
    }

    def "isPhone Location"() {
        given: "A location"
        ContactPoint location = getLocation()

        when: "When testing for phone"
        boolean result = location.isPhone()

        then: "Should be false"
        !result
    }

    def "isPhone WorkEmail"() {
        given: "A WorkEmail"
        ContactPoint workEmail = getWorkEmail()

        when: "When testing for phone"
        boolean result = workEmail.isPhone()

        then: "Should be false"
        !result
    }

    def "Equals same object"() {
        when: "Comparing the same object"
        boolean result = contactPoint.equals(contactPoint)

        then: "Should be equal"
        result
    }

    def "Equals similar object"() {
        given: "A similar object"
        ContactPoint cp2 = getBillingAddress()
        cp2.startDate = contactPoint.startDate

        when: "Comparing them"
        boolean result1 = contactPoint.equals(cp2)
        boolean result2 = cp2.equals(contactPoint)

        then: "Should be equal"
        result1
        result2
    }

    def "Equals different object"() {
        given: "A different object"
        ContactPoint cp2 = getLocation()

        when: "Comparing them"
        boolean result1 = contactPoint.equals(cp2)
        boolean result2 = cp2.equals(contactPoint)

        then: "Should not be equal"
        !result1
        !result2
    }

    def "Setting address with address"() {
        given: "Another adddress"
        Address addr2 = AddressTest.address2

        when: "Setting a new address"
        contactPoint.address = addr2

        then: "Should be the new address"
        contactPoint.address.equals(addr2)
    }

    def "Setting email with address"() {
        given: "An email"
        EmailAddress email = EmailAddressTest.emailAddress

        when: "Setting the email with an address"
        contactPoint.emailAddress = email

        then: "Should throw exception"
        thrown AddyCaddyException
    }

    def "Setting phone number with address"() {
        given: "A phone number"
        Phone phone = PhoneTest.phone

        when: "Setting the phone number with an address"
        contactPoint.phone = phone

        then: "Should throw exception"
        thrown AddyCaddyException
    }

    def "Setting address with email"() {
        given: "Another address"
        contactPoint = getWorkEmail()
        Address addr2 = AddressTest.address2

        when: "Setting a new address"
        contactPoint.address = addr2

        then: "Should throw exception"
        thrown AddyCaddyException
    }

    def "Setting email with email"() {
        given: "An email"
        contactPoint = getWorkEmail()
        EmailAddress email = EmailAddressTest.emailAddress

        when: "Setting the email with an email"
        contactPoint.emailAddress = email

        then: "Should be new email"
        contactPoint.emailAddress == email
    }

    def "Setting phone number with email"() {
        contactPoint = getWorkEmail()
        given: "A phone number"
        Phone phone = PhoneTest.phone

        when: "Setting the phone number with an email"
        contactPoint.phone = phone

        then: "Should throw exception"
        thrown AddyCaddyException
    }

    def "Setting address with phone"() {
        given: "Another address"
        contactPoint = getHomePhone()
        Address addr2 = AddressTest.address2

        when: "Setting a new address"
        contactPoint.address = addr2

        then: "Should throw exception"
        thrown AddyCaddyException
    }

    def "Setting email with phone"() {
        given: "An email"
        contactPoint = getHomePhone()
        EmailAddress email = EmailAddressTest.emailAddress

        when: "Setting the email with an email"
        contactPoint.emailAddress = email

        then: "Should throw exception"
        thrown AddyCaddyException
    }

    def "Setting phone number with phone"() {
        given: "A phone number"
        contactPoint = getHomePhone()
        Phone phone = PhoneTest.phone

        when: "Setting the phone number with an email"
        contactPoint.phone = phone

        then: "Should be added"
        contactPoint.phone == phone
    }

    def "adding first location"() {
        given: "A location"
        contactPoint = getLocation()
        Set<ContactPoint> set = new HashSet<>()

        when: "Adding to an empty set"
        ContactPoint.addContactPoint(set, contactPoint)

        then: "Should hold one location"
        ContactPoint current = ContactPoint.findCurrentContactPoint(set, ContactPointType.Location)
        contactPoint.equals(current)
    }

    def "adding second location"() {
        given: "A location"
        contactPoint = getLocation()

        and: "a set with a location"
        Set<ContactPoint> set = new HashSet<>()
        ContactPoint cp2 = getLocation2()
        set.add(cp2);

        when: "Adding to a set with a location"
        ContactPoint.addContactPoint(set, contactPoint)

        then: "Should hold the last location"
        ContactPoint current = ContactPoint.findCurrentContactPoint(set, ContactPointType.Location)
        contactPoint.equals(current)
        !cp2.isInPlay()
    }

    def "isInPlay"() {
        given: "A current contact point"
        contactPoint = getBillingAddress()

        when: "Seeing if still current"
        boolean result = contactPoint.isInPlay()

        then: "Should be true"
        result
    }

    def "is not InPlay"() {
        given: "An expired contact point"
        contactPoint = getBillingAddress()
        contactPoint.endNow()

        when: "Seeing if still current"
        boolean result = contactPoint.isInPlay()

        then: "Should be false"
        !result
    }
}
