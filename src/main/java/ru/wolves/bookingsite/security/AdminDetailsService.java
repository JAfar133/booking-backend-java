package ru.wolves.bookingsite.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.wolves.bookingsite.models.AdminUser;
import ru.wolves.bookingsite.repositories.AdminRepo;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AdminDetailsService implements UserDetailsService {
    private final AdminRepo adminRepo;

    @Autowired
    public AdminDetailsService(AdminRepo adminRepo) {
        this.adminRepo = adminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AdminUser> admin = adminRepo.findByUsername(username);
        if(admin.isEmpty()){
            throw new UsernameNotFoundException("Admin not found");
        }
        return new AdminDetails(admin.get());
    }
}
