/**
 * 日    期：12-2-11
 */
package com.rop.impl;

import com.rop.RequestContext;
import com.rop.RopResponse;
import com.rop.ServiceMethodAdapter;
import com.rop.ServiceMethodHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <pre>
 *    通过该服务方法适配器调用目标的服务方法
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class AnnotationServiceMethodAdapter implements ServiceMethodAdapter {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 调用ROP服务方法
     *
     * @param methodContext
     * @return
     */
    public RopResponse invokeServiceMethod(RequestContext methodContext) {
        try {
            //分析上下文中的错误
            ServiceMethodHandler serviceMethodHandler = methodContext.getServiceMethodHandler();
            if (logger.isDebugEnabled()) {
                logger.debug("执行" + serviceMethodHandler.getHandler().getClass() +
                        "." + serviceMethodHandler.getHandlerMethod().getName());
            }
            if (serviceMethodHandler.isHandlerMethodWithParameter()) {
                return (RopResponse) serviceMethodHandler.getHandlerMethod().invoke(
                        serviceMethodHandler.getHandler(), methodContext.getRopRequest());
            } else {
                return (RopResponse) serviceMethodHandler.getHandlerMethod().invoke(serviceMethodHandler.getHandler());
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}

