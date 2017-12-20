package ru.checkout.services;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.checkout.domain.Order;
import ru.checkout.utils.ServiceResponse;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Transactional
@Service
public class OrderService {

    @Inject
    private SessionFactory sessionFactory;

    @Transactional(readOnly = true)
    public ServiceResponse index(int limit, int offset, Date creationDateStart, Date creationDateEnd, String productVendorCode) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Order.class);
        if (creationDateStart != null) criteria.add(Restrictions.ge("creationDate", creationDateStart));
        if (creationDateEnd != null) criteria.add(Restrictions.le("creationDate", creationDateEnd));
        if (productVendorCode != null)
            criteria.createCriteria("orderProducts").createCriteria("product")
                    .add(Restrictions.ilike("vendorCode", productVendorCode));

        int total = ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
        List<Order> orders = Collections.emptyList();
        if (total > 0 && total > offset) {
            List<Long> ids = criteria.setMaxResults(limit).setFirstResult(offset).setProjection(Projections.id()).list();
            orders = sessionFactory.getCurrentSession().createCriteria(Order.class)
                    .add(Restrictions.in("id", ids)).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        }
        return new ServiceResponse(orders, new ServiceResponse.Metadata(total));
    }

    @Transactional(readOnly = true)
    public ServiceResponse showByNumber(String number) {
        Order order = (Order) sessionFactory.getCurrentSession().createCriteria(Order.class)
                .add(Restrictions.ilike("number", number)).uniqueResult();
        return new ServiceResponse(order, null);
    }

    @Transactional(readOnly = true)
    public ServiceResponse show(Long id) {
        Order order = (Order) sessionFactory.getCurrentSession().get(Order.class, id);
        return new ServiceResponse(order, null);
    }

    public ServiceResponse create(Order order) {
        sessionFactory.getCurrentSession().save(order);
        return new ServiceResponse(order, null);
    }

    public ServiceResponse update(Order order) {
        order = (Order) sessionFactory.getCurrentSession().merge(order);
        return new ServiceResponse(order, null);
    }

    public boolean delete(Long id) {
        Order order = (Order) sessionFactory.getCurrentSession().get(Order.class, id);
        if (order != null) {
            sessionFactory.getCurrentSession().delete(order);
            return true;
        } else return false;
    }
}
