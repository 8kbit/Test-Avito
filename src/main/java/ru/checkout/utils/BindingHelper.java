package ru.checkout.utils;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BindingHelper {
    public static BindingResult bindMapToDomain(Object domain, Map properties, ConversionService conversionService, Validator... validators) {
        DataBinder binder = new DataBinder(domain);
        binder.setConversionService(conversionService);
        binder.addValidators(validators);
        binder.bind(new MutablePropertyValues(properties));
        binder.validate();
        return binder.getBindingResult();
    }

    public static class CustomErrors {
        private final Map errors;

        public Map getErrors() {
            return errors;
        }

        public CustomErrors(List<FieldError> fieldErrors) {
            Map<String, String> map = new HashMap();
            for (FieldError fieldError : fieldErrors)
                map.put(fieldError.getField(), fieldError.getDefaultMessage());
            errors = Collections.unmodifiableMap(map);
        }
    }
}
