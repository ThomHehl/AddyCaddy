package org.addycaddy.service;

import com.heavyweightsoftware.exception.HeavyweightException;
import org.addycaddy.client.dto.ContactPointDto;

public interface AddressValidationService {
    ContactPointDto[] validate(ContactPointDto contactPointDto) throws HeavyweightException;
}
