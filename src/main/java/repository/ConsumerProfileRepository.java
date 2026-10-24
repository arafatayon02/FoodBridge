package com.foodbridge.consumer;import org.springframework.data.jpa.repository.JpaRepository;import java.util.Optional;public interface ConsumerProfileRepository extends JpaRepository<ConsumerProfile,Long>{Optional<ConsumerProfile> findByEmail(String email);}

