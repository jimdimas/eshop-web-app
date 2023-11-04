package com.jimdimas.api.product;

import com.jimdimas.api.exception.BadRequestException;
import com.jimdimas.api.exception.ConflictException;
import com.jimdimas.api.exception.NotFoundException;
import com.jimdimas.api.user.User;
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
    public void addProduct(@RequestAttribute(name="user") User user, @RequestBody Product product,@RequestParam(name="category") String category)
            throws NotFoundException, BadRequestException {
        productService.addProduct(user,product,category);
    }

    @PutMapping(path="{productId}")
    public void updateProduct(@RequestAttribute(name="user") User user,@PathVariable UUID productId,@RequestBody Product product)
            throws ConflictException, BadRequestException, NotFoundException {
        productService.updateProduct(user,productId,product);
    }

    @DeleteMapping(path="{productId}")
    public void deleteProduct(@RequestAttribute(name="user") User user,@PathVariable UUID productId) throws ConflictException, NotFoundException {
        productService.deleteProduct(user,productId);
    }
}
