package org.addycaddy.pojo

import org.addycaddy.exception.AddyCaddyException
import spock.lang.Specification
import spock.lang.Unroll

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

    static ContactPoint getBusinessPhone() {
        ContactPoint result = new ContactPoint()

        result.contactPointType = ContactPointType.BusinessPhone
        result.startDate = new LocalDate(1970, 01, 01)
        result.phone = PhoneTest.phone

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

        result.contactPointType = ContactPointType.LocationAddress
        result.startDate = new LocalDate(1970, 01, 01)
        result.address = AddressTest.address2

        return result
    }

    static ContactPoint getLocation2() {
        ContactPoint result = new ContactPoint()

        result.contactPointType = ContactPointType.LocationAddress
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

    static List<ContactPointType> getAddressTypes() {
        List<ContactPointType> result = new ArrayList<>();

        for (ContactPointType cpType : ContactPointType.values()) {
            if (cpType.toString().endsWith("Address")) {
                result.add(cpType);
            }
        }

        return result;
    }

    static List<ContactPointType> getEmailTypes() {
        List<ContactPointType> result = new ArrayList<>();

        for (ContactPointType cpType : ContactPointType.values()) {
            if (cpType.toString().endsWith("Email")) {
                result.add(cpType);
            }
        }

        return result;
    }

    static List<ContactPointType> getPhoneTypes() {
        List<ContactPointType> result = new ArrayList<>();

        for (ContactPointType cpType : ContactPointType.values()) {
            if (cpType.toString().endsWith("Phone")
                || cpType.toString().endsWith("Fax")) {
                result.add(cpType);
            }
        }

        return result;
    }

    void setup() {
        contactPoint = getBillingAddress()
    }

    @Unroll("#cpType should be address")
    def "isAddress addresses"() {
        given: "An address of a certain type"
        contactPoint.setContactPointType(cpType)

        expect: "isAddress() to be true"
        contactPoint.isAddress()

        where:
        cpType << getAddressTypes()
    }

    @Unroll("#cpType should not be an address")
    def "isAddress phones and email"() {
        given: "An address of a certain type"
        contactPoint.setContactPointType(cpType)

        expect: "isAddress() to be false"
        !contactPoint.isAddress()

        where:
        cpType << getPhoneTypes() + getEmailTypes()
    }

    @Unroll("#cpType should be an email")
    def "isEmail emails"() {
        given: "An email of a certain type"
        contactPoint.setContactPointType(cpType)

        expect: "isEmail() to be true"
        contactPoint.isEmail()

        where:
        cpType << getEmailTypes()
    }

    @Unroll("#cpType should not be an email")
    def "isEmail phones and addresses"() {
        given: "A contact point of a certain type"
        contactPoint.setContactPointType(cpType)

        expect: "isEmail to be false"
        !contactPoint.isEmail()

        where:
        cpType << getPhoneTypes() + getAddressTypes()
    }

    @Unroll("#cpType should be a phone")
    def "isPhone phones"() {
        given: "An phone of a certain type"
        contactPoint.setContactPointType(cpType)

        expect: "isPhone() to be true"
        contactPoint.isPhone()

        where:
        cpType << getPhoneTypes()
    }

    @Unroll("#cpType should not be a phone")
    def "isPhone emails and addresses"() {
        given: "A contact point of a certain type"
        contactPoint.setContactPointType(cpType)

        expect: "isPhone to be false"
        !contactPoint.isPhone()

        where:
        cpType << getEmailTypes() + getAddressTypes()
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
        ContactPoint current = ContactPoint.findCurrentContactPoint(set, ContactPointType.LocationAddress)
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
        ContactPoint current = ContactPoint.findCurrentContactPoint(set, ContactPointType.LocationAddress)
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
