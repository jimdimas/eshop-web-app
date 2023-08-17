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
    public void addProduct(User user, Product product, String providedCategory) {
        Optional<User> uploadUser = userService.getUserByUsername(user, user.getUsername());
        if (!uploadUser.isPresent()){
            throw new IllegalStateException("No user exists with provided id");
        }
        Category actualCategory=null;
        for (Category field:Category.values()){
            if (field.name().equals(providedCategory)){
                actualCategory=field;
                break;
            }
        }
        if (actualCategory==null){
            throw new IllegalStateException("Invalid product category provided");
        }
        checkProductFields(product);
        Product endProduct = Product.builder()  //not setting category enum yet because it can't be serialized by json
                .productId(UUID.randomUUID())
                .user(uploadUser.get())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .category(actualCategory)
                .creationDate(LocalDate.now())
                .description(product.getDescription())
                .build();

        productRepository.save(endProduct);
    }

    public Optional<Product> getProductById(UUID productId) {  return productRepository.findProductByPublicId(productId); }


    public Optional<List<Product>> getUserProducts(String username) {
        return productRepository.findProductsByUserUsername(username);
    }

    private void checkProductFields(Product product) throws IllegalStateException{
        if (product.getPrice()<=0 || product.getPrice()>1000){
            throw new IllegalStateException("Invalid product price");
        }
        if (product.getName()==null || product.getName().isBlank() || product.getName().length()<2){
            throw new IllegalStateException("Invalid product name");
        }
        if (product.getQuantity()<0 || product.getQuantity()>1000){
            throw new IllegalStateException("Invalid product capacity");
        }
        if (product.getDescription().length()>100){
            throw new IllegalStateException("Product description can't be more than 100 characters");
        }
    }

    public void updateProduct(User user, UUID productId, Product product) {
        Optional<Product> productExists = productRepository.findProductByPublicId(productId);
        if (!productExists.isPresent()){
            throw new IllegalStateException("No product with given id exists");
        }
        Product updatedProduct = productExists.get();
        if (!user.getUsername().equals(updatedProduct.getUser().getUsername())){
            throw new IllegalStateException("Cannot update given product,it was created by a different user");
        }

        //If new value is set,update it,else keep old value
        if (product.getName()!=null && !product.getName().isBlank()){
            updatedProduct.setName(product.getName());
        }
        if (product.getDescription()!=null && !product.getDescription().isBlank()){
            updatedProduct.setDescription(product.getDescription());
        }
        if (product.getPrice()!=null){
            updatedProduct.setPrice(product.getPrice());
        }
        if (product.getQuantity()!=null){
            updatedProduct.setQuantity(product.getQuantity());
        }

        checkProductFields(updatedProduct);
        productRepository.save(updatedProduct);
    }

    public void deleteProduct(User user, UUID productId) {
        Optional<Product> productExists = productRepository.findProductByPublicId(productId);
        if (!productExists.isPresent()){
            throw new IllegalStateException("No product with given id exists");
        }
        Product deletedProduct=productExists.get();
        if (!deletedProduct.getUser().getUsername().equals(user.getUsername())){
            throw new IllegalStateException("Cannot delete given product,it was created by a different user");
        }

        productRepository.delete(deletedProduct);
    }
}
