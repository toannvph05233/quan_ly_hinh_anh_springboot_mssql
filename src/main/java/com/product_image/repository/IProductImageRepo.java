package com.product_image.repository;

import com.product_image.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProductImageRepo extends JpaRepository<ProductImage, Integer> {
    ProductImage findProductImagesByMainStatusAndProductId(int status, int idP);
}
