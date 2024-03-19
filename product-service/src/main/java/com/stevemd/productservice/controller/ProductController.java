package com.stevemd.productservice.controller;

import com.stevemd.productservice.dto.ProductRequest;
import com.stevemd.productservice.dto.ProductResponse;
import com.stevemd.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> findAll() {
       return productService.findAllProducts();
    }

    @PostMapping("/create")
    //@ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(@RequestBody ProductRequest productRequest) {
         return productService.createProduct(productRequest);
    }
}
