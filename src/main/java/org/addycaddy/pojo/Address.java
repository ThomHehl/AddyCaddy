package org.addycaddy.pojo;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Address {
    public static final String          COUNTRY_CODE_US = "US";

    @Id
    @GeneratedValue
    private Long                        id;

    @Column()
    private String                      attention;

    @Column(nullable = false)
    private String                      name;

    @Column(nullable = false)
    private String                      street1;

    @Column()
    private String                      street2;

    @Column(nullable = false)
    private String                      city;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CountryCode                 countryCode = CountryCode.US;

    @Column(nullable = false)
    private String                      state;

    @Column(nullable = false)
    private String                      postalCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(CountryCode country) {
        this.countryCode = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(attention, address.attention) &&
                name.equals(address.name) &&
                street1.equals(address.street1) &&
                Objects.equals(street2, address.street2) &&
                city.equals(address.city) &&
                countryCode.equals(address.countryCode) &&
                state.equals(address.state) &&
                postalCode.equals(address.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attention, name, street1, street2, city, countryCode, state, postalCode);
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", attention='" + attention + '\'' +
                ", name='" + name + '\'' +
                ", street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", country='" + countryCode + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                '}';
    }
}
