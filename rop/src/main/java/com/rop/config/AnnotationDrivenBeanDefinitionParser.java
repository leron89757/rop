/**
 * 版权声明：中图一购网络科技有限公司 版权所有 违者必究 2012 
 * 日    期：12-6-4
 */
package com.rop.config;

import com.rop.impl.AnnotationServletServiceRouterFactoryBean;
import com.rop.validation.DefaultRopValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * <pre>
 * 功能说明：
 * </pre>
 *
 * @author 陈雄华
 * @version 1.0
 */
public class AnnotationDrivenBeanDefinitionParser implements BeanDefinitionParser {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Object source = parserContext.extractSource(element);
        CompositeComponentDefinition compDefinition = new CompositeComponentDefinition(element.getTagName(), source);
        parserContext.pushContainingComponent(compDefinition);


        //注册ServiceRouter Bean
        RootBeanDefinition serviceRouterDef = new RootBeanDefinition(AnnotationServletServiceRouterFactoryBean.class);
        String serviceRouterName = parserContext.getReaderContext().registerWithGeneratedName(serviceRouterDef);
        parserContext.registerComponent(new BeanComponentDefinition(serviceRouterDef, serviceRouterName));

        //设置formattingConversionService
        RuntimeBeanReference conversionServiceRbf = getConversionService(element, source, parserContext);
        serviceRouterDef.getPropertyValues().add("formattingConversionService", conversionServiceRbf);

        //设置RopValidator
        RuntimeBeanReference ropValidator = getRopValidator(element, source, parserContext);
        serviceRouterDef.getPropertyValues().add("ropValidator", ropValidator);

        //设置TaskExecutor
        RuntimeBeanReference taskExecutorBeanReference = getExecutor(element, source, parserContext);
        serviceRouterDef.getPropertyValues().add("threadPoolExecutor", taskExecutorBeanReference);

        //设置signEnable
        String signEnable = element.getAttribute("sign-enable");
        if (StringUtils.hasText(signEnable)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("signEnable", Boolean.valueOf(signEnable));
        }

        //设置signEnable
        String extErrorBasename = element.getAttribute("ext-error-base-name");
        if (StringUtils.hasText(extErrorBasename)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("extErrorBasename", extErrorBasename);
        }

        String serviceTimeoutSeconds = element.getAttribute("service-timeout-seconds");
        if (StringUtils.hasText(serviceTimeoutSeconds)) {
            serviceRouterDef.getPropertyValues().addPropertyValue("serviceTimeoutSeconds", Integer.parseInt(serviceTimeoutSeconds));
        }

        parserContext.popAndRegisterContainingComponent();
        return null;
    }

    private RuntimeBeanReference getConversionService(Element element, Object source, ParserContext parserContext) {
        RuntimeBeanReference conversionServiceRbf;
        if (element.hasAttribute("formatting-conversion-service")) {
            conversionServiceRbf = new RuntimeBeanReference(element.getAttribute("formatting-conversion-service"));
            if (logger.isInfoEnabled()) {
                logger.info("Rop装配一个自定义的FormattingConversionService:" + conversionServiceRbf.getBeanName());
            }
        } else {
            RootBeanDefinition conversionDef = new RootBeanDefinition(FormattingConversionServiceFactoryBean.class);
            conversionDef.setSource(source);
            conversionDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            String conversionName = parserContext.getReaderContext().registerWithGeneratedName(conversionDef);
            parserContext.registerComponent(new BeanComponentDefinition(conversionDef, conversionName));
            conversionServiceRbf = new RuntimeBeanReference(conversionName);
        }
        return conversionServiceRbf;
    }

    private RuntimeBeanReference getRopValidator(Element element, Object source, ParserContext parserContext) {
        RuntimeBeanReference ropValidatorRbf;
        if (element.hasAttribute("rop-validator")) {
            ropValidatorRbf = new RuntimeBeanReference(element.getAttribute("rop-validator"));
            if (logger.isInfoEnabled()) {
                logger.info("Rop装配一个自定义的RopValidator:" + ropValidatorRbf.getBeanName());
            }
        } else {
            RootBeanDefinition conversionDef = new RootBeanDefinition(DefaultRopValidator.class);
            conversionDef.setSource(source);
            conversionDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
            String conversionName = parserContext.getReaderContext().registerWithGeneratedName(conversionDef);
            parserContext.registerComponent(new BeanComponentDefinition(conversionDef, conversionName));
            ropValidatorRbf = new RuntimeBeanReference(conversionName);
        }
        return ropValidatorRbf;
    }

    /**
     * 创建异步执行器
     *
     * @param element
     * @param source
     * @param parserContext
     * @return
     */
    private RuntimeBeanReference getExecutor(Element element, Object source, ParserContext parserContext) {
        RootBeanDefinition taskExecutorDef =
                new RootBeanDefinition(org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean.class);
        String taskExecutorName = parserContext.getReaderContext().registerWithGeneratedName(taskExecutorDef);

        String corePoolSize = element.getAttribute("core-pool-size");
        if (StringUtils.hasText(corePoolSize)) {
            taskExecutorDef.getPropertyValues().addPropertyValue("corePoolSize", corePoolSize);
        }

        String maxPoolSize = element.getAttribute("max-pool-size");
        if (StringUtils.hasText(maxPoolSize)) {
            taskExecutorDef.getPropertyValues().addPropertyValue("maxPoolSize", maxPoolSize);
        }

        String keepAliveSeconds = element.getAttribute("keep-alive-seconds");
        if (StringUtils.hasText(keepAliveSeconds)) {
            taskExecutorDef.getPropertyValues().addPropertyValue("keepAliveSeconds", keepAliveSeconds);
        } else {
            taskExecutorDef.getPropertyValues().addPropertyValue("keepAliveSeconds", 2 * 60); //默认为2分钟
        }

        String queueCapacity = element.getAttribute("queue-capacity");
        if (StringUtils.hasText(queueCapacity)) {
            taskExecutorDef.getPropertyValues().addPropertyValue("queueCapacity", queueCapacity);
        }

        parserContext.registerComponent(new BeanComponentDefinition(taskExecutorDef, taskExecutorName));
        RuntimeBeanReference taskExecutorBeanReference = new RuntimeBeanReference(taskExecutorName);

        return taskExecutorBeanReference;
    }
}

