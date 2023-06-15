package io.choerodon.test.manager.infra.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.infra.feign.operator.RemoteIamOperator;
import org.springframework.beans.BeanUtils;

public class ConvertUtils {

    private static final Map<Long, ProjectDTO> ORGANIZATION_MAP = new ConcurrentHashMap<>();

    private ConvertUtils() {
    }

    /**
     * This is a method to convert an object to destination object.
     * It's only for the simple object that can use
     * {@link BeanUtils#copyProperties(Object, Object)} method.
     * And the destination class should have the non-parameter constructor.
     * {@link Class#newInstance()} method is used.
     *
     * @param source           the source object
     * @param destinationClass the destination class
     * @param <S>              the source type
     * @param <D>              the destination type
     * @return destination object
     */
    public static <S, D> D convertObject(S source, Class<D> destinationClass) {
        if (source == null) {
            return null;
        }
        try {
            D destination = destinationClass.newInstance();
            BeanUtils.copyProperties(source, destination);
            return destination;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CommonException("Can not instantiate the class type: {} with no-arg constructor.", destinationClass.getName());
        }
    }

    /**
     * convert page with special converter
     *
     * @param source    source page
     * @param converter converter for list content of page
     * @param <S>       the source content type
     * @param <D>       the destination content type
     * @return destination page
     */
    public static <S, D> Page<D> convertPage(Page<S> source, Function<S, D> converter) {
        if (source == null) {
            return null;
        }
        Page<D> destination = new Page<>();
        BeanUtils.copyProperties(source, destination, "list");
        if (source.getContent() != null) {
            destination.setContent(source.getContent().stream().map(converter).collect(Collectors.toList()));
        } else {
            destination.setContent(new ArrayList<>());
        }
        return destination;
    }

    /**
     * convert page with default beanUtils
     *
     * @param source source page
     * @param <S>    the source content type
     * @param <D>    the destination content type
     * @return destination page
     */
    public static <S, D> Page<D> convertPage(Page<S> source, Class<D> destinationClass) {
        if (source == null) {
            return null;
        }
        Page<D> destination = new Page<>();
        BeanUtils.copyProperties(source, destination, "list");
        if (source.getContent() != null) {
            destination.setContent(source.getContent().stream().map(s -> convertObject(s, destinationClass)).collect(Collectors.toList()));
        }
        return destination;
    }

    /**
     * convert List with default beanUtils
     *
     * @param source source page
     * @param <S>    the source content type
     * @param <D>    the destination content type
     * @return destination page
     */
    public static <S, D> List<D> convertList(List<S> source, Class<D> destinationClass) {
        if (source == null) {
            return new ArrayList<>() ;
        }
        if (source.isEmpty()) {
            return new ArrayList<>();
        }
        return source.stream().map(s -> convertObject(s, destinationClass)).collect(Collectors.toList());
    }


    /**
     * convert List with special converter
     *
     * @param source source page
     * @param <S>    the source content type
     * @param <D>    the destination content type
     * @return destination page
     */
    public static <S, D> List<D> convertList(List<S> source, Function<S, D> converter) {
        if (source == null) {
            return new ArrayList<>();
        }
        if (source.isEmpty()) {
            return new ArrayList<>();
        }
        return source.stream().map(converter).collect(Collectors.toList());
    }

    public static <T> List<List<T>> averageAssign(List<T> source,int n){
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    public static Long getOrganizationId(Long projectId) {
        return queryProject(projectId).getOrganizationId();
    }

    private static ProjectDTO queryProject(Long projectId) {
        ProjectDTO projectVO = ORGANIZATION_MAP.get(projectId);
        if (projectVO != null) {
            return projectVO;
        } else {
            projectVO = SpringBeanUtil.getBean(RemoteIamOperator.class).getProjectById(projectId);
            if (projectVO != null) {
                ORGANIZATION_MAP.put(projectId, projectVO);
                return projectVO;
            } else {
                throw new CommonException("error.queryProject.notFound");
            }
        }
    }
}
