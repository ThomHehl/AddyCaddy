package org.addycaddy.pojo

import spock.lang.Specification

class AddressTest extends Specification {
    private Address address

    static Address getAddress() {
        Address result = new Address()

        result.setAttention("Billing Department")
        result.setName("Heavyweight Software")
        result.setStreet1("3447 Woodspring Dr")
        result.setCity("Lexington")
        result.setState("KY")
        result.setPostalCode("40515")

        return result
    }

    static Address getAddress2() {
        Address result = new Address()

        result.setName("Bob Clarke")
        result.setStreet1("Lucasville Giovanni's")
        result.setStreet2("10689 US 23")
        result.setCity("Lucasville")
        result.setState("OH")
        result.setPostalCode("45648")

        return result
    }

    void setup() {
        address = getAddress()
    }

    def "Equals same object"() {
        when: "Comparing the same object"
        boolean result = address.equals(address)

        then: "Should be equal"
        result
    }

    def "Equals similar object"() {
        given: "A similar object"
        Address addr2 = getAddress()

        when: "Comparing them"
        boolean result1 = address.equals(addr2)
        boolean result2 = addr2.equals(address)

        then: "Should be equal"
        result1
        result2
    }

    def "Equals different object"() {
        given: "A different object"
        Address addr2 = getAddress2()

        when: "Comparing them"
        boolean result1 = address.equals(addr2)
        boolean result2 = addr2.equals(address)

        then: "Should not be equal"
        !result1
        !result2
    }
}
