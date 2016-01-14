
package com.fengjx.modules.sys.controller.admin.interceptor;

import com.fengjx.commons.plugin.db.Record;
import com.fengjx.commons.utils.WebUtil;
import com.fengjx.modules.sys.model.SysMenu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminInterceptor implements HandlerInterceptor {

    /**
     * 管理基础路径
     */
    @Value("${adminPath}")
    protected String adminPath;

    @Autowired
    private SysMenu sysMenu;

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception e) throws Exception {

    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler,
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
        if (!WebUtil.isAjax(request)) {
            String url = StringUtils.replace(request.getRequestURI(), request.getContextPath(), "");
            Record menu = sysMenu.findByUrl(url);
            if (!menu.isEmpty()) {
                String pid = menu.getStr("id");
                if (StringUtils.isNotBlank(menu.getStr("parent_ids"))) {
                    pid = StringUtils.split(menu.getStr("parent_ids"), ",")[0];
                } else if (StringUtils.isNotBlank(menu.getStr("parent_id"))) {
                    pid = menu.getStr("parent_id");
                }
                request.setAttribute("admin_menu_pid", pid);
            }
        }
        return true;
    }
}
