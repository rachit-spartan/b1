package com.blockone.electronicstore.service.sst

import com.blockone.electronicstore.db.model.BundleDeal
import com.blockone.electronicstore.db.model.DiscountDeal
import com.blockone.electronicstore.db.model.Product
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.joda.money.CurrencyUnit.USD
import org.joda.money.Money
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.util.*

class AddAndUpdateCartSST : SingleServiceTestBase() {

    private val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }
    private val mainProductId = UUID.randomUUID()
    private val bundledProductId = UUID.randomUUID()
    private val mainProductId2 = UUID.randomUUID()
    private val discountedProductId = UUID.randomUUID()

    @BeforeEach
    fun `Persist products in database`() {
        val mainProduct = productRepository.save(
            Product(
                productId = mainProductId,
                name = "Test Main Product",
                description = "",
                price = Money.of(USD, BigDecimal(10))
            )
        )
        val bundledProduct = productRepository.save(
            Product(
                productId = bundledProductId,
                name = "Test Bundled Product",
                description = "",
                price = Money.of(USD, BigDecimal(10))
            )
        )
        val mainProduct2 = productRepository.save(
            Product(
                productId = mainProductId2,
                name = "Test Main Product 2",
                description = "",
                price = Money.of(USD, BigDecimal(10))
            )
        )
        val discountedProduct = productRepository.save(
            Product(
                productId = discountedProductId,
                name = "Test Discounted Product",
                description = "",
                price = Money.of(USD, BigDecimal(10))
            )
        )
        bundleDealRepository.save(
            BundleDeal(
                name = "Bundle deal", mainProduct = mainProduct, freeProduct = bundledProduct
            )
        )
        discountDealRepository.save(
            DiscountDeal(
                name = "Discount Deal",
                discount = BigDecimal(10),
                mainProduct = mainProduct2,
                discountedProduct = discountedProduct
            )
        )
    }
    @Test
    fun `Add normal product, bundled product, discounted product, main product for discounted`() {
        // region Add a normal product and a bundled product is automatically added
        // Add a normal product and a bundled product should be added automatically
        val addNormalProduct =
            """
            {
                "productId": "$mainProductId"
            }
            """.trimIndent()
        val cartAfterFirstReq = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/update_cart")
                .content(addNormalProduct)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val mainProductInCart = objectMapper.readTree(cartAfterFirstReq.response.contentAsString).let {
            objectMapper.readValue(it[mainProductId.toString()].toString(), Item::class.java)
        }
        val bundledProductInCart = objectMapper.readTree(cartAfterFirstReq.response.contentAsString).let {
            objectMapper.readValue(it[bundledProductId.toString()].toString(), Item::class.java)
        }
        assertThat(bundledProductInCart.bundledQuantity).isEqualTo(1)
        assertThat(bundledProductInCart.normalQuantity).isEqualTo(0)
        assertThat(bundledProductInCart.discountedQuantity).isEqualTo(0)
        assertThat(bundledProductInCart.totalPrice).isEqualTo(BigDecimal.ZERO)

        assertThat(mainProductInCart.bundledQuantity).isEqualTo(0)
        assertThat(mainProductInCart.normalQuantity).isEqualTo(1)
        assertThat(mainProductInCart.discountedQuantity).isEqualTo(0)
        assertThat(mainProductInCart.totalPrice).isEqualTo(BigDecimal.TEN.setScale(1))
        // endregion

        // region Adding a discounted product
        // Add a discounted product first
        val addDiscountedProduct =
            """
            {
                "productId": "$discountedProductId"
            }
            """.trimIndent()
        val cartAfterSecondReq = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/update_cart")
                .content(addDiscountedProduct)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val discountedProductInitially = objectMapper.readTree(cartAfterSecondReq.response.contentAsString).let {
            objectMapper.readValue(it[discountedProductId.toString()].toString(), Item::class.java)
        }
        assertThat(discountedProductInitially.bundledQuantity).isEqualTo(0)
        assertThat(discountedProductInitially.normalQuantity).isEqualTo(1)
        assertThat(discountedProductInitially.discountedQuantity).isEqualTo(0)
        assertThat(discountedProductInitially.totalPrice).isEqualTo(BigDecimal.TEN.setScale(1))
        // endregion

        // region Add a normal product for an existing discounted product
        // Add a product for which the discounted product exists in the cart and the discounted
        // product's price is decreased automatically
        val addNormalProduct2 =
            """
            {
                "productId": "$mainProductId2"
            }
            """.trimIndent()
        val cartResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/update_cart")
                .content(addNormalProduct2)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        val cart = objectMapper.readTree(cartResponse.response.contentAsString)
        val mainProduct2InCart = objectMapper.readValue(cart[mainProductId2.toString()].toString(), Item::class.java)
        val discountedProduct = objectMapper.readValue(cart[discountedProductId.toString()].toString(), Item::class.java)

        assertThat(mainProduct2InCart.bundledQuantity).isEqualTo(0)
        assertThat(mainProduct2InCart.normalQuantity).isEqualTo(1)
        assertThat(mainProduct2InCart.discountedQuantity).isEqualTo(0)
        assertThat(mainProduct2InCart.totalPrice).isEqualTo(BigDecimal.TEN.setScale(1))

        assertThat(discountedProduct.bundledQuantity).isEqualTo(0)
        assertThat(discountedProduct.normalQuantity).isEqualTo(0)
        assertThat(discountedProduct.discountedQuantity).isEqualTo(1)
        assertThat(discountedProduct.totalPrice).isEqualTo(BigDecimal(9).setScale(1))
        // endregion
    }
}
data class Item(
    val productId: UUID,
    val discountedQuantity: Int,
    val normalQuantity: Int,
    val bundledQuantity: Int,
    val totalPrice: BigDecimal
)
