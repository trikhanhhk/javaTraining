package com.javateam.mgep.service.impl;

import com.javateam.mgep.entity.Authoritty;
import com.javateam.mgep.repositories.AuthorityRepository;
import com.javateam.mgep.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorityServiceIpml implements AuthorityService {

    @Autowired
    AuthorityRepository authorityRepository;

    @Override
    public List<Authoritty> findByAllAuthoritty() {
        List<Authoritty> authorittyList = authorityRepository.findAll();
        return authorittyList;
    }
}
