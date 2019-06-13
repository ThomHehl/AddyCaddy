package org.addycaddy.pojo;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(indexes = { @Index(name = "EMAIL_ADDR_EMAIL_IDX", columnList=EmailAddress.COL_EMAIL, unique = false) })
public class EmailAddress {
    public static final String          COL_EMAIL = "email";

    @Id
    @GeneratedValue
    private Long                        id;

    @Column(name = COL_EMAIL, nullable = false)
    private String                      email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "EmailAddress{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailAddress that = (EmailAddress) o;
        return email.equals(that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
