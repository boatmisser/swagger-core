package io.swagger.v3.jaxrs2.integration;

import io.swagger.v3.jaxrs2.Reader;
import io.swagger.v3.oas.integration.GenericOpenApiContext;
import io.swagger.v3.oas.integration.ResourceFilter;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class IntegrationTest {

    @Test(description = "scan a simple resource")
    public void shouldScanWithNewInitialization() throws Exception {
        Set expectedKeys = new HashSet<String>(Arrays.asList("/packageA", "/packageB"));
        SwaggerConfiguration config = new SwaggerConfiguration()
                .resourcePackages(Stream.of("com.my.project.resources", "org.my.project.resources").collect(Collectors.toSet()))
                .openAPI(new OpenAPI().info(new Info().description("TEST INFO DESC")));
        OpenApiContext ctx = new GenericOpenApiContext()
                .openApiConfiguration(config)
                .openApiReader(new Reader(config))
                .openApiScanner(new JaxrsApplicationAndAnnotationScanner().openApiConfiguration(config))
                .init();

        OpenAPI openApi = ctx.read();

        assertNotNull(openApi);
        assertEquals(openApi.getPaths().keySet(), expectedKeys);

        try {
            String configPath = "/integration/openapi-configuration.json";
            //ctx = new XmlWebOpenApiContext().openApiConfiguration(config).init();
            //ctx = new XmlWebOpenApiContext().configLocation(url.getPath()).init();
            ctx = new XmlWebOpenApiContext().configLocation(configPath).init();
            openApi = ctx.read();

            assertNotNull(openApi);
            assertEquals(openApi.getPaths().keySet(), expectedKeys);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(description = "scan a simple resource with an inclusion filter")
    public void shouldScanWithInclusionFilter() throws Exception {
        Set expectedKeys = new HashSet<String>(Arrays.asList("/packageB"));
        SwaggerConfiguration config = new SwaggerConfiguration()
                .resourcePackages(Stream.of("com.my.project.resources", "org.my.project.resources").collect(Collectors.toSet()))
                .resourceFilter(new ResourceFilter().includePackage("org.my.project.resources"))
                .openAPI(new OpenAPI().info(new Info().description("TEST INFO DESC")));
        OpenApiContext ctx = new GenericOpenApiContext()
                .openApiConfiguration(config)
                .openApiReader(new Reader(config))
                .openApiScanner(new JaxrsApplicationAndAnnotationScanner().openApiConfiguration(config))
                .init();

        OpenAPI openApi = ctx.read();

        assertNotNull(openApi);
        assertEquals(openApi.getPaths().keySet(), expectedKeys);
    }

    @Test(description = "scan a simple resource with an exclusion filter")
    public void shouldScanWithExclusionFilter() throws Exception {
        Set expectedKeys = new HashSet<String>(Arrays.asList("/packageA"));
        SwaggerConfiguration config = new SwaggerConfiguration()
                .resourcePackages(Stream.of("com.my.project.resources", "org.my.project.resources").collect(Collectors.toSet()))
                .resourceFilter(new ResourceFilter().excludePackage("org.my.project.resources"))
                .openAPI(new OpenAPI().info(new Info().description("TEST INFO DESC")));
        OpenApiContext ctx = new GenericOpenApiContext()
                .openApiConfiguration(config)
                .openApiReader(new Reader(config))
                .openApiScanner(new JaxrsApplicationAndAnnotationScanner().openApiConfiguration(config))
                .init();

        OpenAPI openApi = ctx.read();

        assertNotNull(openApi);
        assertEquals(openApi.getPaths().keySet(), expectedKeys);
    }

}