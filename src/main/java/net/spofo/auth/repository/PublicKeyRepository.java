package net.spofo.auth.repository;

import net.spofo.auth.entity.PublicKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicKeyRepository extends JpaRepository<PublicKey, Long> {

}