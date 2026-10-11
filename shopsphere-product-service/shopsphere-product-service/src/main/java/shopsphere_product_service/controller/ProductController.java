package shopsphere_product_service.controller;

import shopsphere_product_service.model.Product;
import shopsphere_product_service.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public String getProductById(@PathVariable Long id) {

        Product product = productService.getProductById(id);

        return "Handled by Product Service on port "
                + System.getProperty("server.port")
                + " | Product: "
                + product.getName();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }
}