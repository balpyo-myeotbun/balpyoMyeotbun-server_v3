package site.balpyo.auth.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.balpyo.auth.entity.User;
import site.balpyo.auth.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

            return UserDetailsImpl.build(user);
        } catch (UsernameNotFoundException e) {
            System.out.println("UsernameNotFoundException occurred while loading user by email: " + email);
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.out.println("Unexpected exception occurred while loading user by email: " + email);
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred while loading user by email", e);
        }
    }


//    @Override
//    @Transactional
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + email));
//
//        return UserDetailsImpl.build(user);
//    }

}
