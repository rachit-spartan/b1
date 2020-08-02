package com.blockone.electronicstore.controller.admin

import com.blockone.electronicstore.db.model.Product
import com.blockone.electronicstore.service.ProductService
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping(path = ["/admin/v1"])
class ProductController(
    private val productService: ProductService
) {
    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    @GetMapping("/products")
    fun getAllProducts(): ResponseEntity<List<Product>?> {
        log.info("Get all products")
        val products = productService.getAllProducts()
        return ResponseEntity.ok().body(products)
    }

    @GetMapping("/products/{id}")
    fun getOneProduct(@PathVariable("id") id: UUID): ResponseEntity<Product> {
        log.info("Get product for id: $id")
        val product = productService.getProductById(id)
        return ResponseEntity.ok().body(product)
    }

    @PostMapping("/products")
    fun createNewProduct(@RequestBody createProductRequestBody: CreateProductRequestBody): ResponseEntity<Product> {
        log.info("Create new product for request body: $createProductRequestBody")
        val product = productService.storeProduct(
            Product(
                productId = createProductRequestBody.productId,
                name = createProductRequestBody.name,
                description = createProductRequestBody.description,
                price = Money.of(CurrencyUnit.of(createProductRequestBody.currency), createProductRequestBody.amount)
            )
        )
        return ResponseEntity.ok().body(product)
    }

    @PutMapping("/products/{id}")
    fun updateProduct(
        @PathVariable("id") id: UUID,
        @RequestBody updateProductRequestBody: UpdateProductRequestBody
    ): ResponseEntity<Product> {
        log.info("UpdateProductRequest for id: $id and body $updateProductRequestBody")
        val updatedProduct = productService.updateProduct(
            id = id,
            description = updateProductRequestBody.description,
            price = updateProductRequestBody.amount
        )
        return ResponseEntity.ok().body(updatedProduct)
    }

    @PutMapping("/products/disable/{id}")
    fun disableProduct(
        @PathVariable("id") id: UUID
    ): ResponseEntity<Product> {
        log.info("Disable product for id: $id")
        val updatedProduct = productService.disableProduct(id = id)
        return ResponseEntity.ok().body(updatedProduct)
    }
}
data class CreateProductRequestBody(
    val productId: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val amount: BigDecimal,
    val currency: String
)
data class UpdateProductRequestBody(
    val description: String?,
    val amount: BigDecimal?
)
