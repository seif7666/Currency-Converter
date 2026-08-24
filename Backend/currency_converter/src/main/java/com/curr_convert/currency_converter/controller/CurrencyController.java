package com.curr_convert.currency_converter.controller;

import com.curr_convert.currency_converter.configs.SecurityCfg;
import com.curr_convert.currency_converter.dto.CurrencyPair;
import com.curr_convert.currency_converter.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Currencies", description = "Live currency listing and conversion. Requires a JWT.")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @Operation(
            summary = "List the available currencies",
            description = "Returns every currency code the external provider supports. The list is served " +
                    "from the cache when it is warm, and fetched from the provider otherwise.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Currency codes, e.g. [\"AED\",\"AFN\",\"USD\"]",
                    content = @Content(schema = @Schema(implementation = String[].class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content()),
            @ApiResponse(responseCode = "510", description = "Token Expired!", content = @Content())
    })
    @GetMapping("/")
    public List<String> listAvailableCurrencies(){
        return currencyService.getCurrenciesLst();
    }

    @Operation(
            summary = "Convert an amount between two currencies",
            description = "Converts `amount` from one currency to another. The rate for the pair is taken " +
                    "from the cache while it is still fresh, otherwise it is fetched from the provider and " +
                    "cached. The conversion is also recorded as one of the caller's frequents.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The converted amount",
                    content = @Content(schema = @Schema(implementation = Double.class, example = "84.31"))),
            @ApiResponse(responseCode = "400", description = "A parameter is missing or not a number",
                    content = @Content()),
            @ApiResponse(responseCode = "401", description = "Missing or invalid JWT", content = @Content()),
            @ApiResponse(responseCode = "510", description = "Token Expired!", content = @Content())
    })
    @GetMapping("/compute")
    public double computeAmount(
            @Parameter(description = "Currency code to convert from", example = "USD", required = true)
            @RequestParam String from,
            @Parameter(description = "Currency code to convert to", example = "EUR", required = true)
            @RequestParam String to,
            @Parameter(description = "Amount expressed in the `from` currency", example = "100", required = true)
            @RequestParam double amount ){
        CurrencyPair pair= new CurrencyPair(from,to);
        return currencyService.computeAmount(pair,amount);
    }
}
