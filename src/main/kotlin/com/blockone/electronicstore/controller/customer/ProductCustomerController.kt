package com.blockone.electronicstore.controller.customer

import com.blockone.electronicstore.db.model.Product
import com.blockone.electronicstore.service.ProductService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(path = ["/api/v1"])
class ProductCustomerController(
    private val productService: ProductService
) {
    private val log: Logger = LoggerFactory.getLogger(ProductCustomerController::class.java)

    @GetMapping("/products")
    fun getAllProducts(): ResponseEntity<List<Product>?> {
        log.info("Getting all products for customer")

        val products = productService.getAllProducts()
        return ResponseEntity.ok().body(products)
    }

    @GetMapping("/products/{id}")
    fun getOneProduct(@PathVariable("id") id: UUID): ResponseEntity<Product> {
        log.info("Getting product $id for customer")

        val product = productService.getProductById(id)
        return ResponseEntity.ok().body(product)
    }
}
