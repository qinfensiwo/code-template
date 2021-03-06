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
 * @author ???Mr.Wang
 * @date ???Created in 2021/1/27 17:16\
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
            //??????????????????
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
            //??????????????????
            urlMap.put(DEFAULT_PATH + "/**", resourceHttpRequestHandler);
        }
        urlHandlerMapping.setUrlMap(urlMap);
        //??????DispatcherServlet??????SimpleUrlHandlerMapping?????????
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
        //???????????????swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                //???????????????????????????apiInfo??????????????????????????????
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.changgou.goods"))//?????????????????????API??????
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());

    }

    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder()
                .title("?????????????????? API ????????????").description("??????????????????API ????????????").version("1.0.0").build();
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
     * ??????API??????
     * apiInfo() ??????API????????????
     * ??????select()??????????????????ApiSelectorBuilder??????,?????????????????????????????????Swagger????????????
     * ????????????????????????????????????????????????????????????API????????????
     *
     * @return
     */
  /* @Bean
    public Docket createRestApi(){
        //???????????????swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                //???????????????????????????apiInfo??????????????????????????????
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.changgou.goods"))//?????????????????????API??????
                .paths(PathSelectors.any())
                .build();
    }
    *//**
     * ?????????API??????????????????????????????????????????????????????????????????
     * ???????????????http://??????????????????/swagger-ui.html
     * @return
     *//*
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("??????Swagger2") //??????????????????????????????
                .description("Swagger????????????")//API?????????
//                .termsOfServiceUrl("www.footmark.top")//??????url???
                .version("1.0")
                .build();
    }*/
}