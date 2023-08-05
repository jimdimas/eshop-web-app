package com.jimdimas.api.product;

import com.jimdimas.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/v1/product"})
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @GetMapping()
    public Optional<List<Product>> getAllProducts(@RequestParam(name="user",required = false) Optional<String> username){
        if (username.isPresent()){
            return productService.getUserProducts(username.get());
        }
        return Optional.ofNullable(productService.getAllProducts());
    }

    @GetMapping(path="{productId}")
    public Optional<Product> getProductById(@PathVariable UUID productId){
        return productService.getProductById(productId);
    }

    @PostMapping
    public void addProduct(@RequestBody Product product,@RequestParam(name="user") String username){
        productService.addProduct(product,username);
    }

}
