package ru.checkout.controllers;

import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import ru.checkout.domain.OrderProduct;
import ru.checkout.domain.Role;
import ru.checkout.services.OrderProductService;
import ru.checkout.services.OrderService;
import ru.checkout.services.ProductService;
import ru.checkout.utils.BindingHelper;
import ru.checkout.utils.ServiceResponse;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = "api/orders/{orderId}/orderProducts",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderProductController {

    @Named(value = "mvcValidator")
    @Inject
    private Validator validator;

    @Named(value = "orderProductValidator")
    @Inject
    private Validator orderProductValidator;

    @Inject
    private OrderProductService orderProductService;

    @Inject
    private OrderService orderService;

    @Inject
    private ProductService productService;

    @Inject
    private ConversionService conversionService;

    @Secured({Role.ROLE_ADMIN})
    @Transactional
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@PathVariable Long orderId, @RequestBody Map orderProductMap) throws IOException {
        orderProductMap.put("order", orderService.show(orderId).getData());
        orderProductMap.put("product", productService.show(((Integer) orderProductMap.get("product")).longValue()).getData());
        OrderProduct orderProduct = new OrderProduct();
        BindingResult errors = BindingHelper.bindMapToDomain(orderProduct, orderProductMap, conversionService,
                validator, orderProductValidator);
        if (errors.hasErrors())
            return new ResponseEntity(new BindingHelper.CustomErrors(errors.getFieldErrors()), HttpStatus.BAD_REQUEST);

        ServiceResponse serviceResult = orderProductService.create(orderProduct);
        return new ResponseEntity(serviceResult.getData(), HttpStatus.CREATED);
    }

    @Secured({Role.ROLE_ADMIN})
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable Long id) {
        if (orderProductService.delete(id))
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        else return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}