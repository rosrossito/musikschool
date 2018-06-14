/*
 * This file is generated by jOOQ.
*/
package com.epam.charity.jooq.dto.tables.records;


import com.epam.charity.jooq.dto.tables.ApplicationsAddress;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ApplicationsAddressRecord extends UpdatableRecordImpl<ApplicationsAddressRecord> implements Record6<Long, String, String, String, String, String> {

    private static final long serialVersionUID = -991845542;

    /**
     * Setter for <code>dev.applications_address.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>dev.applications_address.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>dev.applications_address.country</code>.
     */
    public void setCountry(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>dev.applications_address.country</code>.
     */
    public String getCountry() {
        return (String) get(1);
    }

    /**
     * Setter for <code>dev.applications_address.city</code>.
     */
    public void setCity(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>dev.applications_address.city</code>.
     */
    public String getCity() {
        return (String) get(2);
    }

    /**
     * Setter for <code>dev.applications_address.street</code>.
     */
    public void setStreet(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>dev.applications_address.street</code>.
     */
    public String getStreet() {
        return (String) get(3);
    }

    /**
     * Setter for <code>dev.applications_address.zip</code>.
     */
    public void setZip(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>dev.applications_address.zip</code>.
     */
    public String getZip() {
        return (String) get(4);
    }

    /**
     * Setter for <code>dev.applications_address.region</code>.
     */
    public void setRegion(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>dev.applications_address.region</code>.
     */
    public String getRegion() {
        return (String) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Long, String, String, String, String, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Long, String, String, String, String, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return ApplicationsAddress.APPLICATIONS_ADDRESS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return ApplicationsAddress.APPLICATIONS_ADDRESS.COUNTRY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return ApplicationsAddress.APPLICATIONS_ADDRESS.CITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return ApplicationsAddress.APPLICATIONS_ADDRESS.STREET;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return ApplicationsAddress.APPLICATIONS_ADDRESS.ZIP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return ApplicationsAddress.APPLICATIONS_ADDRESS.REGION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getCountry();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getCity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getStreet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getZip();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getRegion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationsAddressRecord value1(Long value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationsAddressRecord value2(String value) {
        setCountry(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationsAddressRecord value3(String value) {
        setCity(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationsAddressRecord value4(String value) {
        setStreet(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationsAddressRecord value5(String value) {
        setZip(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationsAddressRecord value6(String value) {
        setRegion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationsAddressRecord values(Long value1, String value2, String value3, String value4, String value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ApplicationsAddressRecord
     */
    public ApplicationsAddressRecord() {
        super(ApplicationsAddress.APPLICATIONS_ADDRESS);
    }

    /**
     * Create a detached, initialised ApplicationsAddressRecord
     */
    public ApplicationsAddressRecord(Long id, String country, String city, String street, String zip, String region) {
        super(ApplicationsAddress.APPLICATIONS_ADDRESS);

        set(0, id);
        set(1, country);
        set(2, city);
        set(3, street);
        set(4, zip);
        set(5, region);
    }
}
