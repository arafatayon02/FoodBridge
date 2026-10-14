package com.foodbridge.supershop.controller;
import com.foodbridge.supershop.dto.*;
import com.foodbridge.supershop.entity.*;
import com.foodbridge.supershop.service.FoodBridgeService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
@RestController
@RequestMapping("/api")
public class ApiController {
    private final FoodBridgeService service;
    public ApiController(FoodBridgeService service){this.service=service;}
    @GetMapping("/health") public Map<String,String> health(){return Map.of("status","UP","service","FoodBridge Supershop API");}
    @PostMapping("/supershops/register") public ResponseEntity<?> register(@Valid @RequestBody RegisterSupershopRequest r){return ResponseEntity.status(201).body(shopDto(service.register(r)));}
    @GetMapping("/admin/supershops/pending") public List<Map<String,Object>> pending(){return service.pending().stream().map(this::shopDto).toList();}
    @GetMapping("/admin/supershops") public List<Map<String,Object>> allShops(){return service.allShops().stream().map(this::shopDto).toList();}
    @PostMapping("/supershops/{id}/review") public Map<String,Object> review(@PathVariable Long id,@Valid @RequestBody ReviewRequest r,Authentication a){return shopDto(service.review(id,r,a.getName()));}
    @PostMapping("/listings") public ResponseEntity<?> createListing(@Valid @RequestBody ListingRequest r){return ResponseEntity.status(201).body(listingDto(service.createListing(r)));}
    @GetMapping("/listings") public List<Map<String,Object>> listings(){return service.publicListings().stream().map(this::listingDto).toList();}
    @GetMapping("/price-preview") public Map<String,Object> price(@RequestParam BigDecimal originalPrice,@RequestParam BigDecimal discountPercent){return service.pricePreview(originalPrice,discountPercent);}
    @PostMapping(value="/bulk-listings",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> bulk(@RequestParam Long supershopId,@RequestPart("file") MultipartFile file){
        if(file.isEmpty())throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose a CSV file.");
        try(BufferedReader br=new BufferedReader(new InputStreamReader(file.getInputStream(),StandardCharsets.UTF_8))){
            String header=br.readLine();if(header==null||!header.replace("\uFEFF","").trim().equalsIgnoreCase("productName,sku,originalPrice,discountPercent,quantity,expiryDate"))
                throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"CSV header must be: productName,sku,originalPrice,discountPercent,quantity,expiryDate");
            List<ListingRequest> rows=new ArrayList<>();String line;int lineNo=1;
            while((line=br.readLine())!=null){lineNo++;if(line.isBlank())continue;String[] c=line.split(",",-1);if(c.length!=6)throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid CSV columns on line "+lineNo);
                try{rows.add(new ListingRequest(supershopId,c[0].trim(),c[1].trim(),new BigDecimal(c[2].trim()),new BigDecimal(c[3].trim()),Integer.parseInt(c[4].trim()),LocalDate.parse(c[5].trim())));}
                catch(RuntimeException ex){throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid value on CSV line "+lineNo+"; dates must use YYYY-MM-DD.");}
            }
            return ResponseEntity.status(201).body(service.bulkCreate(supershopId,rows).stream().map(this::listingDto).toList());
        }catch(IOException ex){throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST,"Could not read uploaded CSV.");}
    }
    private Map<String,Object> shopDto(Supershop s){Map<String,Object> m=new LinkedHashMap<>();m.put("id",s.getId());m.put("businessName",s.getBusinessName());m.put("address",s.getAddress());m.put("contactPerson",s.getContactPerson());m.put("contactPhone",s.getContactPhone());m.put("ownerName",s.getOwnerName());m.put("ownerEmail",s.getOwnerEmail());m.put("status",s.getStatus());m.put("rejectionReason",s.getRejectionReason());m.put("reviewedBy",s.getReviewedBy());m.put("reviewedAt",s.getReviewedAt());m.put("createdAt",s.getCreatedAt());return m;}
    private Map<String,Object> listingDto(DiscountListing l){return Map.of("id",l.getId(),"supershopId",l.getSupershopId(),"businessName",l.getBusinessName(),"productName",l.getProductName(),"sku",l.getSku(),"originalPrice",l.getOriginalPrice(),"discountPercent",l.getDiscountPercent(),"discountedPrice",l.getDiscountedPrice(),"quantity",l.getQuantity(),"expiryDate",l.getExpiryDate(),"active",l.isActive());}
}
