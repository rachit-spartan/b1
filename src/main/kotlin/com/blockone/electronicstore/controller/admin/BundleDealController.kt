package com.blockone.electronicstore.controller.admin

import com.blockone.electronicstore.db.model.BundleDeal
import com.blockone.electronicstore.service.BundleDealService
import com.blockone.electronicstore.service.ProductService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping(path = ["/admin/v1"])
class BundleDealController(
    private val productService: ProductService,
    private val bundleDealService: BundleDealService
) {
    private val log: Logger = LoggerFactory.getLogger(BundleDealController::class.java)

    @PostMapping("/bundle_deal")
    fun createNewDeal(@RequestBody createBundleRequestBody: CreateBundleRequestBody): ResponseEntity<BundleDeal> {
        log.info("Creating a new bundle deal request $createBundleRequestBody")
        val discountDeal = bundleDealService.createBundleDeal(
            name = createBundleRequestBody.name,
            mainProductId = createBundleRequestBody.mainProductId,
            discountedProductId = createBundleRequestBody.freeProductId
        )
        return ResponseEntity.ok().body(discountDeal)
    }
}
data class CreateBundleRequestBody(
    val name: String,
    val mainProductId: UUID,
    val freeProductId: UUID
)
