package com.blockone.electronicstore.controller.admin

import com.blockone.electronicstore.db.model.DiscountDeal
import com.blockone.electronicstore.service.DiscountDealService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal
import java.util.*

@RestController
@RequestMapping(path = ["/admin/v1"])
class DiscountDealController(
    private val discountDealService: DiscountDealService
) {
    private val log: Logger = LoggerFactory.getLogger(DiscountDealController::class.java)

    @PostMapping("/discount_deal")
    fun createNewDeal(@RequestBody createDiscountRequestBody: CreateDiscountRequestBody): ResponseEntity<DiscountDeal> {
        log.info("Creating a new discount deal request body $createDiscountRequestBody")
        val discountDeal = discountDealService.createDeal(
            name = createDiscountRequestBody.name,
            discount = createDiscountRequestBody.discount,
            mainProductId = createDiscountRequestBody.mainProductId,
            discountedProductId = createDiscountRequestBody.discountedProductId
        )
        return ResponseEntity.ok().body(discountDeal)
    }
}
data class CreateDiscountRequestBody(
    val name: String,
    val discount: BigDecimal,
    val mainProductId: UUID,
    val discountedProductId: UUID
)
