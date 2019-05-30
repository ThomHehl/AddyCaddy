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
import java.util.ArrayList;
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


        String customerId = contactPointDto.getCustomerId();
        List<ContactPoint> oldContactPoints = contactPointRepository.findByCustomerId(customerId);

        ContactPointType newType = ContactPointType.valueOf(contactPointDto.getContactPointType());
        oldContactPoints.forEach(oldContactPoint -> {
            if (oldContactPoint.getContactPointType() == newType && oldContactPoint.isInPlay()) {
                oldContactPoint.setEndDate(LocalDate.now());
            }
        });

        ContactPoint contactPoint = fromDto(contactPointDto);
        contactPoint.setExternalId();
        contactPoint.setStartDate();
        contactPoint = contactPointRepository.saveAndFlush(contactPoint);

        ContactPointDto result = toDto(contactPoint);
        return result;
    }

    private ContactPoint fromDto(ContactPointDto contactPointDto) throws AddyCaddyException {
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

        return contactPoint;
    }

    private ContactPointDto toDto(ContactPoint contactPoint) {
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
        result.setAddressId(contactPoint.getExternalId());

        return result;
    }

    @Override
    public List<ContactPointDto> findByCustomerId(String customerId) {
        List<ContactPoint> contactPoints = contactPointRepository.findByCustomerId(customerId);

        List<ContactPointDto> result = new ArrayList<>(contactPoints.size());
        contactPoints.forEach(contactPoint -> {
            ContactPointDto dto = toDto(contactPoint);
            result.add(dto);
        });

        return result;
    }

    @Override
    public ContactPointDto update(ContactPointDto contactPointDto) throws AddyCaddyException {
        String externalId = contactPointDto.getAddressId();

        ContactPoint contactPoint = contactPointRepository.findByExternalId(externalId);
        if (contactPoint == null) {
            throw new AddyCaddyException("Contact Point not found for external ID:" + externalId);
        }

        if (contactPoint.isAddress()) {
            Address address = contactPoint.getAddress();
            address.setAttention(contactPointDto.getAttention());
            address.setName(contactPointDto.getName());
            address.setStreet1(contactPointDto.getStreet1());
            address.setStreet2(contactPointDto.getStreet2());
            address.setCity(contactPointDto.getCity());
            address.setState(contactPointDto.getState());
            address.setPostalCode(contactPointDto.getPostalCode());
            address.setCountryCode(CountryCode.valueOf(contactPointDto.getCountryCode()));
        }
        else if (contactPoint.isEmail()) {
            EmailAddress email = contactPoint.getEmailAddress();
            email.setEmail(contactPointDto.getEmail());
        }
        else if (contactPoint.isPhone()) {
            Phone phone = contactPoint.getPhone();
            phone.setPhone(contactPointDto.getPhoneNumber());
            phone.setCountryCode(CountryCode.valueOf(contactPointDto.getCountryCode()));
        }

        contactPoint = contactPointRepository.saveAndFlush(contactPoint);

        ContactPointDto result = toDto(contactPoint);
        return result;
    }
}
