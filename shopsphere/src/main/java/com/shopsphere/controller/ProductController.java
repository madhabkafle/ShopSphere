package com.shopsphere.controller;
import com.shopsphere.service.ProductServiceClient;
import org.springframework.data.domain.Page;
import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;
import com.shopsphere.mapper.ProductMapper;
import com.shopsphere.model.Category;
import com.shopsphere.model.Product;
import com.shopsphere.service.CategoryService;
import com.shopsphere.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final ProductServiceClient productServiceClient;

    public ProductController(ProductService productService,
                             ProductMapper productMapper,
                             CategoryService categoryService,
                             ProductServiceClient productServiceClient) {

        this.productService = productService;
        this.productMapper = productMapper;
        this.categoryService = categoryService;
        this.productServiceClient = productServiceClient;
    }

    // CREATE product
    @PostMapping("/api/products")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        Product product = productService.createProduct(request);

        ProductResponse response = productMapper.toResponse(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/api/products-from-service/{id}")
    public String getProductFromProductService(@PathVariable Long id) {
        return productServiceClient.getProductById(id);
    }

    // GET product by ID
    @GetMapping("/api/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        Product product = productService.getProductById(id);

        ProductResponse response = productMapper.toResponse(product);

        return ResponseEntity.ok(response);
    }

    // TEST TRANSACTION
    @PostMapping("/api/products/test-transaction")
    public ResponseEntity<Void> testTransaction() {

        productService.testTransaction();

        return ResponseEntity.ok().build();
    }

    // UPDATE product
    @PutMapping("/api/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        Product product = productService.updateProduct(id, request);

        ProductResponse response = productMapper.toResponse(product);

        return ResponseEntity.ok(response);
    }

    // DELETE product
    @DeleteMapping("/api/products/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    // CREATE category
    @PostMapping("/api/categories")
    public Category createCategory(@RequestBody Category category) {

        return categoryService.createCategory(category);
    }

}