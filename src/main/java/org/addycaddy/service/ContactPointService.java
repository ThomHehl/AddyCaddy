package org.addycaddy.service;

import org.addycaddy.client.dto.ContactPointDto;
import org.addycaddy.exception.AddyCaddyException;

import java.util.List;

public interface ContactPointService {

    ContactPointDto create(ContactPointDto contactPointDto)
            throws AddyCaddyException;

    ContactPointDto[] create(ContactPointDto[] contactPoints)
            throws AddyCaddyException;

    List<ContactPointDto> findByCustomerId(String customerId);

    List<ContactPointDto> findByEmail(String email);

    List<ContactPointDto> findByPhone(String phoneNumber);

    List<ContactPointDto> findByPostalCode(String postalCode);

    ContactPointDto update(ContactPointDto contactPointDto)
            throws AddyCaddyException;

    ContactPointDto[] update(ContactPointDto[] contactPoints)
            throws AddyCaddyException;
}
