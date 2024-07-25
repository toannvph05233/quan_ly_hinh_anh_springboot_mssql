package com.product_image.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "tbl_product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private int id;

    @Column(name = "_title", nullable = false, columnDefinition = "nvarchar(100) default 'no name'")
    private String title;

    @Column(name = "_price", nullable = false, columnDefinition = "DECIMAL(18,0) default 0")
    private BigDecimal price;

    @Column(name = "_status", nullable = false, columnDefinition = "int default 1")
    private int status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> images;

    public String getImageUrl() {
        if (images != null && !images.isEmpty()) {
            for (ProductImage i:images) {
                if (i.getMainStatus() == 1) return  i.getFileName();
            }
        }
        return null;
    }

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }
}

