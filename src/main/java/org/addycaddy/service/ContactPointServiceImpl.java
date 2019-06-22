package org.addycaddy.service;

import com.heavyweightsoftware.exception.HeavyweightException;
import com.heavyweightsoftware.util.MultimapList;
import org.addycaddy.client.dto.ContactPointDto;
import org.addycaddy.exception.AddyCaddyException;
import org.addycaddy.pojo.*;
import org.addycaddy.repository.ContactPointRepository;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

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
                contactPointRepository.saveAndFlush(oldContactPoint);
            }
        });

        ContactPoint contactPoint = fromDto(contactPointDto);
        contactPoint.setStartDate();
        contactPoint = contactPointRepository.saveAndFlush(contactPoint);

        ContactPointDto result = toDto(contactPoint);
        return result;
    }

    @Override
    public ContactPointDto[] create(ContactPointDto[] contactPointDtos) throws AddyCaddyException {
        Map<String, List<ContactPoint>> newContactPointMap = mapByCustomer(contactPointDtos);

        List<ContactPoint> oldContactPoints = contactPointRepository.findByCustomerIdIn(newContactPointMap.keySet());
        Map<String, List<ContactPoint>> oldContactPointMap = new MultimapList<>();
        oldContactPoints.forEach(cp -> {
            String customerId = cp.getCustomerId();
            List<ContactPoint> customerList = oldContactPointMap.get(customerId);
            customerList.add(cp);
        });

        List<ContactPoint> toBeSaved = new ArrayList<>();
        List<ContactPointDto> returnList = new ArrayList<>();

        newContactPointMap.entrySet().forEach(entry ->{
            final String customerId = entry.getKey();
            List<ContactPoint> newContactPointList = entry.getValue();
            newContactPointList.forEach(newContactPoint -> {
                ContactPointType newtype = newContactPoint.getContactPointType();

                oldContactPointMap.get(customerId).forEach(oldContactPoint -> {
                    if (newtype == oldContactPoint.getContactPointType()) {
                        oldContactPoint.setEndDate();
                        toBeSaved.add(oldContactPoint);
                    }
                });

                toBeSaved.add(newContactPoint);
                ContactPointDto dto = toDto(newContactPoint);
                returnList.add(dto);
            });
        });

        contactPointRepository.saveAll(toBeSaved);
        ContactPointDto[] result = new ContactPointDto[returnList.size()];
        result = returnList.toArray(result);

        return result;
    }

    private Map<String, List<ContactPoint>> mapByCustomer(ContactPointDto[] contactPointDtos) throws AddyCaddyException {
        Map<String, List<ContactPoint>> result = new MultimapList<>();

        for (ContactPointDto contactPointDto : contactPointDtos){
            String customerId = contactPointDto.getCustomerId();
            List<ContactPoint> contactPoints = result.get(customerId);
            ContactPoint contactPoint = fromDto(contactPointDto);
            contactPoint.setStartDate();
            contactPoints.add(contactPoint);
        }

        return result;
    }

    private ContactPoint fromDto(ContactPointDto contactPointDto) throws AddyCaddyException {
        ContactPoint contactPoint = mapper.map(contactPointDto, ContactPoint.class);

        if (StringUtils.isEmpty(contactPointDto.getAddressId())) {
            contactPoint.setExternalId();
        }
        else {
            contactPoint.setExternalId(contactPointDto.getAddressId());
        }

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

    private List<ContactPointDto> toDto(List<ContactPoint> contactPoints) {
        List<ContactPointDto> result = new ArrayList<>(contactPoints.size());

        contactPoints.forEach(contactPoint -> {
            if(contactPoint.isInPlay()) {
                ContactPointDto dto = toDto(contactPoint);
                result.add(dto);
            }
        });

        return result;
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

        List<ContactPointDto> result = toDto(contactPoints);

        return result;
    }

    @Override
    public List<ContactPointDto> findByEmail(String email) {
        List<ContactPoint> contactPoints = contactPointRepository.findByEmailAddressEmail(email);
        List<ContactPointDto> result = toDto(contactPoints);
        return result;
    }

    @Override
    public List<ContactPointDto> findByPhone(String phoneNumber) {
        List<ContactPoint> contactPoints = contactPointRepository.findByPhonePhoneNumber(phoneNumber);
        List<ContactPointDto> result = toDto(contactPoints);
        return result;
    }

    @Override
    public List<ContactPointDto> findByPostalCode(String postalCode) {
        List<ContactPoint> contactPoints = contactPointRepository.findByAddressPostalCode(postalCode);
        List<ContactPointDto> result = toDto(contactPoints);
        return result;
    }

    @Override
    public ContactPointDto update(ContactPointDto contactPointDto) throws AddyCaddyException {
        String externalId = contactPointDto.getAddressId();

        ContactPoint contactPoint = contactPointRepository.findByExternalId(externalId);
        updateContactPoint(contactPoint, contactPointDto);
        contactPoint = contactPointRepository.saveAndFlush(contactPoint);

        ContactPointDto result = toDto(contactPoint);
        return result;
    }

    private void updateContactPoint(ContactPoint contactPoint, ContactPointDto contactPointDto) throws AddyCaddyException {
        if (contactPoint == null) {
            throw new AddyCaddyException("Contact Point not found for external ID:" + contactPointDto.getAddressId());
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
            phone.setPhoneNumber(contactPointDto.getPhoneNumber());
            phone.setCountryCode(CountryCode.valueOf(contactPointDto.getCountryCode()));
        }
    }

    @Override
    public ContactPointDto[] update(ContactPointDto[] contactPointDtos) throws AddyCaddyException {
        Set<String> addressIds = new HashSet<>();

        for (ContactPointDto dto : contactPointDtos) {
            String addressId = dto.getAddressId();
            if (StringUtils.isEmpty(addressId)) {
                throw new AddyCaddyException("Unable to update with no address ID:" + dto.getAddressId());
            }
            addressIds.add(addressId);
        }

        List<ContactPoint> dbContactPoints = contactPointRepository.findByExternalIdIn(addressIds);
        for (ContactPointDto dto : contactPointDtos) {
            String addressId = dto.getAddressId();

            ContactPoint dbContactPoint = null;
            for (ContactPoint dbcp : dbContactPoints) {
                if (addressId.equals(dbcp.getExternalId())) {
                    dbContactPoint = dbcp;
                    break;
                }
            }

            updateContactPoint(dbContactPoint, dto);
        }

        dbContactPoints = contactPointRepository.saveAll(dbContactPoints);
        List<ContactPointDto> resultList = toDto(dbContactPoints);
        ContactPointDto[] result = new ContactPointDto[resultList.size()];
        result = resultList.toArray(result);

        return result;
    }
}
