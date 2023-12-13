package com.jimdimas.api.product;

import com.jimdimas.api.exception.BadRequestException;
import com.jimdimas.api.exception.ConflictException;
import com.jimdimas.api.exception.NotFoundException;
import com.jimdimas.api.exception.UnauthorizedException;
import com.jimdimas.api.user.User;
import com.jimdimas.api.user.UserService;
import com.jimdimas.api.util.JsonResponse;
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
    public List<ProductProjection> getAllProducts(){
        return productRepository.findAllProjectedBy();
    }

    @PostMapping
    public JsonResponse addProduct(User user, Product product, String providedCategory) throws NotFoundException, BadRequestException, UnauthorizedException {
        Optional<User> uploadUser = userService.getUserByUsername(user, user.getUsername());
        if (!uploadUser.isPresent()){
            throw new NotFoundException("No user with username: "+user.getUsername()+" exists.");
        }
        Category actualCategory=null;
        for (Category field:Category.values()){
            if (field.name().equals(providedCategory)){
                actualCategory=field;
                break;
            }
        }
        if (actualCategory==null){
            throw new BadRequestException("Invalid product category provided");
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
        return JsonResponse.builder().message("Product created successfully.").build();
    }

    public Optional<Product> getProductById(UUID productId) {  return productRepository.findProductByPublicId(productId); }


    public Optional<List<ProductProjection>> getUserProducts(String username) {
        return productRepository.findProductsProjectionByUserUsername(username);
    }

    private void checkProductFields(Product product) throws BadRequestException {
        if (product.getPrice()<=0 || product.getPrice()>1000){
            throw new BadRequestException("Invalid product price");
        }
        if (product.getName()==null || product.getName().isBlank() || product.getName().length()<2){
            throw new BadRequestException("Invalid product name");
        }
        if (product.getQuantity()<0 || product.getQuantity()>1000){
            throw new BadRequestException("Invalid product capacity");
        }
        if (product.getDescription().length()>100){
            throw new BadRequestException("Product description can't be more than 100 characters");
        }
    }

    public JsonResponse updateProduct(User user, UUID productId, Product product) throws BadRequestException, ConflictException, NotFoundException {
        Optional<Product> productExists = productRepository.findProductByPublicId(productId);
        if (!productExists.isPresent()){
            throw new NotFoundException("No product with given id exists");
        }
        Product updatedProduct = productExists.get();
        if (!user.getUsername().equals(updatedProduct.getUser().getUsername())){
            throw new ConflictException("Cannot update given product,it was created by a different user");
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
        return JsonResponse.builder().message("Product with id : "+productId.toString()+" updated successfully.").build();
    }

    public JsonResponse deleteProduct(User user, UUID productId) throws NotFoundException, ConflictException {
        Optional<Product> productExists = productRepository.findProductByPublicId(productId);
        if (!productExists.isPresent()){
            throw new NotFoundException("No product with given id exists");
        }
        Product deletedProduct=productExists.get();
        if (!deletedProduct.getUser().getUsername().equals(user.getUsername())){
            throw new ConflictException("Cannot delete given product,it was created by a different user");
        }

        productRepository.delete(deletedProduct);
        return JsonResponse.builder().message("Product with id : "+productId.toString()+" deleted successfully.").build();
    }

    public void updateProductQuantity(UUID productId,Integer quantity) throws NotFoundException, BadRequestException {
        Optional<Product> productExists = productRepository.findProductByPublicId(productId);
        if (!productExists.isPresent()){
            throw new NotFoundException("No product with id "+productId.toString()+" exists");
        }
        Product product=productExists.get();
        product.setQuantity(quantity);
        productRepository.save(product);
    }
}
