package tk.bad_rabbit.rcam.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan({"tk.bad_rabbit.rcam.spring.*", 
  "tk.bad_rabbit.rcam.distributed_backend.*",
  "org.springframework.context.annotation.CommonAnnotationBeanPostProcessor"})
public class DefaultCoordinatorSpringConfig extends WebMvcConfigurerAdapter {
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
      .addResourceLocations("/");
  }
  
  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable("tk.bad_rabbit.rcam.spring.servlet.handlers.DefaultServletImpl");
  }
  
  @Bean
  public InternalResourceViewResolver jspViewResolver() {
    InternalResourceViewResolver bean = new InternalResourceViewResolver();
    bean.setPrefix("/WEB-INF/views/");
    bean.setSuffix(".jsp");
    return bean;
  }
  

  //@Bean(name = "multipartResolver")
  //public CommonsMultipartResolver getMultipartResolver() {
  //  return new CommonsMultipartResolver();
  //}
  
  @Bean(name = "messageSource")
  public ReloadableResourceBundleMessageSource getMessageSource() {
    ReloadableResourceBundleMessageSource resource = new ReloadableResourceBundleMessageSource();
    resource.setDefaultEncoding("UTF-8");
    return resource;
  }
}
