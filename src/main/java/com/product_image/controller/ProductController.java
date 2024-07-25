package com.product_image.controller;

import com.product_image.entity.Product;
import com.product_image.entity.ProductImage;
import com.product_image.repository.IProductImageRepo;
import com.product_image.repository.IProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.net.ssl.SSLEngineResult;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    IProductRepo productRepo;

    @Autowired
    IProductImageRepo productImageRepo;

    @Value("${file.upload}")
    private String uploadDir;

    @GetMapping
    public ModelAndView products(@RequestParam(defaultValue = "0") int page) {
        Page<Product> productPage = productRepo.findAll(PageRequest.of(page, 8));
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("products", productPage);
        modelAndView.addObject("totalPages", productPage.getTotalPages());
        modelAndView.addObject("currentPage", productPage.getNumber());
        return modelAndView;
    }

    @PostMapping
    public String addProduct(@RequestParam String title, @RequestParam BigDecimal price) {
        Product product = new Product();
        product.setTitle(title);
        product.setPrice(price);
        product.setStatus(1);
        productRepo.save(product);
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public ModelAndView productsDetail(@PathVariable int id) {
        Product product = productRepo.findById(id).get();
        ModelAndView modelAndView = new ModelAndView("productDetail");
        modelAndView.addObject("product", product);
        return modelAndView;
    }

    @GetMapping("/images/change/{idP}/{idI}")
    public ResponseEntity<?> imagesChange(@PathVariable int idP, @PathVariable int idI) {
        ProductImage image1 = productImageRepo.findProductImagesByMainStatusAndProductId(1, idP);
        ProductImage image2 = productImageRepo.findById(idI).get();
        if (image1 != null) {
            image1.setMainStatus(0);
            productImageRepo.save(image1);
        }
        image2.setMainStatus(1);
        productImageRepo.save(image2);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/images/{idP}")
    public String addImage(@PathVariable int idP, @RequestParam MultipartFile[] imgFiles, RedirectAttributes redirectAttributes) throws IOException {
        if (imgFiles == null || imgFiles.length == 0 || imgFiles[0].isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "No files selected for upload.");
            return "redirect:/products/" + idP;
        }

        Product product = productRepo.findById(idP).orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + idP));

        for (MultipartFile file : imgFiles) {
            if (!file.isEmpty()) {
                ProductImage image = new ProductImage();
                String name = generateRandomFourDigitNumber() + file.getOriginalFilename();
                FileCopyUtils.copy(file.getBytes(), new File(uploadDir + "/" + name));
                image.setMainStatus(0);
                image.setFileName("/" + name);
                image.setProduct(product);
                productImageRepo.save(image);
            }
        }
        return "redirect:/products/" + idP;
    }

    @GetMapping("/images/delete/{idP}/{idI}")
    public String deleteImage(@PathVariable int idP, @PathVariable int idI) {
        Optional<ProductImage> optionalImage = productImageRepo.findById(idI);

        if (optionalImage.isPresent()) {
            ProductImage image = optionalImage.get();
            Path filePath = Paths.get(uploadDir, image.getFileName());

            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            productImageRepo.deleteById(idI);
        }
        return "redirect:/products/" + idP;
    }

    public static int generateRandomFourDigitNumber() {
        Random random = new Random();
        return 1000 + random.nextInt(9000);
    }
}
