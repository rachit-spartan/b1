package com.blockone.electronicstore.service

import com.blockone.electronicstore.db.model.Product
import com.blockone.electronicstore.repository.ProductRepository
import com.blockone.electronicstore.util.SupportException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.lang.Integer.max
import java.math.BigDecimal
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.min

// @Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
// @Transactional
@Service
class CartService(
    private val productRepository: ProductRepository
) {

    private val cart = HashMap<UUID, CartItem>()
    private val log: Logger = LoggerFactory.getLogger(CartService::class.java)

    fun addProductToCart(
        productId: UUID,
        quantity: Int
    ): HashMap<UUID, CartItem> {
        log.info("Adding product $productId with quantity = $quantity")

        val product = productRepository.findById(productId).takeIf { it.isPresent }?.get()
            ?: throw SupportException("Trying to add a non existent product")
        if (cart[productId] == null) {
            CartItem(
                productId = productId,
                totalPrice = product.price.amount * BigDecimal(quantity),
                discountedQuantity = 0,
                normalQuantity = quantity,
                bundledQuantity = 0,
                normalPricePerItem = product.price.amount
            )
        } else {
            cart[productId]!!.copy(
                totalPrice = product.price.amount * BigDecimal(quantity),
                normalQuantity = quantity
            )
        }.let { cart[productId] = it }

        adjustCartProducts(product, quantity)

        return cart
    }

    fun adjustCartProducts(
        product: Product,
        quantity: Int
    ) {
        log.info("Adjusting items in the cart")

        adjustBundleDeals(product, quantity)

        adjustDiscountForExistingItems(product, quantity)

        adjustDiscountForNewlyAddedItem(product, quantity)
    }
    /**
     * if the product has any discount deals products associated and the discount
     * deal product is in cart, add a discount for that product
     */
    fun adjustDiscountForExistingItems(
        product: Product,
        quantity: Int
    ) {
        product.discountDeals.map { discountDeal ->
            if (cart[discountDeal.discountedProduct.productId] != null) {
                log.info("Product $product has a discount deal available: $discountDeal")

                val oldDiscountedItem = cart[discountDeal.discountedProduct.productId]!!
                val oldTotal = oldDiscountedItem.discountedQuantity + oldDiscountedItem.normalQuantity
                val newDiscountedQuantity = min(quantity, oldTotal)
                val newNormalQuantity = max((oldTotal - newDiscountedQuantity), 0)

                oldDiscountedItem.let {
                    it.copy(
                        totalPrice = it.normalPricePerItem.multiply(BigDecimal(newNormalQuantity)) +
                            it.normalPricePerItem.multiply(BigDecimal(newDiscountedQuantity))
                                .multiply(BigDecimal.ONE.minus(discountDeal.discount.divide(BigDecimal(100)))),
                        discountedQuantity = newDiscountedQuantity,
                        normalQuantity = newNormalQuantity
                    ).also { cart[it.productId] = it }
                }
            }
        }
    }

    /**
     * if the product is a discounted products for any product in the cart already,
     * change the price of the given product
     */
    fun adjustDiscountForNewlyAddedItem(
        product: Product,
        quantity: Int
    ) {
        product.availableForDiscountDeals.map { discountDeal ->
            log.info("Product $product is a part of discount deal: $discountDeal")

            if (cart[discountDeal.mainProduct.productId] != null) {
                val mainProductNormalQuantity = cart[discountDeal.mainProduct.productId]!!.normalQuantity
                val discountedQuantity = min(mainProductNormalQuantity, quantity)
                val productCartItem = cart[product.productId]!!
                val discountedProductNormalQuantity = max(quantity - discountedQuantity, 0)

                productCartItem.let {
                    it.copy(
                        totalPrice = productCartItem.normalPricePerItem.multiply(BigDecimal(discountedQuantity)).multiply(BigDecimal.ONE.minus(discountDeal.discount.divide(BigDecimal(100)))) +
                            productCartItem.normalPricePerItem.multiply(BigDecimal(discountedProductNormalQuantity)),
                        discountedQuantity = discountedQuantity,
                        normalQuantity = discountedProductNormalQuantity
                    ).also { cart[it.productId] = it }
                }
            }
        }
    }

    /**
     * if the product has bundle deals associated, add that product to cart
     */
    fun adjustBundleDeals(
        product: Product,
        quantity: Int
    ) {
        product.bundleDeals.map { bundleDeal ->
            if (cart[bundleDeal.freeProduct.productId] == null) {
                log.info("No existing bundled item found in cart for product $product. Adding a new bundled product")
                CartItem(
                    productId = bundleDeal.freeProduct.productId,
                    totalPrice = BigDecimal.ZERO,
                    discountedQuantity = 0,
                    normalQuantity = 0,
                    bundledQuantity = quantity,
                    normalPricePerItem = bundleDeal.freeProduct.price.amount
                )
            } else {
                val oldBundledItem = cart[bundleDeal.freeProduct.productId]!!

                log.info("Existing bundled product $oldBundledItem found in cart for product $product. Adding a new bundled product")

                val oldTotal = oldBundledItem.bundledQuantity + oldBundledItem.normalQuantity
                val newNormalQuantity = max((oldTotal - quantity), 0) // if the new added quantity > oldTotal then make it 0

                oldBundledItem.let {
                    it.copy(
                        totalPrice = it.normalPricePerItem.multiply(BigDecimal(newNormalQuantity)),
                        bundledQuantity = quantity,
                        normalQuantity = newNormalQuantity
                    )
                }
            }.let { cart[it.productId] = it }
        }
    }

    fun getCart(): HashMap<UUID, CartItem> = cart
}

data class CartItem(
    val productId: UUID,
    val discountedQuantity: Int,
    val normalQuantity: Int,
    val bundledQuantity: Int,
    val totalPrice: BigDecimal,
    val normalPricePerItem: BigDecimal
)
