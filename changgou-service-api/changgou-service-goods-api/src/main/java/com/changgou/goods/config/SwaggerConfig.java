package com.changgou.goods.config;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.util.UrlPathHelper;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiResourceController;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;
import springfox.documentation.swagger2.web.Swagger2Controller;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：Mr.Wang
 * @date ：Created in 2021/1/27 17:16\
 */
@Data
@Component
@ConfigurationProperties(prefix = "changgou.swagger")
@Configuration
@EnableSwagger2
public class SwaggerConfig
{
    private static final String DEFAULT_PATH = "/swagger";

    private boolean enable;

    @Bean
    public SimpleUrlHandlerMapping swaggerUrlHandlerMapping(ServletContext servletContext,
                                                            @Value("${swagger.mapping.order:10}") int order) throws Exception
    {
        SimpleUrlHandlerMapping urlHandlerMapping = new SimpleUrlHandlerMapping();
        Map<String, ResourceHttpRequestHandler> urlMap = new HashMap<>();
        {
            PathResourceResolver pathResourceResolver = new PathResourceResolver();
            pathResourceResolver.setAllowedLocations(new ClassPathResource("META-INF/resources/webjars/"));
            pathResourceResolver.setUrlPathHelper(new UrlPathHelper());

            ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
            resourceHttpRequestHandler.setLocations(Collections.singletonList(new ClassPathResource("META-INF/resources/webjars/")));
            resourceHttpRequestHandler.setResourceResolvers(Collections.singletonList(pathResourceResolver));
            resourceHttpRequestHandler.setServletContext(servletContext);
            resourceHttpRequestHandler.afterPropertiesSet();
            //设置新的路径
            urlMap.put(DEFAULT_PATH + "/webjars/**", resourceHttpRequestHandler);
        }
        {
            PathResourceResolver pathResourceResolver = new PathResourceResolver();
            pathResourceResolver.setAllowedLocations(new ClassPathResource("META-INF/resources/"));
            pathResourceResolver.setUrlPathHelper(new UrlPathHelper());

            ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
            resourceHttpRequestHandler.setLocations(Collections.singletonList(new ClassPathResource("META-INF/resources/")));
            resourceHttpRequestHandler.setResourceResolvers(Collections.singletonList(pathResourceResolver));
            resourceHttpRequestHandler.setServletContext(servletContext);
            resourceHttpRequestHandler.afterPropertiesSet();
            //设置新的路径
            urlMap.put(DEFAULT_PATH + "/**", resourceHttpRequestHandler);
        }
        urlHandlerMapping.setUrlMap(urlMap);
        //调整DispatcherServlet关于SimpleUrlHandlerMapping的排序
        urlHandlerMapping.setOrder(order);
        return urlHandlerMapping;
    }

    @Controller
    @ApiIgnore
    @RequestMapping(DEFAULT_PATH)
    public static class SwaggerResourceController implements InitializingBean
    {

        private final ApiResourceController apiResourceController;

        private final Environment environment;

        private final DocumentationCache documentationCache;

        private final ServiceModelToSwagger2Mapper mapper;

        private final JsonSerializer jsonSerializer;

        private Swagger2Controller swagger2Controller;

        @Autowired
        public SwaggerResourceController(ApiResourceController apiResourceController, Environment environment, DocumentationCache documentationCache, ServiceModelToSwagger2Mapper mapper, JsonSerializer jsonSerializer)
        {
            this.apiResourceController = apiResourceController;
            this.environment = environment;
            this.documentationCache = documentationCache;
            this.mapper = mapper;
            this.jsonSerializer = jsonSerializer;
        }

        @Override
        public void afterPropertiesSet()
        {
            swagger2Controller = new Swagger2Controller(environment, documentationCache, mapper, jsonSerializer);
        }

        @GetMapping("/swagger-resources/configuration/security")
        @ResponseBody
        public ResponseEntity<SecurityConfiguration> securityConfiguration()
        {
            return apiResourceController.securityConfiguration();
        }

        @GetMapping("/swagger-resources/configuration/ui")
        @ResponseBody
        public ResponseEntity<UiConfiguration> uiConfiguration()
        {
            return apiResourceController.uiConfiguration();
        }

        @GetMapping("/swagger-resources")
        @ResponseBody
        public ResponseEntity<List<SwaggerResource>> swaggerResources()
        {
            return apiResourceController.swaggerResources();
        }

        @GetMapping(value = "/v2/api-docs", produces = {"application/json", "application/hal+json"})
        @ResponseBody
        public ResponseEntity<Json> getDocumentation(
                @RequestParam(value = "group", required = false) String swaggerGroup,
                HttpServletRequest servletRequest)
        {
            return swagger2Controller.getDocumentation(swaggerGroup, servletRequest);
        }
    }

    @Bean
    public Docket createRestApi()
    {
      /*  return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enable)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.changgou.goods"))
                .paths(PathSelectors.any()).build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());*/
        //版本类型是swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                //通过调用自定义方法apiInfo，获得文档的主要信息
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.changgou.goods"))//扫描该包下面的API注解
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());

    }

    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder()
                .title("商品管理服务 API 接口文档").description("商品管理服务API 接口文档").version("1.0.0").build();
    }

    private List<ApiKey> securitySchemes()
    {
        List<ApiKey> apiKeyList = new ArrayList();
        apiKeyList.add(new ApiKey("token", "token", "header"));
        return apiKeyList;
    }

    private List<SecurityContext> securityContexts()
    {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!login).*$"))
                        .build());
        return securityContexts;
    }

    List<SecurityReference> defaultAuth()
    {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("token", authorizationScopes));
        return securityReferences;
    }
    /**
     * 创建API应用
     * apiInfo() 增加API相关信息
     * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
     * 本例采用指定扫描的包路径来定义指定要建立API的目录。
     *
     * @return
     */
  /* @Bean
    public Docket createRestApi(){
        //版本类型是swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                //通过调用自定义方法apiInfo，获得文档的主要信息
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.changgou.goods"))//扫描该包下面的API注解
                .paths(PathSelectors.any())
                .build();
    }
    *//**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     * 访问地址：http://项目实际地址/swagger-ui.html
     * @return
     *//*
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("使用Swagger2") //接口管理文档首页显示
                .description("Swagger使用演示")//API的描述
//                .termsOfServiceUrl("www.footmark.top")//网站url等
                .version("1.0")
                .build();
    }*/
}