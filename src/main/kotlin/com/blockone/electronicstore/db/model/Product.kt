package com.blockone.electronicstore.db.model

import com.fasterxml.jackson.annotation.JsonBackReference
import org.joda.money.Money
import java.util.UUID
import javax.persistence.*

@Entity
@Table(name = "product")
data class Product(
    @Column
    @Id
    val productId: UUID = UUID.randomUUID(),

    @Column
    val name: String,

    @Column
    val price: Money,

    @Column
    val description: String,

    @OneToMany(mappedBy = "mainProduct", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonBackReference("main_product")
    val discountDeals: List<DiscountDeal> = listOf(),

    @OneToMany(mappedBy = "discountedProduct", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonBackReference("discounted_product")
    val availableForDiscountDeals: List<DiscountDeal> = listOf(),

    @OneToMany(mappedBy = "mainProduct", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonBackReference("main_product")
    val bundleDeals: List<BundleDeal> = listOf(),

    @OneToMany(mappedBy = "freeProduct", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonBackReference("free_product")
    val availableForBundleDeals: List<BundleDeal> = listOf(),

    @Column
    val isAvailableForSale: Boolean = true
) {
    override fun toString() = "Product Id: $productId"
}
