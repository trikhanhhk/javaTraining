package com.javateam.mgep.service;

import com.javateam.mgep.entity.Authoritty;
import com.javateam.mgep.entity.CustomUserDetails;
import com.javateam.mgep.entity.Employee;
import com.javateam.mgep.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private EmployeeRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        // Kiểm tra xem employee có tồn tại trong database không?
        Employee user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        Set<Authoritty> authoritties = user.getAuthorities();
        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        if (authoritties != null){
            for (Authoritty authoritty : authoritties) {
                GrantedAuthority authority = new SimpleGrantedAuthority(authoritty.getName());
                grantList.add(authority);
            }
        }
        return new CustomUserDetails(user,grantList);
    }


}
