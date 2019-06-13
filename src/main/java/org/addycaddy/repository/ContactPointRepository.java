package org.addycaddy.repository;

import org.addycaddy.pojo.ContactPoint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactPointRepository extends JpaRepository<ContactPoint, Long> {
    List<ContactPoint> findByCustomerId(String customerId);

    List<ContactPoint> findByEmailAddressEmail(String email);

    List<ContactPoint> findByAddressPostalCode(String postalCode);

    List<ContactPoint> findByPhonePhoneNumber(String phone);

    ContactPoint findByExternalId(String externalId);
}
