package org.addycaddy.service;

import org.addycaddy.client.dto.ContactPointDto;
import org.addycaddy.exception.AddyCaddyException;
import org.addycaddy.pojo.*;
import org.addycaddy.repository.ContactPointRepository;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ContactPointServiceImpl implements ContactPointService{
    private static final Logger         log = LoggerFactory.getLogger(ContactPointServiceImpl.class);

    @Autowired
    private ContactPointRepository      contactPointRepository;

    private Mapper                      mapper = new DozerBeanMapper();

    @Override
    public ContactPointDto create(ContactPointDto contactPointDto)
            throws AddyCaddyException {

        ContactPoint contactPoint = mapper.map(contactPointDto, ContactPoint.class);

        if (contactPoint.isAddress()) {
            Address address = mapper.map(contactPointDto, Address.class);
            contactPoint.setAddress(address);
        }
        else if (contactPoint.isEmail()) {
            EmailAddress email = mapper.map(contactPointDto, EmailAddress.class);
            contactPoint.setEmailAddress(email);
        }
        else if (contactPoint.isPhone()) {
            Phone phone = mapper.map(contactPointDto, Phone.class);
            contactPoint.setPhone(phone);
        }

        String customerId = contactPointDto.getCustomerId();
        List<ContactPoint> oldContactPoints = contactPointRepository.findByCustomerId(customerId);

        ContactPointType newType = contactPoint.getContactPointType();
        oldContactPoints.forEach(oldContactPoint -> {
            if (oldContactPoint.getContactPointType() == newType && oldContactPoint.isInPlay()) {
                oldContactPoint.setEndDate(LocalDate.now());
                contactPointRepository.saveAndFlush(oldContactPoint);
            }
        });

        contactPoint = contactPointRepository.saveAndFlush(contactPoint);

        ContactPointDto result = null;
        if (contactPoint.isAddress()) {
            result = mapper.map(contactPoint.getAddress(), ContactPointDto.class);
        }
        else if (contactPoint.isEmail()) {
            result = mapper.map(contactPoint.getEmailAddress(), ContactPointDto.class);
        }
        else if (contactPoint.isPhone()) {
            result = mapper.map(contactPoint.getPhone(), ContactPointDto.class);
        }

        result.setCustomerId(contactPoint.getCustomerId());
        result.setContactPointType(contactPoint.getContactPointType().toString());

        return result;
    }
}
