package ru.checkout.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import ru.checkout.domain.Product;
import ru.checkout.domain.Role;
import ru.checkout.services.ProductService;
import ru.checkout.utils.BindingHelper;
import ru.checkout.utils.ServiceResponse;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/products",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

    @Named(value = "mvcValidator")
    @Inject
    private Validator validator;

    @Named(value = "productValidator")
    @Inject
    private Validator productValidator;

    @Inject
    private ProductService productService;

    @Inject
    private ConversionService conversionService;

    @Secured({Role.ROLE_AUTHORISED_USER})
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity index(@RequestParam Integer limit, @RequestParam Integer offset,
                                @RequestParam(required = false) BigDecimal priceMin,
                                @RequestParam(required = false) BigDecimal priceMax,
                                @RequestParam(required = false) String namePart)
            throws JsonProcessingException {
        ServiceResponse serviceResult = productService.index(limit, offset, priceMin, priceMax, namePart);

        Map result = new HashMap();
        result.put("total", serviceResult.getMetadata().getTotalCount());
        result.put("rows", serviceResult.getData());
        return new ResponseEntity(new ObjectMapper().writeValueAsString(result), HttpStatus.OK);
    }

    @Secured({Role.ROLE_AUTHORISED_USER})
    @RequestMapping(value = "/showByVendorCode", method = RequestMethod.GET)
    public ResponseEntity showByNumber(@RequestParam String vendorCode) {
        ServiceResponse serviceResult = productService.showByVendorCode(vendorCode);
        if (serviceResult.getData() != null) return new ResponseEntity(serviceResult.getData(), HttpStatus.OK);
        else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @Secured({Role.ROLE_ADMIN})
    @Transactional
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody Map productMap) throws IOException {
        Product product = new Product();
        BindingResult errors = BindingHelper.bindMapToDomain(product, productMap, conversionService, validator, productValidator);
        if (errors.hasErrors())
            return new ResponseEntity(new BindingHelper.CustomErrors(errors.getFieldErrors()), HttpStatus.BAD_REQUEST);

        ServiceResponse serviceResult = productService.create(product);
        return new ResponseEntity(serviceResult.getData(), HttpStatus.CREATED);
    }

    @Secured({Role.ROLE_ADMIN})
    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable Long id, @RequestBody Map productMap) throws IOException {
        Product product = (Product) productService.show(id).getData();
        if (product == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        BindingResult errors = BindingHelper.bindMapToDomain(product, productMap, conversionService, validator, productValidator);
        if (errors.hasErrors())
            return new ResponseEntity(new BindingHelper.CustomErrors(errors.getFieldErrors()), HttpStatus.BAD_REQUEST);
        product = (Product) productService.update(product).getData();
        return new ResponseEntity(product, HttpStatus.OK);
    }

    @Secured({Role.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable Long id) {
        if (productService.delete(id))
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}