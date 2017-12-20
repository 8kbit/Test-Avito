package ru.checkout.validators;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.checkout.domain.Product;

import javax.inject.Inject;

@Component(value = "productValidator")
public class ProductValidator implements Validator {
    private final static String UNIQUE_CONSTRAINT_NAME = "ru.checkout.validator.constraints.Unique.message";

    @Inject
    private MessageSource messageSource;

    @Inject
    private SessionFactory sessionFactory;

    public boolean supports(Class<?> clazz) {
        return Product.class.equals(clazz);
    }

    public void validate(Object target_, Errors errors) {
        Product target = (Product) target_;
        Product conflictProduct = (Product) sessionFactory.getCurrentSession().createCriteria(Product.class)
                .add(Restrictions.or(Restrictions.ilike("vendorCode", target.getVendorCode()),
                        Restrictions.ilike("name", target.getName())))
                .add(Restrictions.neOrIsNotNull("id", target.getId()))
                .setProjection(Projections.projectionList()
                        .add(Projections.property("vendorCode"), "vendorCode")
                        .add(Projections.property("name"), "name"))
                .setResultTransformer(Transformers.aliasToBean(Product.class)).uniqueResult();
        if (conflictProduct != null)
            if (conflictProduct.getVendorCode().equals(target.getVendorCode()))
                errors.rejectValue("vendorCode", UNIQUE_CONSTRAINT_NAME, null, messageSource.getMessage(UNIQUE_CONSTRAINT_NAME,
                        null, LocaleContextHolder.getLocale()));
            else
                errors.rejectValue("name", UNIQUE_CONSTRAINT_NAME, null, messageSource.getMessage(UNIQUE_CONSTRAINT_NAME,
                        null, LocaleContextHolder.getLocale()));
    }
}