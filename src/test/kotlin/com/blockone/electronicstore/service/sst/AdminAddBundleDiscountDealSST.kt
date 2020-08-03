package com.blockone.electronicstore.service.sst

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

class AdminAddBundleDiscountDeal : SingleServiceTestBase() {

    private val mainProductId = UUID.randomUUID()
    private val bundledProductId = UUID.randomUUID()
    private val mainProductId2 = UUID.randomUUID()
    private val discountedProductId = UUID.randomUUID()

    @Test
    @WithMockUser(username = "user", password = "user", roles = ["ADMIN"])
    fun `Add 4 products - Add new bundle deal - Add new discount deal`() {
        listOf(mainProductId, bundledProductId, mainProductId2, discountedProductId).map {
            val createRequest =
                """
            {
                "productId":"$it",
                "name":"Test$it",
                "description":"Test",
                "currency":"USD",
                "amount":100
            }
                """.trimIndent()

            // Send a request to add a product
            mockMvc.perform(
                MockMvcRequestBuilders.post("/admin/v1/products")
                    .content(createRequest)
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        }
        val createBundleDealRequest =
            """
            {
                "name":"Bundle",
                "mainProductId": "$mainProductId",
                "freeProductId": "$bundledProductId"
            }
            """.trimIndent()

        // Send a request to create a bundle deal
        val createBundleDealResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/admin/v1/bundle_deal")
                .content(createBundleDealRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val bundleDealResponse = objectMapper.readValue(createBundleDealResponse.response.contentAsString, BundleDealResponse::class.java)
        assertThat(bundleDealResponse.name).isEqualTo("Bundle")
        assertThat(bundleDealResponse.mainProduct.productId).isEqualTo(mainProductId)
        assertThat(bundleDealResponse.freeProduct.productId).isEqualTo(bundledProductId)

        val createDiscountDealRequest =
            """
            {
                "name":"Discount",
                "discount": 20,
                "mainProductId": "$mainProductId2",
                "discountedProductId": "$discountedProductId"
            }
            """.trimIndent()

        // Send a request to create a discount deal
        val createDiscountDealResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/admin/v1/discount_deal")
                .content(createDiscountDealRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val discountDealResponse = objectMapper.readValue(createDiscountDealResponse.response.contentAsString, DiscountDealResponse::class.java)
        assertThat(discountDealResponse.name).isEqualTo("Discount")
        assertThat(discountDealResponse.mainProduct.productId).isEqualTo(mainProductId2)
        assertThat(discountDealResponse.discountedProduct.productId).isEqualTo(discountedProductId)
    }
}
data class BundleDealResponse(
    val id: UUID,
    val name: String,
    val mainProduct: ProductIds,
    val freeProduct: ProductIds
)
data class DiscountDealResponse(
    val id: UUID,
    val name: String,
    val mainProduct: ProductIds,
    val discountedProduct: ProductIds
)
data class ProductIds(
    val productId: UUID
)
