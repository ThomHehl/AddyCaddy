package org.addycaddy.pojo

import spock.lang.Specification

class EmailAddressTest extends Specification {
    private EmailAddress email

    static EmailAddress getEmailAddress()  {
        EmailAddress result = new EmailAddress()

        result.email = "thom@heavyweightsoftware.com"

        return result
    }

    static EmailAddress getEmailAddress2()  {
        EmailAddress result = new EmailAddress()

        result.email = "thom@forerunnerintl.org"

        return result
    }

    void setup() {
        email = getEmailAddress()
    }

    def "Equals same object"() {
        when: "Comparing the same object"
        boolean result = email.equals(email)

        then: "Should be equal"
        result
    }

    def "Equals similar object"() {
        given: "A similar object"
        EmailAddress email2 = getEmailAddress()

        when: "Comparing them"
        boolean result1 = email.equals(email2)
        boolean result2 = email2.equals(email)

        then: "Should be equal"
        result1
        result2
    }

    def "Equals different object"() {
        given: "A different object"
        EmailAddress email2 = getEmailAddress2()

        when: "Comparing them"
        boolean result1 = email.equals(email2)
        boolean result2 = email2.equals(email)

        then: "Should not be equal"
        !result1
        !result2
    }
}
