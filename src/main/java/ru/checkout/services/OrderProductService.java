package ru.checkout.services;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.checkout.domain.OrderProduct;
import ru.checkout.utils.ServiceResponse;

import javax.inject.Inject;

@Transactional
@Service
public class OrderProductService {

    @Inject
    private SessionFactory sessionFactory;

    public ServiceResponse create(OrderProduct orderProduct) {
        sessionFactory.getCurrentSession().save(orderProduct);
        return new ServiceResponse(orderProduct, null);
    }

    public boolean delete(Long id) {
        OrderProduct orderProduct = (OrderProduct) sessionFactory.getCurrentSession().get(OrderProduct.class, id);
        if (orderProduct != null) {
            sessionFactory.getCurrentSession().delete(orderProduct);
            return true;
        } else return false;
    }
}
