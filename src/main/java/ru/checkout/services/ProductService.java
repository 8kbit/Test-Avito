package ru.checkout.services;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.checkout.domain.Product;
import ru.checkout.utils.ServiceResponse;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

@Transactional
@Service
public class ProductService {

    @Inject
    private SessionFactory sessionFactory;

    @Transactional(readOnly = true)
    public ServiceResponse index(int limit, int offset, BigDecimal priceMin, BigDecimal priceMax, String namePart) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Product.class);
        if (priceMin != null) criteria.add(Restrictions.ge("price", priceMin));
        if (priceMax != null) criteria.add(Restrictions.le("price", priceMax));
        if (namePart != null) criteria.add(Restrictions.ilike("name", namePart, MatchMode.ANYWHERE));

        int total = ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
        List<Product> products = criteria.setProjection(null).setResultTransformer(Criteria.ROOT_ENTITY)
                .setMaxResults(limit).setFirstResult(offset).list();

        return new ServiceResponse(products, new ServiceResponse.Metadata(total));
    }

    @Transactional(readOnly = true)
    public ServiceResponse showByVendorCode(String vendorCode) {
        Product product = (Product) sessionFactory.getCurrentSession().createCriteria(Product.class)
                .add(Restrictions.ilike("vendorCode", vendorCode)).uniqueResult();
        return new ServiceResponse(product, null);
    }

    @Transactional(readOnly = true)
    public ServiceResponse show(Long id) {
        Product product = (Product) sessionFactory.getCurrentSession().get(Product.class, id);
        return new ServiceResponse(product, null);
    }

    public ServiceResponse create(Product product) {
        sessionFactory.getCurrentSession().save(product);
        return new ServiceResponse(product, null);
    }

    public ServiceResponse update(Product product) {
        product = (Product) sessionFactory.getCurrentSession().merge(product);
        return new ServiceResponse(product, null);
    }

    public boolean delete(Long id) {
        Product product = (Product) sessionFactory.getCurrentSession().get(Product.class, id);
        if (product != null) {
            sessionFactory.getCurrentSession().delete(product);
            return true;
        } else return false;
    }
}
