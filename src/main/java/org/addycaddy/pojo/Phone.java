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
    private String                      countryCode;

    @Column(nullable = false)
    private String                      phone;

    @Override
    public String toString() {
        return "Phone{" +
                "id=" + id +
                ", countryCode='" + countryCode + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone1 = (Phone) o;
        return countryCode.equals(phone1.countryCode) &&
                phone.equals(phone1.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryCode, phone);
    }
}
