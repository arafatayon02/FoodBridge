package com.foodbridge.donation;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationListingApiController {
    private final DonationListingService service;
    public DonationListingApiController(DonationListingService service){this.service=service;}
    @GetMapping public List<DonationListing> list(@RequestParam(defaultValue="false") boolean includeUnavailable){return includeUnavailable?service.all():service.available();}
    @GetMapping("/{id}") public DonationListing get(@PathVariable long id){try{return service.get(id);}catch(jakarta.persistence.EntityNotFoundException e){throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());}}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public DonationListing create(@Valid @RequestBody DonationListingRequest request,Authentication auth){return service.create(request,stableUserId(auth));}
    @PutMapping("/{id}") public DonationListing update(@PathVariable long id,@Valid @RequestBody DonationListingRequest request,Authentication auth){try{return service.update(id,request,stableUserId(auth));}catch(jakarta.persistence.EntityNotFoundException e){throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());}}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void withdraw(@PathVariable long id,Authentication auth){try{service.withdraw(id,stableUserId(auth));}catch(jakarta.persistence.EntityNotFoundException e){throw new ResponseStatusException(HttpStatus.NOT_FOUND,e.getMessage());}}
    private long stableUserId(Authentication auth){return Math.abs((long)auth.getName().hashCode());}
}
