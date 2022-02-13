package ru.service.router.services.jmx;

import com.fasterxml.jackson.core.JsonProcessingException;

import javax.management.MBeanException;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import java.io.UnsupportedEncodingException;

public interface RuleServiceMBean {
    String get(Long id) throws MBeanException, JsonProcessingException;

    String add(String request) throws JsonProcessingException, MBeanException;

    String update(Long id, String request) throws JsonProcessingException, MBeanException;

    String delete(Long id) throws MBeanException;

    String find(String params) throws OpenDataException, UnsupportedEncodingException, MBeanException;

    TabularData getAll() throws JsonProcessingException, MBeanException, OpenDataException;
}
