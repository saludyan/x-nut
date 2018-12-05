package nut.jpa.identifier;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class XNutIdentifierGenerator implements IdentifierGenerator {

    public XNutIdentifierGenerator(){}

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {
        return IDSequence.Instance().nextId();
    }
}
