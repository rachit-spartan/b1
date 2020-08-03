package com.blockone.electronicstore.controller.customer

import com.blockone.electronicstore.service.CartItem
import com.blockone.electronicstore.service.CartService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(path = ["/api/v1"])
class CartController(
    private val cartService: CartService
) {
    private val log: Logger = LoggerFactory.getLogger(CartController::class.java)

    @PostMapping("/update_cart")
    fun updateProductInCart(
        @RequestBody addProductToCartRequest: AddProductToCartRequest
    ): ResponseEntity<HashMap<UUID, CartItem>> {
        log.info("Add/Update product in cart $addProductToCartRequest")

        val cart = cartService.addProductToCart(
            productId = addProductToCartRequest.productId,
            quantity = addProductToCartRequest.quantity ?: 1
        )
        return ResponseEntity.ok().body(cart)
    }

    @GetMapping("/cart")
    fun getCart(): ResponseEntity<HashMap<UUID, CartItem>> = ResponseEntity.ok().body(cartService.getCart())
}

data class AddProductToCartRequest(
    val productId: UUID,
    val quantity: Int?
)
