package com.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "creator")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Creator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "creator_type")
    private CreatorType creatorType;

    protected Creator() {

    }

    protected Creator(int id) {
        this.id = id;
    }

    protected Creator(Builder<?> builder) {
        this.id = builder.id;
        this.creatorType = builder.creatorType;
    }

    protected static abstract class Builder<T extends Builder<T>> {

        private Integer id;

        private CreatorType creatorType;

        protected Builder() {

        }

        protected Builder(Creator creator) {
            this.id = creator.id;
            this.creatorType = creator.creatorType;
        }

        @JsonProperty("id")
        public T id(Integer id) {
            this.id = id;
            return self();
        }

        public void creatorType(CreatorType creatorType) {
            this.creatorType = creatorType;
        }

        protected abstract T self();

        protected abstract Creator build();

    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonIgnore
    public CreatorType getCreatorType() {
        return creatorType;
    }
}
