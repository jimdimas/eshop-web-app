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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    @GetMapping
    public List<ProductProjection> getAllProducts(Dictionary<String,String> searchKeys) throws BadRequestException {
        List<ProductProjection> products;
        if (searchKeys.get("category")!=null){
            String providedCategory=searchKeys.get("category");
            Category actualCategory=convertStringToCategory(providedCategory);
            products=productRepository.findProjectionByCategory(actualCategory);
        }
        else if (searchKeys.get("username")!=null){
            Optional<User> userExists = userService.getUserByUsername(searchKeys.get("username"));
            if (!userExists.isPresent()){
                throw new BadRequestException("No products found because given user doesn't exist");
            }
            products = productRepository.findProjectionByUsername(searchKeys.get("username"));
        }
        else {
            products=productRepository.findAllProjectedBy();
        }
        if (searchKeys.get("sortBy")!=null){
            String sortBy = searchKeys.get("sortBy");
            if (sortBy.equals("price")){
                return products.stream().sorted(Comparator.comparing(ProductProjection::getPrice)).collect(Collectors.toList());
            }
            if (sortBy.equals("creationDate")){
                return products.stream().sorted(Comparator.comparing(ProductProjection::getCreationDate)).collect(Collectors.toList());
            }
            throw new BadRequestException("Invalid field for sorting provided");
        }
        return products;
    }

    @PostMapping
    public JsonResponse addProduct(User user, Product product, String providedCategory) throws NotFoundException, BadRequestException, UnauthorizedException {
        Category actualCategory=convertStringToCategory(providedCategory);
        checkProductFields(product);
        Product endProduct = Product.builder()  //not setting category enum yet because it can't be serialized by json
                .productId(UUID.randomUUID())
                .user(user)
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

    public Optional<Product> getProductById(UUID productId) {  return productRepository.findByPublicId(productId); }

    public JsonResponse updateProduct(User user, UUID productId, Product product) throws BadRequestException, ConflictException, NotFoundException {
        Optional<Product> productExists = productRepository.findByPublicId(productId);
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
        Optional<Product> productExists = productRepository.findByPublicId(productId);
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
        Optional<Product> productExists = productRepository.findByPublicId(productId);
        if (!productExists.isPresent()){
            throw new NotFoundException("No product with id "+productId.toString()+" exists");
        }
        Product product=productExists.get();
        product.setQuantity(quantity);
        productRepository.save(product);
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

    private Category convertStringToCategory(String providedCategory) throws BadRequestException {
        for (Category field:Category.values()){
            if (field.name().equals(providedCategory)){
                return field;
            }
        }
        throw new BadRequestException("Invalid category provided");
    }
}
