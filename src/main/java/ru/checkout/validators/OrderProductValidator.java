package ru.checkout.validators;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.checkout.domain.OrderProduct;

import javax.inject.Inject;

@Component(value = "orderProductValidator")
public class OrderProductValidator implements Validator {
    private final static String UNIQUE_CONSTRAINT_NAME = "ru.checkout.validator.constraints.Unique.message";

    @Inject
    private MessageSource messageSource;

    @Inject
    private SessionFactory sessionFactory;

    public boolean supports(Class<?> clazz) {
        return OrderProduct.class.equals(clazz);
    }

    public void validate(Object target_, Errors errors) {
        OrderProduct target = (OrderProduct) target_;
        if (target.getOrder() != null && target.getProduct() != null) {
            Criteria criteria = sessionFactory.getCurrentSession().createCriteria(OrderProduct.class)
                    .add(Restrictions.neOrIsNotNull("id", target.getId()))
                    .setProjection(Projections.rowCount());
            criteria.createCriteria("order").add(Restrictions.idEq(target.getOrder().getId()));
            criteria.createCriteria("product").add(Restrictions.idEq(target.getProduct().getId()));
            if (((Long) criteria.uniqueResult()) > 0) {
                errors.rejectValue("order", UNIQUE_CONSTRAINT_NAME, null, messageSource.getMessage(UNIQUE_CONSTRAINT_NAME,
                        null, LocaleContextHolder.getLocale()));
                errors.rejectValue("product", UNIQUE_CONSTRAINT_NAME, null, messageSource.getMessage(UNIQUE_CONSTRAINT_NAME,
                        null, LocaleContextHolder.getLocale()));
            }
        }
    }
}