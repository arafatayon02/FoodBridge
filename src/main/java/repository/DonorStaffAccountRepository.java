package com.foodbridge.donor;
import org.springframework.data.jpa.repository.JpaRepository;import java.util.List;
public interface DonorStaffAccountRepository extends JpaRepository<DonorStaffAccount,Long>{List<DonorStaffAccount> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId);boolean existsByOrganizationIdAndEmail(Long organizationId,String email);java.util.Optional<DonorStaffAccount> findFirstByEmailAndActiveTrue(String email);}
