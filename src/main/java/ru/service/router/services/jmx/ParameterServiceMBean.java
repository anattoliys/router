package ru.service.router.services.jmx;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.management.MBeanException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;

public interface ParameterServiceMBean {
    CompositeData get(Long id) throws OpenDataException, MBeanException;

    String add(String name, String type, String description, Integer rank) throws JsonProcessingException;

    String update(Long id, String name, String type, String description, Integer rank) throws JsonProcessingException;

    String delete(Long id) throws MBeanException;

    TabularData getAll() throws OpenDataException;
}
