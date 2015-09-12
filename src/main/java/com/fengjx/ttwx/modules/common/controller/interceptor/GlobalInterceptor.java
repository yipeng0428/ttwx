
package com.fengjx.ttwx.modules.common.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 分页拦截器
 */
public class GlobalInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalInterceptor.class);

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception e) throws Exception {

    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView arg3) throws Exception {

    }

    /**
     * 如果返回false 从当前拦截器往回执行所有拦截器的afterCompletion方法，再退回拦截器链 如果返回true
     * 执行下一个拦截器，直到所有拦截器都执行完毕 再运行被拦截的Controller
     * 然后进入拦截器链从最后一个拦截器往回运行所有拦截器的postHandle方法
     * 接着依旧是从最后一个拦截器往回执行所有拦截器的afterCompletion方法
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        HandlerMethod handlerMethod = null;
        if (handler instanceof HandlerMethod) {
            handlerMethod = (HandlerMethod) handler;
        }
        handlerMethod.getMethod();



        return true;
    }

}
