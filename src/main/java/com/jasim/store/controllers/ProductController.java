package com.jasim.store.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jasim.store.dtos.ErrorDto;
import com.jasim.store.dtos.ProductDto;
import com.jasim.store.entities.Product;
import com.jasim.store.exceptions.ProductNotFoundException;
import com.jasim.store.exceptions.RedisDataException;
import com.jasim.store.mappers.ProductMapper;
import com.jasim.store.repositories.CategoryRepository;
import com.jasim.store.repositories.ProductRepository;
import com.jasim.store.services.RedisService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/product")
@AllArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final RedisService redisService;

    @GetMapping("/all")
    public List<ProductDto> getAllProducts(@RequestParam(name = "categoryId", defaultValue = "", required = false) Short categoryId) {
       try {
           List<ProductDto> productRedis = redisService.get("Category_"+categoryId, new TypeReference<List<ProductDto>>(){});
           if (productRedis != null){
               return productRedis;
           }else {
               List<Product> products;
               if (categoryId != null) {
                   products = productRepository.findProductByCategory_Id(categoryId);
               } else {
                   products = productRepository.findProductsByCategory();
               }
               redisService.set("Category_" + categoryId, products.stream().map(productMapper::toDto).toList(), 300l);
               return products.stream().map(productMapper::toDto).toList();
           }

       }catch (Exception e){
           throw new RedisDataException(e.getMessage());
       }

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findProductById(@PathVariable() Long id) {
        var product = productRepository.findById(id).orElse(null);
        if (product == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(productMapper.toDto(product));
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto request,
                                                    UriComponentsBuilder componentsBuilder) {
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }
        var product = productMapper.toEntity(request);
        product.setCategory(category);
        productRepository.save(product);
        request.setId(product.getId());
        var uri = componentsBuilder.path("/product/{id}").buildAndExpand(request.getId()).toUri();
        return ResponseEntity.created(uri).body(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,
                                                    @RequestBody ProductDto request
    ) {
        var category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id" + id));
        productMapper.toEntityUpdate(request, product);
        product.setCategory(category);
        productRepository.save(product);
        request.setId(product.getId());

        return ResponseEntity.ok(request);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        Product product = productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id" + id));
        productRepository.deleteById(product.getId());
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RedisDataException.class)
    public ResponseEntity<ErrorDto> handleRedisException( Exception e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ErrorDto(e.getMessage()+"xxxxxxxxx")
        );
    }
}

