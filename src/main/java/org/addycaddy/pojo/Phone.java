package org.addycaddy.pojo;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Phone {

    @Id
    @GeneratedValue
    private Long                        id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CountryCode                 countryCode;

    @Column(nullable = false)
    private String                      phoneNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(CountryCode countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phone) {
        this.phoneNumber = phone;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone1 = (Phone) o;
        return countryCode.equals(phone1.countryCode) &&
                phoneNumber.equals(phone1.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, phoneNumber);
    }
}
