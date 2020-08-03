package com.blockone.electronicstore.service

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpSession
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

abstract class AbstractSessionProvider {
    private var session: MockHttpSession? = null
    private var request: MockHttpServletRequest? = null
    protected fun startSession() {
        session = MockHttpSession()
    }

    protected fun endSession() {
        session!!.clearAttributes()
        session = null
    }

    protected fun startRequest() {
        request = MockHttpServletRequest()
        request!!.setSession(session!!)
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(request!!))
    }

    protected fun endRequest() {
        (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.requestCompleted()
        RequestContextHolder.resetRequestAttributes()
        request = null
    }
}
