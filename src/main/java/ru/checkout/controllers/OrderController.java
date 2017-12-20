package ru.checkout.controllers;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import ru.checkout.domain.Order;
import ru.checkout.domain.Role;
import ru.checkout.services.OrderService;
import ru.checkout.utils.BindingHelper;
import ru.checkout.utils.ServiceResponse;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/orders",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    @Named(value = "mvcValidator")
    @Inject
    private Validator validator;

    @Named(value = "orderValidator")
    @Inject
    private Validator orderValidator;

    @Inject
    private OrderService orderService;

    @Inject
    private ConversionService conversionService;

    @PreAuthorize("(#creationDateStart!=null or #creationDateEnd!=null or #productVendorCode!=null)?hasRole('" +
            Role.ROLE_SEARCH_ORDERS + "'):hasRole('" + Role.ROLE_VIEW_ORDERS + "')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity index(@RequestParam Integer limit, @RequestParam Integer offset,
                                @RequestParam(required = false) Date creationDateStart,
                                @RequestParam(required = false) Date creationDateEnd,
                                @RequestParam(required = false) String productVendorCode) {
        ServiceResponse serviceResult = orderService.index(limit, offset, creationDateStart, creationDateEnd, productVendorCode);

        Map result = new HashMap();
        result.put("total", serviceResult.getMetadata().getTotalCount());
        result.put("rows", serviceResult.getData());
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @Secured({Role.ROLE_AUTHORISED_USER})
    @RequestMapping(value = "/showByNumber", method = RequestMethod.GET)
    public ResponseEntity showByNumber(@RequestParam String number) {
        ServiceResponse serviceResult = orderService.showByNumber(number);
        if (serviceResult.getData() != null) return new ResponseEntity(serviceResult.getData(), HttpStatus.OK);
        else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @Secured({Role.ROLE_ADMIN})
    @Transactional
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody Map orderMap) throws IOException {
        Order order = new Order();
        BindingResult errors = BindingHelper.bindMapToDomain(order, orderMap, conversionService, validator, orderValidator);
        if (errors.hasErrors())
            return new ResponseEntity(new BindingHelper.CustomErrors(errors.getFieldErrors()), HttpStatus.BAD_REQUEST);

        ServiceResponse serviceResult = orderService.create(order);
        return new ResponseEntity(serviceResult.getData(), HttpStatus.CREATED);
    }

    @Secured({Role.ROLE_ADMIN})
    @Transactional
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable Long id, @RequestBody Map orderMap) throws IOException {
        Order order = (Order) orderService.show(id).getData();
        if (order == null) return new ResponseEntity(HttpStatus.NOT_FOUND);

        BindingResult errors = BindingHelper.bindMapToDomain(order, orderMap, conversionService, validator, orderValidator);
        if (errors.hasErrors())
            return new ResponseEntity(new BindingHelper.CustomErrors(errors.getFieldErrors()), HttpStatus.BAD_REQUEST);
        order = (Order) orderService.update(order).getData();
        return new ResponseEntity(order, HttpStatus.OK);
    }

    @Secured({Role.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        if (orderService.delete(id))
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}