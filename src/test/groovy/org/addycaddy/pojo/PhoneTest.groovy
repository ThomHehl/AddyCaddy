package org.addycaddy.pojo

import spock.lang.Specification

class PhoneTest extends Specification {
    private Phone phone;

    static Phone getPhone() {
        Phone result = new Phone()

        result.countryCode = CountryCode.UnitedStates
        result.phone = "8595551212"

        return result
    }

    static Phone getPhone2() {
        Phone result = new Phone()

        result.countryCode = CountryCode.UnitedStates
        result.phone = "7405551212"

        return result
    }

    void setup() {
        phone = getPhone()
    }

    def "Equals same object"() {
        when: "Comparing the same object"
        boolean result = phone.equals(phone)

        then: "Should be equal"
        result
    }

    def "Equals similar object"() {
        given: "A similar object"
        Phone phone2 = getPhone()

        when: "Comparing them"
        boolean result1 = phone.equals(phone2)
        boolean result2 = phone2.equals(phone)

        then: "Should be equal"
        result1
        result2
    }

    def "Equals different object"() {
        given: "A different object"
        Phone phone2 = getPhone2()

        when: "Comparing them"
        boolean result1 = phone.equals(phone2)
        boolean result2 = phone2.equals(phone)

        then: "Should not be equal"
        !result1
        !result2
    }
}
