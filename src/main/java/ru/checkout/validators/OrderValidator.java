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
import ru.checkout.domain.Order;

import javax.inject.Inject;

@Component(value = "orderValidator")
public class OrderValidator implements Validator {
    private final static String UNIQUE_CONSTRAINT_NAME = "ru.checkout.validator.constraints.Unique.message";

    @Inject
    private MessageSource messageSource;

    @Inject
    private SessionFactory sessionFactory;

    public boolean supports(Class<?> clazz) {
        return Order.class.equals(clazz);
    }

    public void validate(Object target_, Errors errors) {
        Order target = (Order) target_;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Order.class)
                .add(Restrictions.ilike("number", target.getNumber())).add(Restrictions.neOrIsNotNull("id", target.getId()))
                .setProjection(Projections.rowCount());
        if ((Long) criteria.uniqueResult() > 0)
            errors.rejectValue("number", UNIQUE_CONSTRAINT_NAME, null, messageSource.getMessage(UNIQUE_CONSTRAINT_NAME,
                    null, LocaleContextHolder.getLocale()));
    }
}