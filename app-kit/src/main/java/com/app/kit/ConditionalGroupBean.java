package com.app.kit;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.group.GroupSequenceProvider;

/**
 * @author qiangt
 * @since 2023/7/5 11:25
 */
@Setter
@Getter
@GroupSequenceProvider(ConditionalGroupSequenceProvider.class)
public class ConditionalGroupBean {

    private Integer type;

    public interface ConditionalGroupOne {
    }

    public interface ConditionalGroupTwo {
    }

}
