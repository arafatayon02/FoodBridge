package com.foodbridge.supershop.service;
import com.foodbridge.supershop.dto.*;
import com.foodbridge.supershop.entity.*;
import com.foodbridge.supershop.repository.*;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.*;
import java.time.LocalDate;
import java.util.*;
@Service
public class BranchService {
    private final SupershopRepository shops; private final DiscountListingRepository listings; private final PasswordEncoder encoder;
    public FoodBridgeService(SupershopRepository shops,DiscountListingRepository listings,PasswordEncoder encoder){this.shops=shops;this.listings=listings;this.encoder=encoder;}

    public List<DiscountListing> publicListings(){return listings.findByActiveTrueAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(LocalDate.now());}
    public Map<String,Object> pricePreview(BigDecimal price,BigDecimal percent){
        if(price==null||price.compareTo(BigDecimal.ZERO)<=0||percent==null||percent.compareTo(BigDecimal.ZERO)<0||percent.compareTo(new BigDecimal("90"))>0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Price must be positive and discount must be between 0 and 90 percent.");
        BigDecimal saved=price.multiply(percent).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);
        return Map.of("originalPrice",price.setScale(2,RoundingMode.HALF_UP),"discountPercent",percent,"savings",saved,"discountedPrice",price.subtract(saved).setScale(2,RoundingMode.HALF_UP));
    }
    @Transactional public List<DiscountListing> bulkCreate(Long shopId,List<ListingRequest> rows){
        if(rows.isEmpty()||rows.size()>500)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Upload must contain 1 to 500 data rows.");
        List<DiscountListing> result=new ArrayList<>();
        for(ListingRequest row:rows){
            if(!shopId.equals(row.supershopId()))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Every row must use the selected branch ID.");
            result.add(createListing(row));
        }
        return result;
    }
}
