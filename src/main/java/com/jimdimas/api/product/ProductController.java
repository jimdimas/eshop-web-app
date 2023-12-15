package com.jimdimas.api.product;

import com.jimdimas.api.exception.BadRequestException;
import com.jimdimas.api.exception.ConflictException;
import com.jimdimas.api.exception.NotFoundException;
import com.jimdimas.api.exception.UnauthorizedException;
import com.jimdimas.api.user.User;
import com.jimdimas.api.user.UserService;
import com.jimdimas.api.util.JsonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/v1/product"})
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    public Optional<List<ProductProjection>> getAllProducts(
            @RequestParam(name="user",required = false) Optional<String> username,
            @RequestParam(name="category",required=false) Optional<String> category,
            @RequestParam(name="sortBy",required = false) Optional<String> sortBy) throws BadRequestException {
        Dictionary<String,String> searchKeys = new Hashtable<>();
        if (category.isPresent()){
            searchKeys.put("category",category.get());
        }
        if (sortBy.isPresent()){
            searchKeys.put("sortBy",sortBy.get());
        }
        if (username.isPresent()){
            searchKeys.put("username",username.get());
        }
        return Optional.ofNullable(productService.getAllProducts(searchKeys));
    }

    @GetMapping(path="{productId}")
    public Optional<Product> getProductById(@PathVariable UUID productId){
        return productService.getProductById(productId);
    }

    @PostMapping
    public ResponseEntity<JsonResponse> addProduct(@RequestAttribute(name="user") User user, @RequestBody Product product, @RequestParam(name="category") String category)
            throws NotFoundException, BadRequestException, UnauthorizedException {
        return ResponseEntity.ok(productService.addProduct(user,product,category));
    }

    @PutMapping(path="{productId}")
    public ResponseEntity<JsonResponse> updateProduct(@RequestAttribute(name="user") User user,@PathVariable UUID productId,@RequestBody Product product)
            throws ConflictException, BadRequestException, NotFoundException {
        return ResponseEntity.ok(productService.updateProduct(user,productId,product));
    }

    @DeleteMapping(path="{productId}")
    public ResponseEntity<JsonResponse> deleteProduct(@RequestAttribute(name="user") User user,@PathVariable UUID productId) throws ConflictException, NotFoundException {
        return ResponseEntity.ok(productService.deleteProduct(user,productId));
    }
}
