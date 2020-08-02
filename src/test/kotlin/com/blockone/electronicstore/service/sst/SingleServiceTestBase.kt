package com.blockone.electronicstore.service.sst

import com.blockone.electronicstore.repository.BundleDealRepository
import com.blockone.electronicstore.repository.DiscountDealRepository
import com.blockone.electronicstore.repository.ProductRepository
import com.nhaarman.mockitokotlin2.reset
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.internal.util.MockUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureEmbeddedDatabase
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
abstract class SingleServiceTestBase {

    @Autowired
    private lateinit var ctx: ApplicationContext

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var productRepository: ProductRepository

    @Autowired
    protected lateinit var bundleDealRepository: BundleDealRepository

    @Autowired
    protected lateinit var discountDealRepository: DiscountDealRepository

    @BeforeEach
    fun setup() {
        fun resetContext() {
            ctx.beanDefinitionNames.forEach {
                val bean = ctx.getBean(it)
                if (MockUtil.isMock(bean)) {
                    reset(bean)
                }
            }

            productRepository.deleteAll()
            bundleDealRepository.deleteAll()
            discountDealRepository.deleteAll()
        }
    }
}
