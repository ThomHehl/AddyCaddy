package org.addycaddy.pojo;

import com.heavyweightsoftware.util.DateHelper;
import com.heavyweightsoftware.util.StringHelper;
import org.addycaddy.exception.AddyCaddyException;
import org.addycaddy.exception.DuplicateContactPointException;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Objects;

@Entity
@Table(indexes = { @Index(name = "CON_PT_CUST_ID_IDX", columnList=ContactPoint.COL_CUSTOMER_ID, unique = false) })
public class ContactPoint {

    public static final LocalDate       DEFAULT_END_DATE = LocalDate.of(2999, 12, 31);
    public static final int             EXTERNAL_ID_LENGTH = 64;
    public static final String          COL_CUSTOMER_ID = "customer_id";

    @Id
    @GeneratedValue
    private Long                        id;

    @Column(nullable = false, unique = true)
    private String                      externalId;

    @Column(nullable = false, name = COL_CUSTOMER_ID)
    private String                      customerId;

    @Column(nullable = true)
    private String                      otherId;

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
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "fk_address")
    private Address                     address;

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "fk_email")
    private EmailAddress                emailAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "fk_phone")
    private Phone                       phone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOtherId() {
        return otherId;
    }

    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    public ContactPointType getContactPointType() {
        return contactPointType;
    }

    public void setContactPointType(ContactPointType contactPointType) {
        this.contactPointType = contactPointType;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId() {
        setExternalId(StringHelper.generateRandom(EXTERNAL_ID_LENGTH, StringHelper.URL_SAFE_CHARACTERS));
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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

    public void setEndDate() {
        setEndDate(LocalDate.now());
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = DateHelper.toCalendar(endDate);
    }

    public LocalDate getStartDate() {
        return DateHelper.toLocalDate(this.startDate);
    }

    public void setStartDate() {
        this.startDate = new GregorianCalendar();
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

    public boolean isAddress() {
        boolean result;

        switch (contactPointType) {
            case BillingAddress:
            case BusinessAddress:
            case HomeAddress:
            case LocationAddress:
            case ShippingAddress:
            case WorkAddress:
                result = true;
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    public boolean isEmail() {
        boolean result;

        switch (contactPointType) {
            case BillingEmail:
            case BusinessEmail:
            case HomeEmail:
            case LocationEmail:
            case WorkEmail:
                result = true;
                break;
            default:
                result = false;
                break;
        }

        return result;
    }


    public boolean isPhone() {
        boolean result;

        switch (contactPointType) {
            case BillingFax:
            case BillingPhone:
            case BusinessFax:
            case BusinessPhone:
            case LocationFax:
            case LocationPhone:
            case HomeFax:
            case HomePhone:
            case WorkFax:
            case WorkPhone:
                result = true;
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    @Override
    public String toString() {
        return "ContactPoint{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", contactPointType=" + contactPointType +
                ", endDate=" + endDate +
                ", startDate=" + startDate +
                ", address=" + address +
                ", emailAddress=" + emailAddress +
                ", phone=" + phone +
                '}';
    }
}
