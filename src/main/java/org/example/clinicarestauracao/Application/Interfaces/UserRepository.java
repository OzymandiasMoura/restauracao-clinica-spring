package org.example.clinicarestauracao.Application.Interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.clinicarestauracao.Domain.Entities.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long>
{
    UserDetails findUserByUsername(String username);
}
