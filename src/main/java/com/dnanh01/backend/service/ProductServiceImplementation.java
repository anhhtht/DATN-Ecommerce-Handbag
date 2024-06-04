package com.dnanh01.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dnanh01.backend.dto.ProductDto;
import com.dnanh01.backend.exception.ProductException;
import com.dnanh01.backend.model.Brand;
import com.dnanh01.backend.model.Product;
import com.dnanh01.backend.repository.BrandRepository;
import com.dnanh01.backend.repository.ProductRepository;
import com.dnanh01.backend.request.CreateProductRequest;

@Service
public class ProductServiceImplementation implements ProductService {

    private ProductRepository productRepository;
    // private UserService userService;
    private BrandRepository brandRepository;

    public ProductServiceImplementation(
            ProductRepository productRepository,
            UserService userService,
            BrandRepository brandRepository) {
        this.productRepository = productRepository;
        // this.userService = userService;
        this.brandRepository = brandRepository;

    }

    @Override
    public Product createProduct(CreateProductRequest req) {

        Brand foundBrand = brandRepository.findSingleBrandByName(req.getBrand().getName());

        if (foundBrand == null) {
            Brand saveBrand = new Brand();
            saveBrand.setName(req.getBrand().getName());
            saveBrand.setImageUrl(req.getBrand().getImageUrl());
            foundBrand = brandRepository.save(saveBrand);

        }

        Product product = new Product();
        product.setBrand(foundBrand);
        product.setDiscountPersent(req.getDiscountPersent());
        product.setCreateAt(LocalDateTime.now());
        product.setDescription(req.getDescription());
        product.setDiscountedPrice(req.getDiscountedPrice());
        product.setImageUrl(req.getImageUrl());
        product.setTitle(req.getTitle());
        product.setWarehousePrice(req.getWarehousePrice());
        product.setPrice(req.getPrice());
        product.setColors(req.getColor());
        product.setQuantity(req.getQuantity());

        Product savedProduct = productRepository.save(product);

        return savedProduct;
    }

    @Override
    public String deleteProduct(Long productId) throws ProductException {
        Product product = findProductById(productId);
        product.getColors().clear();
        productRepository.delete(product);
        return "Product deleted successfully !!!";
    }

    @Override
    public Product updateProduct(Long productId, Product req) throws ProductException {
        Product product = findProductById(productId);

        if (product.getId().equals(productId)) {
            // update so luong san pham
            product.setDescription(req.getDescription());
            product.setPrice(req.getPrice());
            product.setDiscountedPrice(req.getDiscountedPrice());
            product.setDiscountPersent(req.getDiscountPersent());
            product.setWarehousePrice(req.getWarehousePrice());

            product.setQuantity(req.getQuantity());
            product.setColors(req.getColors());

        }
        return productRepository.save(product);
    }

    @Override
    public Product findProductById(Long id) throws ProductException {
        Optional<Product> opt = productRepository.findById(id);
        if (opt.isPresent()) {
            return opt.get();
        }
        throw new ProductException("Product not found with id - " + id);
    }

    @Override
    public List<Product> findProductByBrand(String brand) {

        return null;
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getAllProduct(String brand, List<String> colors, Integer minPrice,
            Integer maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        List<Product> products = productRepository.filterProducts(brand, minPrice, maxPrice, minDiscount, sort);  
        if (stock != null) {
            if (stock.equals("in_stock")) {
                products = products.stream().filter(p -> p.getQuantity() > 0).collect(Collectors.toList());
            } else if (stock.equals("out_of_stock")) {
                products = products.stream().filter(p -> p.getQuantity() < 1).collect(Collectors.toList());
            }
        }
        int startIndex = (int) pageable.getOffset();
        int endIndex = Math.min(startIndex + pageable.getPageSize(), products.size());
        List<Product> pageContent = products.subList(startIndex, endIndex);
        Page<Product> filteredProducts = new PageImpl<>(pageContent, pageable, products.size());
        return filteredProducts;
    }

    public List<ProductDto> getTopNewProducts() {
        List<Product> products = productRepository.findTop3ByOrderByCreateAtDesc();
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<ProductDto> getTopSellingProducts() {
        List<Product> products = productRepository.findTop3ByOrderByCreateAtDesc();
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public List<ProductDto> getTopRatingProducts() {
        List<Product> products = productRepository.findTop3ByOrderByCreateAtDesc();
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private ProductDto convertToDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscountedPrice(),
                product.getDiscountPersent(),
                product.getQuantity(),
                product.getImageUrl(),
                product.getCreateAt(),
                product.getBrand().getId(),
                product.getBrand().getName()
        );
    }
}
