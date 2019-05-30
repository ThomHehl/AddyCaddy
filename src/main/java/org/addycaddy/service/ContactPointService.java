package org.addycaddy.service;

import org.addycaddy.client.dto.ContactPointDto;
import org.addycaddy.exception.AddyCaddyException;

public interface ContactPointService {

    ContactPointDto create(ContactPointDto contactPointDto)
            throws AddyCaddyException;

    ContactPointDto update(ContactPointDto contactPointDto)
            throws AddyCaddyException;
}
