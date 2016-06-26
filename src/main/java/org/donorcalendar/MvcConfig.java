package org.donorcalendar;

import org.dozer.DozerBeanMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Bean(name = "org.dozer.Mapper")
    public DozerBeanMapper dozerBean() {
//        List<String> mappingFiles = Arrays.asList(
//                "dozer-global-configuration.xml",
//                "dozer-bean-mappings.xml",
//                "more-dozer-bean-mappings.xml"
//        );

        DozerBeanMapper dozerBean = new DozerBeanMapper();
//        dozerBean.setMappingFiles(mappingFiles);
        return dozerBean;
    }

}