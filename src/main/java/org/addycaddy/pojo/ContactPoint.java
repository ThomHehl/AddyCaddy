package org.addycaddy.pojo;

import com.heavyweightsoftware.util.DateHelper;
import org.addycaddy.exception.AddyCaddyException;
import org.addycaddy.exception.DuplicateContactPointException;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Objects;

@Entity
public class ContactPoint {

    public static final LocalDate       DEFAULT_END_DATE = LocalDate.of(2999, 12, 31);

    @Id
    @GeneratedValue
    private Long                        id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContactPointType            contactPointType;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Calendar                    endDate = DateHelper.toCalendar(DEFAULT_END_DATE);

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Calendar                    startDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_address")
    private Address                     address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_email")
    private EmailAddress                emailAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_phone")
    private Phone                       phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContactPointType getContactPointType() {
        return contactPointType;
    }

    public void setContactPointType(ContactPointType contactPointType) {
        this.contactPointType = contactPointType;
    }

    public LocalDate getEndDate() {
        LocalDate result;

        if (endDate == null) {
            result = DEFAULT_END_DATE;
        }
        else {
            result = DateHelper.toLocalDate(endDate);
        }

        return result;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = DateHelper.toCalendar(endDate);
    }

    public LocalDate getStartDate() {
        return DateHelper.toLocalDate(this.startDate);
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = DateHelper.toCalendar(startDate);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) throws AddyCaddyException {
        if (emailAddress == null && phone == null) {
            this.address = address;
        }
        else {
            throw new AddyCaddyException("Cannot set address on a contact that already has email or address");
        }
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(EmailAddress emailAddress) throws AddyCaddyException {
        if (address == null && phone == null) {
            this.emailAddress = emailAddress;
        }
        else {
            throw new AddyCaddyException("Cannot set email address on a contact that already has phone or address");
        }
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) throws AddyCaddyException {
        if (emailAddress == null && address == null) {
            this.phone = phone;
        }
        else {
            throw new AddyCaddyException("Cannot set phone number on a contact that already has email or address");
        }
    }

    @Override
    public String toString() {
        return "ContactPoint{" +
                "id=" + id +
                ", contactPointType=" + contactPointType +
                ", endDate=" + DateHelper.toIso8601(endDate) +
                ", startDate=" + DateHelper.toIso8601(startDate) +
                ", address=" + address +
                ", emailAddress=" + emailAddress +
                ", phone=" + phone +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactPoint that = (ContactPoint) o;
        return contactPointType == that.contactPointType &&
                endDate.equals(that.endDate) &&
                startDate.equals(that.startDate) &&
                Objects.equals(address, that.address) &&
                Objects.equals(emailAddress, that.emailAddress) &&
                Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactPointType, endDate, startDate, address, emailAddress, phone);
    }

    public void end(Calendar expiredDate) {
        endDate = expiredDate;
    }

    public void endNow() {
        GregorianCalendar now = new GregorianCalendar();
        now.add(Calendar.SECOND, -1);
        end(now);
    }

    /**
     * Is this contact point still in force?
     * @return true if current
     */
    public boolean isInPlay() {
        GregorianCalendar now = new GregorianCalendar();
        boolean result = !startDate.after(now) && !endDate.before(now);
        return result;
    }

    /**
     * Add a contact point to the collection
     * @param contactPoints the current contact points
     * @param newContactPoint the contact point to be added
     */
    public static void addContactPoint(Collection<ContactPoint> contactPoints, ContactPoint newContactPoint)
            throws DuplicateContactPointException {
        ContactPointType newType = newContactPoint.contactPointType;

        contactPoints.forEach(contactPoint -> {
            if (newType == contactPoint.contactPointType) {
                contactPoint.endNow();
            }
        });

        newContactPoint.startDate = new GregorianCalendar();
        contactPoints.add(newContactPoint);
    }

    /**
     * Return the current contact point of the specified type
     * @param contactPoints the contact points
     * @param sought the type being vound
     * @return the currently in force location
     */
    public static ContactPoint findCurrentContactPoint(Collection<ContactPoint> contactPoints, ContactPointType sought) {
        ContactPoint result = null;

        for (ContactPoint contactPoint : contactPoints) {
            if (contactPoint.contactPointType == sought && contactPoint.isInPlay()) {
                result = contactPoint;
                break;
            }
        }

        return result;
    }
}
