package com.blockone.electronicstore.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer

@Configuration
@EnableRedisHttpSession
class SessionConfig : AbstractHttpSessionApplicationInitializer()

@Configuration
class RedisConfiguration {
    @Bean
    fun sessionRedisTemplate(
        connectionFactory: RedisConnectionFactory?
    ): RedisTemplate<Any, Any>? {
        val template = RedisTemplate<Any, Any>()
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.setDefaultSerializer(GenericJackson2JsonRedisSerializer())
        template.setConnectionFactory(connectionFactory!!)
        return template
    }
}
