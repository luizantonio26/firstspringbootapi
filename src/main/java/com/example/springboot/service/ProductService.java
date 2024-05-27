package com.example.springboot.service;

import com.example.springboot.controllers.ProductController;
import com.example.springboot.dtos.ProductRecordDto;
import com.example.springboot.models.ProductModel;
import com.example.springboot.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<ProductModel> getAllProducts(){
        List<ProductModel> products = productRepository.findAll();

        if (!products.isEmpty()){
            for (ProductModel product : products) {
                UUID id = product.getId();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }

        return products;
    }

    public ProductModel getProduct(UUID id){
        Optional<ProductModel> product = productRepository.findById(id);

        if(!product.isEmpty()){
            product.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("List Products"));
        }

        return product.get();
    }

    public ProductModel createProduct(ProductRecordDto productDto){
        var product = new ProductModel();
        BeanUtils.copyProperties(productDto, product);
        return productRepository.save(product);
    }

    public ProductModel updateProduct(UUID id, ProductRecordDto productRecordDto){
        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()){
            return product0.get();
        }
        var productModel = product0.get();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return productRepository.save(productModel);
    }

    public String deleteProduct(UUID id){
        Optional<ProductModel> product0 = productRepository.findById(id);
        if (product0.isEmpty()){
            return "Product not found";
        }
        productRepository.delete(product0.get());
        return "Product deleted";
    }

}
