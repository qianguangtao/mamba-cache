package com.app.kit;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ConditionalGroupSequenceProvider implements DefaultGroupSequenceProvider<ConditionalGroupBean> {

    @Override
    public List<Class<?>> getValidationGroups(final ConditionalGroupBean bean) {
        final List<Class<?>> defaultGroupSequence = new LinkedList<>();
        defaultGroupSequence.add(ConditionalGroupBean.class);
        if (Objects.nonNull(bean)) {
            final Integer type = bean.getType();
            if (Objects.equals(1, type)) {
                defaultGroupSequence.add(ConditionalGroupBean.ConditionalGroupOne.class);
            } else if (Objects.equals(2, type)) {
                defaultGroupSequence.add(ConditionalGroupBean.ConditionalGroupTwo.class);
            }
        }
        return defaultGroupSequence;
    }

}
