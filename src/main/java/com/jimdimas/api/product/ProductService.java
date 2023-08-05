package com.jimdimas.api.product;

import com.jimdimas.api.user.User;
import com.jimdimas.api.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    @GetMapping
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @PostMapping
    public void addProduct(Product product,String username) {
        Optional<User> uploadUser = userService.getUserByUsername(username);
        if (!uploadUser.isPresent()){
            throw new IllegalStateException("No user exists with provided id");
        }
        checkProductFields(product);
        Product endProduct = Product.builder()  //not setting category enum yet because it can't be serialized by json
                .productId(UUID.randomUUID())
                .user(uploadUser.get())
                .name(product.getName())
                .price(product.getPrice())
                .capacity(product.getCapacity())
                .creationDate(LocalDate.now())
                .description(product.getDescription())
                .build();

        productRepository.save(endProduct);
    }

    public Optional<Product> getProductById(UUID productId) {  return productRepository.findProductByPublicId(productId); }


    public Optional<List<Product>> getUserProducts(String username) {
        return productRepository.findProductsByUserUsername(username);
    }

    private void checkProductFields(Product product){
        if (product.getPrice()<=0 || product.getPrice()>1000){
            throw new IllegalStateException("Invalid product price");
        }
        if (product.getName()==null || product.getName().isBlank() || product.getName().length()<2){
            throw new IllegalStateException("Invalid product name");
        }
        if (product.getCapacity()<0 || product.getCapacity()>1000){
            throw new IllegalStateException("Invalid product capacity");
        }
        if (product.getDescription().length()>100){
            throw new IllegalStateException("Product description can't be more than 100 characters");
        }
    }
}
