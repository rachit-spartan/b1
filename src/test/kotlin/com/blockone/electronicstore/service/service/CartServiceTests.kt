package com.blockone.electronicstore.service.service

import com.blockone.electronicstore.db.model.BundleDeal
import com.blockone.electronicstore.db.model.DiscountDeal
import com.blockone.electronicstore.db.model.Product
import com.blockone.electronicstore.repository.ProductRepository
import com.blockone.electronicstore.service.AbstractSessionTest
import com.blockone.electronicstore.service.CartItem
import com.blockone.electronicstore.service.CartService
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class CartServiceTests : AbstractSessionTest() {

    private lateinit var cartService: CartService
    private val productRepositoryMock: ProductRepository = mock()
    private val mainProductId = UUID.randomUUID()
    private val bundledProductId = UUID.randomUUID()
    private val discountedProductId = UUID.randomUUID()
    private val mainProduct = Product(
        productId = mainProductId,
        name = "Main",
        price = Money.of(CurrencyUnit.USD, BigDecimal(100)),
        description = ""
    )
    private val bundledProduct = Product(
        productId = bundledProductId,
        name = "bundled",
        price = Money.of(CurrencyUnit.USD, BigDecimal(5)),
        description = ""
    )
    private val discountedProduct = Product(
        productId = discountedProductId,
        name = "bundled",
        price = Money.of(CurrencyUnit.USD, BigDecimal(5)),
        description = ""
    )
    private val bundleDeal = BundleDeal(
        name = "bundleDeal",
        mainProduct = mainProduct,
        freeProduct = bundledProduct
    )
    private val discountedDeal = DiscountDeal(
        name = "Discount",
        discount = BigDecimal.TEN,
        mainProduct = mainProduct,
        discountedProduct = discountedProduct
    )

    @BeforeEach
    fun setup() {
        cartService = CartService(
            productRepository = productRepositoryMock
        )
    }

    @Nested
    inner class BundleDealTests {

        @BeforeEach
        fun mockRepositories() {
            whenever(productRepositoryMock.findById(mainProductId))
                .thenReturn(Optional.of(mainProduct.copy(bundleDeals = listOf(bundleDeal))))
            whenever(productRepositoryMock.findById(bundledProductId))
                .thenReturn(Optional.of(bundledProduct.copy(availableForBundleDeals = listOf(bundleDeal))))
        }

        @Test
        fun `When no bundled product in cart and a main product is added`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(mainProductId, 1)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 1,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(100).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[bundledProductId]).isEqualTo(
                CartItem(
                    productId = bundledProductId,
                    discountedQuantity = 0,
                    normalQuantity = 0,
                    bundledQuantity = 1,
                    totalPrice = BigDecimal.ZERO,
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }

        @Test
        fun `When one bundled product in cart and one main product is added`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(bundledProductId, 1)
            endRequest()
            startRequest()
            cartService.addProductToCart(mainProductId, 1)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 1,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(100).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[bundledProductId]).isEqualTo(
                CartItem(
                    productId = bundledProductId,
                    discountedQuantity = 0,
                    normalQuantity = 0,
                    bundledQuantity = 1,
                    totalPrice = BigDecimal(0).setScale(2),
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }

        @Test
        fun `When 3 bundled product in cart and 5 main products are added`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(bundledProductId, 3)
            endRequest()
            startRequest()
            cartService.addProductToCart(mainProductId, 5)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 5,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(500).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[bundledProductId]).isEqualTo(
                CartItem(
                    productId = bundledProductId,
                    discountedQuantity = 0,
                    normalQuantity = 0,
                    bundledQuantity = 5,
                    totalPrice = BigDecimal(0).setScale(2),
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }

        @Test
        fun `When 5 bundled product in cart and 3 main products are added`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(bundledProductId, 5)
            endRequest()
            startRequest()
            cartService.addProductToCart(mainProductId, 3)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 3,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(300).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[bundledProductId]).isEqualTo(
                CartItem(
                    productId = bundledProductId,
                    discountedQuantity = 0,
                    normalQuantity = 2,
                    bundledQuantity = 3,
                    totalPrice = BigDecimal(10).setScale(2),
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }

        @Test
        fun `When 10 bundled product in cart (3 paid & 7 free) & quantity of main products are changed to 8`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(bundledProductId, 10)
            endRequest()
            startRequest()
            cartService.addProductToCart(mainProductId, 7)
            endRequest()
            startRequest()
            cartService.addProductToCart(mainProductId, 8)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 8,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(800).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[bundledProductId]).isEqualTo(
                CartItem(
                    productId = bundledProductId,
                    discountedQuantity = 0,
                    normalQuantity = 2,
                    bundledQuantity = 8,
                    totalPrice = BigDecimal(10).setScale(2),
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }

        @Test
        fun `When 10 bundled product in cart (3 paid & 7 free) & quantity of main products is changed to 15`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(bundledProductId, 10)
            endRequest()
            startRequest()
            cartService.addProductToCart(mainProductId, 7)
            endRequest()
            startRequest()
            cartService.addProductToCart(mainProductId, 15)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 15,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(1500).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[bundledProductId]).isEqualTo(
                CartItem(
                    productId = bundledProductId,
                    discountedQuantity = 0,
                    normalQuantity = 0,
                    bundledQuantity = 15,
                    totalPrice = BigDecimal.ZERO.setScale(2),
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }
    }

    @Nested
    inner class DiscountDealTests {
        @BeforeEach
        fun mockRepositories() {
            whenever(productRepositoryMock.findById(mainProductId))
                .thenReturn(Optional.of(mainProduct.copy(discountDeals = listOf(discountedDeal))))
            whenever(productRepositoryMock.findById(discountedProductId))
                .thenReturn(Optional.of(discountedProduct.copy(availableForDiscountDeals = listOf(discountedDeal))))
        }

        @Test
        fun `When no discounted product in cart and a main product is added`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(mainProductId, 1)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(1)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 1,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(100).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertNull(cart[discountedProductId])
            endSession()
        }

        @Test
        fun `When one discounted product in cart and one main product is added`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(discountedProductId, 1)
            endRequest()
            startRequest()
            cartService.addProductToCart(mainProductId, 1)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 1,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(100).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[discountedProductId]).isEqualTo(
                CartItem(
                    productId = discountedProductId,
                    discountedQuantity = 1,
                    normalQuantity = 0,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(4.5).setScale(3),
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }

        @Test
        fun `When 3 discounted product in cart and 5 main products are added`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(discountedProductId, 3)
            endRequest()
            startRequest()
            cartService.addProductToCart(mainProductId, 5)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 5,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(500).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[discountedProductId]).isEqualTo(
                CartItem(
                    productId = discountedProductId,
                    discountedQuantity = 3,
                    normalQuantity = 0,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(13.5).setScale(3),
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }

        @Test
        fun `When 5 discounted product in cart and 3 main products are added`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(discountedProductId, 5)
            endRequest()
            startRequest()
            cartService.addProductToCart(mainProductId, 3)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 3,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(300).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[discountedProductId]).isEqualTo(
                CartItem(
                    productId = discountedProductId,
                    discountedQuantity = 3,
                    normalQuantity = 2,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(23.5).setScale(3),
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }

        @Test
        fun `When 5 main product in cart and 3 discounted products are added`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(mainProductId, 5)
            endRequest()
            startRequest()
            cartService.addProductToCart(discountedProductId, 3)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 5,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(500).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[discountedProductId]).isEqualTo(
                CartItem(
                    productId = discountedProductId,
                    discountedQuantity = 3,
                    normalQuantity = 0,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(13.5).setScale(3),
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }

        @Test
        fun `When 3 main product in cart and 5 discounted products are added`() {
            // Arrange
            startSession()
            startRequest()
            cartService.addProductToCart(mainProductId, 3)
            endRequest()
            startRequest()
            cartService.addProductToCart(discountedProductId, 5)
            endRequest()

            // Act & Assert
            val cart = cartService.getCart()
            assertThat(cart.size).isEqualTo(2)
            assertThat(cart[mainProductId]).isEqualTo(
                CartItem(
                    productId = mainProductId,
                    discountedQuantity = 0,
                    normalQuantity = 3,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(300).setScale(2),
                    normalPricePerItem = BigDecimal(100).setScale(2)
                )
            )
            assertThat(cart[discountedProductId]).isEqualTo(
                CartItem(
                    productId = discountedProductId,
                    discountedQuantity = 3,
                    normalQuantity = 2,
                    bundledQuantity = 0,
                    totalPrice = BigDecimal(23.5).setScale(3),
                    normalPricePerItem = BigDecimal(5).setScale(2)
                )
            )
            endSession()
        }
    }
}
