package com.blockone.electronicstore.service.sst

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

class AdminAddUpdateDeleteProduct : SingleServiceTestBase() {

    private val objectMapper: ObjectMapper = jacksonObjectMapper().apply {
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @Test
    @WithMockUser(username = "user", password = "user", roles = ["ADMIN"])
    fun `Add new product - update quantity and price - delete the product`() {
        val createRequest =
            """
            {
                "name":"Test",
                "description":"Test",
                "currency": "USD",
                "amount": 100
            }
            """.trimIndent()

        // Send a request to add a product
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/admin/v1/products")
                .content(createRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val response = objectMapper.readValue(result.response.contentAsString, ProductResponse::class.java)
        assertThat(response.name).isEqualTo("Test")
        assertThat(response.description).isEqualTo("Test")
        assertThat(response.isAvailableForSale).isEqualTo(true)

        val productId = response.productId

        // Send Request to update the created product
        val updateRequest =
            """
            {
                "description":"Test Description"
            }
            """.trimIndent()

        val updateProductResult = mockMvc.perform(
            MockMvcRequestBuilders.put("/admin/v1/products/$productId")
                .content(updateRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val updateProductResponse = objectMapper.readValue(updateProductResult.response.contentAsString, ProductResponse::class.java)
        assertThat(updateProductResponse.name).isEqualTo("Test")
        assertThat(updateProductResponse.description).isEqualTo("Test Description")
        assertThat(updateProductResponse.isAvailableForSale).isEqualTo(true)

        // Send request to disable the product
        val disableRequest =
            """
            {
                "description":"Test Description"
            }
            """.trimIndent()

        val disableProductResult = mockMvc.perform(
            MockMvcRequestBuilders.put("/admin/v1/products/disable/$productId")
                .content(disableRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()
        val disableProductResponse = objectMapper.readValue(disableProductResult.response.contentAsString, ProductResponse::class.java)
        assertThat(disableProductResponse.name).isEqualTo("Test")
        assertThat(disableProductResponse.description).isEqualTo("Test Description")
        assertThat(disableProductResponse.isAvailableForSale).isEqualTo(false)
    }

    @Test
    fun `Add new product without admin authorization returns a 401`() {
        val createRequest =
            """
            {
                "name":"Test",
                "description":"Test",
                "currency": "USD",
                "amount": 100
            }
            """.trimIndent()

        // Send a request to add a product
        mockMvc.perform(
            MockMvcRequestBuilders.post("/admin/v1/products")
                .content(createRequest)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
            .andReturn()
    }
}

data class ProductResponse(
    val productId: UUID,
    val description: String,
    val isAvailableForSale: Boolean,
    val name: String
)
