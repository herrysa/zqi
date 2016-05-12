package com.zqi.unit;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHelper
    implements ApplicationContextAware {

    private static ApplicationContext context;

    /*
     * 注入ApplicationContext
     */
    public void setApplicationContext( ApplicationContext context )
        throws BeansException {
        //在加载Spring时自动获得context
        SpringContextHelper.context = context;
        //		System.out.println(SpringContextHelper.context);
    }

    public static Object getBean( String beanName ) {
        return context.getBean( beanName );
    }

}
