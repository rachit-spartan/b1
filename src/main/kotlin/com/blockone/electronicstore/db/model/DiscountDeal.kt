package com.blockone.electronicstore.db.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "discount_deals")
data class DiscountDeal(
    @Column
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column
    val name: String,

    @Column
    val discount: BigDecimal,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_product_id")
    @JsonManagedReference("main_product")
    val mainProduct: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discounted_product_id")
    @JsonManagedReference("discounted_product")
    val discountedProduct: Product
)
