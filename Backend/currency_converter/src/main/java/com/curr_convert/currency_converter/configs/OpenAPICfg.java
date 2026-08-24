package com.curr_convert.currency_converter.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPICfg {

    public static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI currencyConverterOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Currency Converter API")
                        .version("0.0.1-SNAPSHOT")
                        .description("""
                                Converts amounts between currencies using live rates from an external \
                                exchange-rate provider. Rates are cached in Redis with a short TTL so \
                                repeated conversions of the same pair do not hit the provider again.

                                Every user has their own account, and the conversions they run are kept \
                                as their most recent frequents.

                                **Authentication:** call `POST /user/login`, take the JWT returned in the \
                                `Token` response header and send it back as `Authorization: Bearer <token>` \
                                on every other endpoint. Tokens are valid for one hour."""))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT handed out by /user/login in the Token response header.")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}
