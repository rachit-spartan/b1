package com.blockone.electronicstore.service.service

import com.blockone.electronicstore.db.model.Product
import com.blockone.electronicstore.repository.ProductRepository
import com.blockone.electronicstore.service.ProductService
import com.nhaarman.mockitokotlin2.any
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

class ProductServiceTests {
    private lateinit var productService: ProductService
    private val productRepositoryMock: ProductRepository = mock()
    private val firstDummyProduct = Product(
        productId = UUID.randomUUID(),
        name = "FirstTestProduct",
        description = "This is first test product",
        price = Money.of(CurrencyUnit.USD, BigDecimal.TEN)
    )
    private val secondDummyProduct = Product(
        productId = UUID.randomUUID(),
        name = "SecondTestProduct",
        description = "This is second test product",
        price = Money.of(CurrencyUnit.EUR, BigDecimal.ONE)
    )
    @BeforeEach
    fun setup() {
        productService = ProductService(
            productRepository = productRepositoryMock
        )
    }

    @Nested
    inner class GetAllProductsTests {
        @Test
        fun `Given two products in db, we return both of them when asked for all products`() {
            whenever(productRepositoryMock.findAll()).thenReturn(listOf(firstDummyProduct, secondDummyProduct))
            val products = productService.getAllProducts()
            assertThat(products!!.size).isEqualTo(2)
        }

        @Test
        fun `Given no product in db, we return empty list when asked for all products`() {
            whenever(productRepositoryMock.findAll()).thenReturn(listOf())
            val products = productService.getAllProducts()
            assertThat(products!!.size).isEqualTo(0)
        }
    }

    @Nested
    inner class GetSingleProductTests {
        @Test
        fun `Given no product in db, we return empty list when asked for products`() {
            whenever(productRepositoryMock.findById(any())).thenReturn(Optional.empty())
            val product = productService.getProductById(UUID.randomUUID())
            assertNull(product)
        }
    }
}
