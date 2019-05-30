package org.addycaddy.service;

import org.addycaddy.client.dto.ContactPointDto;
import org.addycaddy.exception.AddyCaddyException;

import java.util.List;

public interface ContactPointService {

    ContactPointDto create(ContactPointDto contactPointDto)
            throws AddyCaddyException;

    ContactPointDto update(ContactPointDto contactPointDto)
            throws AddyCaddyException;

    List<ContactPointDto> findByCustomerId(String customerId);
}
